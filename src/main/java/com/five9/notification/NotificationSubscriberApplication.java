package com.five9.notification;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

import com.five9.notification.entity.Recording;
import com.five9.notification.repository.RecordingRepository;
import com.five9.notification.service.RecordingService;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.tasks.v2beta3.*;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@Configuration
@Import({GcpPubSubAutoConfiguration.class})
@SpringBootApplication
public class NotificationSubscriberApplication {
    @Autowired
    RecordingService recordingService;

    @Autowired
    RecordingRepository repository;
    public static void main(String[] args) {
        SpringApplication.run(NotificationSubscriberApplication.class, args);
    }

    // Create a message channel for messages arriving from the subscription `sub-one`.
    @Bean
    public MessageChannel inputMessageChannel() {
        return new PublishSubscribeChannel();
    }

    // Create an inbound channel adapter to listen to the subscription `sub-one` and send
    // messages to the input message channel.
    @Bean
    public PubSubInboundChannelAdapter inboundChannelAdapter(
        @Qualifier("inputMessageChannel") MessageChannel messageChannel,
        PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter =
                        new PubSubInboundChannelAdapter(pubSubTemplate, "notifications.events-sub");
        adapter.setOutputChannel(messageChannel);
        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(String.class);
        return adapter;
    }

    // Define what happens to the messages arriving in the message channel.
    @ServiceActivator(inputChannel = "inputMessageChannel")
    public void messageReceiver(
        String payload,
        @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
        log.info("Message arrived via an inbound channel adapter from notifications.events-sub! Payload: " + payload);

        handleMessage(payload);

        message.ack();
    }

    private void handleMessage(String payload) {
        RecordingUploadEvent event = new Gson().fromJson(payload, RecordingUploadEvent.class);
        log.info("Received event Type {} ", event.getEventType());

        RecordingUploadEvent checkUploadEvent = event.withEventType(EventType.CHECK_UPLOAD_LINK);
        RecordingUploadEvent checkStatusEvent = event.withEventType(EventType.CHECK_STATUS);

        switch (event.getEventType()) {
            case START:
                insert(event);
                createDelayedTask(checkStatusEvent, 30);
                createDelayedTask(checkUploadEvent, 15);
                break;
            case SUCCESS:
                update(event);
                break;
            default:
        }
    }
    private void update(RecordingUploadEvent event) {
        Optional<Recording> recording = recordingService.findByDomainIdAndRecordingId(event.getDomainId(), event.getRecordingId());
        if (recording.isPresent()) {
            recording.get().setEndTimestamp(new java.sql.Timestamp(new Date().getTime()));
            recording.get().setSucceeded(true);
            recordingService.saveRecording(recording.get());
        } else {log.info("No Records found");}
    }

    private void insert(RecordingUploadEvent event) {
        Recording recording = new Recording();
        recording.setRecordingId(event.getRecordingId());
        recording.setDestination(event.getDestination());
        recording.setDomainId(event.getDomainId());
        recording.setQueuedTimestamp(new java.sql.Timestamp(new Date().getTime()));
        recording.setRecordingFilename(event.getRecordingFilename());
        recording.setSucceeded(false);

        recordingService.saveRecording(recording);
    }

    private void createDelayedTask(RecordingUploadEvent event, int delay_time) {
        try (CloudTasksClient client = CloudTasksClient.create()) {

            String projectId = "notifications-project-358320";
            String queueId = "delayed-queue-2";
            String locationId = "us-central1";

            int seconds = delay_time; // Scheduled delay for the task in seconds

            // Construct the fully qualified location.
            String queuePath = QueueName.of(projectId, locationId, queueId).toString();

            // Construct the task body.
            Task.Builder taskBuilder =
                    Task.newBuilder()
                            .setHttpRequest(
                                    HttpRequest.newBuilder()
                                            .setBody(ByteString.copyFrom(new Gson().toJson(event), Charset.defaultCharset()))
                                            .setUrl("https://7d44-50-220-235-244.ngrok.io/notifications-svc/v1/tasks/submit")
                                            .setHttpMethod(HttpMethod.POST)
                                            .build());

            // Add the scheduled time to the request.
            taskBuilder.setScheduleTime(
                    Timestamp.newBuilder()
                            .setSeconds(Instant.now(Clock.systemUTC()).plusSeconds(seconds).getEpochSecond()));

            // Send create task request.
            Task task = client.createTask(queuePath, taskBuilder.build());
            log.info("Task {} created for event {} ", task.getName(), event.getRecordingId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Create an input binder to receive messages from `topic-two` using a Consumer bean.
    @Bean
    public Consumer<Message<String>> receiveMessageFromTopicTwo() {
        return message -> {
            log.info(
                            "Message arrived via an input binder from topic! Payload: " + message.getPayload());
        };
    }

}
