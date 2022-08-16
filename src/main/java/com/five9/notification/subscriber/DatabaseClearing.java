package com.five9.notification.subscriber;

import com.five9.notification.service.RecordingService;
import com.five9.pubsub.MessageReceiver;
import com.five9.pubsub.MessageReceiverCallback;
import com.five9.pubsub.spring.EventBusSubscriber;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Date;

@EventBusSubscriber(
        topic = "${events.ingress.deleteRecords-topic}",
        subscription = "${events.ingress.deleteRecords-subscription}")
@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class DatabaseClearing implements MessageReceiver<JsonObject>{

    private final RecordingService recordingService;


    @Override
    public void onMessage(JsonObject message, MessageReceiverCallback callback) {
        log.info("***********Received cron message********** {}", message.get("cron-job"));

        java.sql.Timestamp start = new java.sql.Timestamp(
                new Date().getTime() - 1 * 60 * 60 * 1000);

        recordingService.deleteRecordingsByQueuedTimestampGreaterThanAndSucceeded(start, true);
    }
}
