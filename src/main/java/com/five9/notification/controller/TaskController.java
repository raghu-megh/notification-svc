package com.five9.notification.controller;


import com.five9.notification.EventType;
import com.five9.notification.RecordingUploadEvent;
import com.five9.notification.entity.Recording;
import com.five9.notification.service.RecordingService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.Arrays;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tasks")
public class TaskController {

    @Autowired
    RecordingService recordingService;

    @PostMapping("/submit")
    public ResponseEntity<String> submit(@RequestBody String body) throws Exception {
        // check db for success of this recordingId + domainId
        // if no success then fire a notification
        RecordingUploadEvent event = new Gson().fromJson(body, RecordingUploadEvent.class);

        if (event.getEventType() == EventType.valueOf("ERROR")) {
            printPayload(body);

            Recording Records_exists = recordingService.findByDomainIdAndRecordingId(event.getDomainId(), event.getRecordingId());
            if (Records_exists.getSucceeded() == 1) {log.info("Received Success Event for the recording");}
            else {log.info("Did not Receive Success Event for the recording");}
        }
        else if (event.getEventType() == EventType.valueOf("UNKNOWN")) {

            printPayload(body);

            List<Recording> Records_exists = Arrays.asList(recordingService.findByDomainId(event.getDomainId()));

            int count = 0;
            for(int i = 0; i < Records_exists.size(); i++){
                if(Records_exists.get(i).getSucceeded() == 1){
                    count += 1;
                }
            }

            if (count > 0) {log.info("Received Success Event for at least 1 domain");}
            else {log.info("Did not Receive Success Event for  at least 1 domain");}
        }

        return ResponseEntity.ok().build();
    }

    public void printPayload(String body) {
        String output;
        output = String.format("Received task %s", body);
        System.out.println(output);
    }
}
