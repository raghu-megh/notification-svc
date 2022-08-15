package com.five9.notification.subscriber;


import com.five9.notification.entity.Recording;
import com.five9.notification.model.EventType;
import com.five9.notification.model.RecordingUploadEvent;
import com.five9.notification.service.RecordingService;
import com.five9.pubsub.MessageReceiver;
import com.five9.pubsub.MessageReceiverCallback;
import com.five9.pubsub.spring.EventBusSubscriber;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.tasks.v2beta3.*;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.PubsubMessage;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@EventBusSubscriber(
                topic = "${events.ingress.topic}",
                subscription = "${events.ingress.subscription}")
@Slf4j
@AllArgsConstructor
@Service
public class Ingress implements MessageReceiver<RecordingUploadEvent> {

    private final String queuePath;
    private final Duration checkStatusInterval;
    private final Duration checkUploadLinkInterval;
    private final RecordingService recordingService;
    private final String saEmail;
    private final String publishUrl;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(RecordingUploadEvent event, MessageReceiverCallback callback) {
        log.info("Received event Type={} for recording={} and domain={}", event.getEventType(), event.getRecordingId(),
                        event.getDomainId());
        try {
            switch (event.getEventType()) {
                case START:
                    insert(event);
                    createDelayedTaskUsing(event.withEventType(EventType.CHECK_STATUS),
                                    checkStatusInterval.getSeconds());
                    createDelayedTaskUsing(event.withEventType(EventType.CHECK_UPLOAD_LINK),
                                    checkUploadLinkInterval.getSeconds());
                    break;

                case SUCCESS:
                    update(event);
                    break;

                case CHECK_UPLOAD_LINK:
                    recordingService.findByDomainIdAndRecordingId(event.getDomainId(), event.getRecordingId())
                                    .ifPresent(recording -> {
                                        java.sql.Timestamp queuedTime = recording.getQueuedTimestamp();
                                        java.sql.Timestamp start = new java.sql.Timestamp(
                                                        queuedTime.getTime() - checkUploadLinkInterval.toMillis());
                                        recordingService.findRecordingsUsing(event.getDomainId(), start, queuedTime,
                                                        true)
                                                        .ifPresent(recordings -> {
                                                            if (recordings.isEmpty()) {
                                                                log.info("****NOTIFICATION**** Check upload link for domain {}",
                                                                                event.getDomainId());
                                                            }
                                                        });
                                    });
                    break;

                case CHECK_STATUS:
                    recordingService.findByDomainIdAndRecordingId(event.getDomainId(), event.getRecordingId())
                                    .filter(Recording::isSucceeded)
                                    .ifPresentOrElse(recording -> log.info(
                                                    "Received SUCCESS event for the recording {} and domain {}",
                                                    event.getRecordingId(), event.getDomainId()),
                                                    () -> log.info("****NOTIFICATION**** Did not receive SUCCESS event for the recording: {} and domain {}",
                                                                    event.getRecordingId(), event.getDomainId()));
                    break;
                default:
            }
            callback.ack();
        } catch (Exception e) {
            log.error("Received {}: {}", e.getMessage(), e);
            callback.nack();
        }
    }

    private void update(RecordingUploadEvent event) {
        recordingService.findByDomainIdAndRecordingId(event.getDomainId(), event.getRecordingId())
                        .ifPresentOrElse(recording -> {
                            recording.setEndTimestamp(new java.sql.Timestamp(new Date().getTime()));
                            recording.setSucceeded(true);
                            recordingService.save(recording);
                        }, () -> log.info("No Records found for recording={} and domain={}", event.getRecordingId(),
                                        event.getDomainId()));
    }

    private void insert(RecordingUploadEvent event) {
        recordingService.save(Recording.builder()
                        .domainId(event.getDomainId())
                        .recordingId(event.getRecordingId())
                        .destination(event.getDestination())
                        .recordingFilename(event.getRecordingFilename())
                        .queuedTimestamp(new java.sql.Timestamp(new Date().getTime()))
                        .succeeded(false)
                        .build());
    }

    private void createDelayedTaskUsing(RecordingUploadEvent event, long timeDelay) {
        try (CloudTasksClient client = CloudTasksClient.create()) {
            // Construct the task body.
            Task.Builder taskBuilder =
                            Task.newBuilder()
                                            .setHttpRequest(createPubSubRequest(event))
                                            .setScheduleTime(
                                                            Timestamp.newBuilder()
                                                                            .setSeconds(Instant.now(Clock.systemUTC())
                                                                                            .plusSeconds(timeDelay)
                                                                                            .getEpochSecond()));

            // Send create task request.
            Task task = client.createTask(queuePath, taskBuilder.build());
            log.info("Task={} created for recordingId={} ", task.getName(), event.getRecordingId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> createPayload(RecordingUploadEvent event) {
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                        .setData(ByteString.copyFrom(new Gson().toJson(event).getBytes()))
                        .build();

        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("data", Base64.getEncoder().encodeToString(pubsubMessage.getData().toByteArray()));
        if (pubsubMessage.getAttributesMap() != null) {
            messagePayload.put("attributes", pubsubMessage.getAttributesMap());
        }
        return Collections.singletonMap("messages", messagePayload);
    }

    private HttpRequest createPubSubRequest(RecordingUploadEvent event) throws JsonProcessingException {
        Map<String, Object> payload = createPayload(event);

        String url = UriComponentsBuilder
                        .fromHttpUrl(publishUrl)
                        .encode()
                        .toUriString();

        OAuthToken oauthToken = OAuthToken.newBuilder().setServiceAccountEmail(saEmail)
                        .setScope("https://www.googleapis.com/auth/pubsub").build();

        String serializedPayload = objectMapper.writeValueAsString(payload);
        return HttpRequest.newBuilder()
                        .setBody(ByteString.copyFrom(serializedPayload, StandardCharsets.UTF_8)).setUrl(url)
                        .setOauthToken(oauthToken).setHttpMethod(HttpMethod.POST)
                        .putHeaders("Content-Type", "application/json").build();
    }
}
