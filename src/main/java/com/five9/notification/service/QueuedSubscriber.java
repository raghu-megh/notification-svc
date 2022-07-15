package com.five9.notification.service;

import com.five9.avro.recording.upload.events.RecordingUploadQueued;
import com.five9.pubsub.MessageReceiver;
import com.five9.pubsub.MessageReceiverCallback;
import com.five9.pubsub.spring.EventBusSubscriber;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EventBusSubscriber(topic = "first-topic", subscription = "first-subscription")
@AllArgsConstructor
public class QueuedSubscriber implements MessageReceiver<String> {

    @Override
    public void onMessage(String message, MessageReceiverCallback callback) {
        log.info("Message received: {}", message);
        RecordingUploadQueued queued = new Gson().fromJson(message, RecordingUploadQueued.class);
        callback.ack();
    }
}
