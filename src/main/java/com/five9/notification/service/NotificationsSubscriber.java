package com.five9.notification.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import lombok.extern.slf4j.Slf4j;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
public class NotificationsSubscriber {

    private Subscriber subscriber;
    private String projectId;
    private String subscriptionId;

    public NotificationsSubscriber(String projectId, String subscriptionId) {
        this.projectId = projectId;
        this.subscriptionId = subscriptionId;
    }

    public void subscribeAsyncExample() {
        ProjectSubscriptionName subscriptionName =
                        ProjectSubscriptionName.of(projectId, subscriptionId);

        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                        (PubsubMessage message, AckReplyConsumer consumer) -> {
                            // Handle incoming message, then ack the received message.
                            log.info("Id: " + message.getMessageId());
                            log.info("Data: " + message.getData().toStringUtf8());
                            consumer.ack();
                        };


        subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
        // Start the subscriber.
        subscriber.startAsync().awaitRunning();

        log.info("Listening for messages on %s:\n", subscriptionName.toString());
    }

    public void stopSubscriber() {
        try {
            // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
            subscriber.awaitTerminated(30, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            subscriber.stopAsync();
        }
    }
}
