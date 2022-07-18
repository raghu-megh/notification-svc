package com.five9.notification.service;

import com.five9.avro.recording.upload.events.RecordingUploadQueued;
import com.five9.notification.entity.Recording;
import com.five9.pubsub.MessageReceiver;
import com.five9.pubsub.MessageReceiverCallback;
import com.five9.pubsub.spring.EventBusSubscriber;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@EventBusSubscriber(topic = "first-topic", subscription = "first-subscription")
@AllArgsConstructor
public class QueuedSubscriber implements MessageReceiver<String> {

    @Autowired
    private RecordingService service;

    public QueuedSubscriber() {

    }

    @Override
    public void onMessage(String message, MessageReceiverCallback callback) {
        log.info("Message received: {}", message);

//        RecordingUploadQueued queued = new Gson().fromJson(message, RecordingUploadQueued.class);
//        log.info(queued.getDestination());

//        String[] arrOfStr1 = message.split(",");
//        List<String> input_message1 = new ArrayList<String>();
//        for (String a : arrOfStr1){
//            input_message1.add(a);
//        }
//
//        String input_message2 = input_message1.get(0) + ',' + input_message1.get(1) +',' + input_message1.get(2)+',' + input_message1.get(3) + ',';
//
//        String[] arrOfStr2 = input_message1.get(4).split(":");
//        List<String> input_message3 = new ArrayList<String>();
//        for (String a : arrOfStr2){
//            input_message3.add(a);
//        }
//
//        String final_message = input_message2 + input_message3.get(0) + ':' + '"' + input_message3.get(1).strip()+ ":00:00Z" + '"' + '}';
//        log.info(final_message);
//
//        Gson g = new Gson();
//        Recording jsonObj = g.fromJson(final_message, Recording.class);
////        Recording record = Recording.builder().domainId(jsonObj.get("domainId").toString()).recordingId(jsonObj.get("recordingId").toString()).destination(jsonObj.get("destination").toString()).recordingFilename(jsonObj.get("recordingFilename").toString()).queuedTimestamp(jsonObj.get("queuedTimestamp").toString()).build();
//        service.saveRecording(jsonObj);

//        RecordingUploadQueued queued = new Gson().fromJson(final_message, RecordingUploadQueued.class);
//        log.info(queued.getDestination());

        callback.ack();
    }
}
