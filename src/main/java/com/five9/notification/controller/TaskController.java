package com.five9.notification.controller;

import com.five9.notification.RecordingUploadEvent;
import com.five9.notification.entity.Recording;
import com.five9.notification.service.RecordingService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tasks")
public class TaskController {
    @Autowired
    RecordingService recordingService;
    public Timestamp queuedTime;

    @PostMapping("/submit")
    public ResponseEntity<String> submit(@RequestBody String body) {
        RecordingUploadEvent event = new Gson().fromJson(body, RecordingUploadEvent.class);

        switch (event.getEventType()){
            case CHECK_UPLOAD_LINK:
                Optional<Recording> getRecording = recordingService.findByRecordingId(event.getRecordingId());
                if(getRecording.isPresent()){
                    queuedTime = getRecording.get().getQueuedTimestamp();
                }else {log.info("No Records found");}
                Timestamp start = new java.sql.Timestamp(queuedTime.getTime() - 2 * 60 * 1000);//2mins
                //change query
                Optional<List<Recording>> domainExists = recordingService.findByQueuedTimestampBetweenAndDomainId(start,queuedTime,event.getDomainId());
                printEventDetails(event);
                if(domainExists.isPresent()){
                    int count = 0;
                    for(int i = 0; i < domainExists.get().size(); i++){
                        if(domainExists.get().get(i).isSucceeded()){count += 1;}}
                    if (count > 0) {log.info("Received Success Event for at least 1 domain(domain id)");}
                    else {log.info("Did not Receive Success Event for  at least 1 domain");}
                }else {log.info("Did not Receive Success Event for  at least 1 domain");}
                break;
            case CHECK_STATUS:
                Optional<Recording> recordingsExists = recordingService.findByDomainIdAndRecordingId(event.getDomainId(),event.getRecordingId());
                printEventDetails(event);
                if(recordingsExists.isPresent()){
                    if (recordingsExists.get().isSucceeded()) {log.info("Received Success Event for the recording");}
                    else {log.info("Did not Receive Success Event for the recording");}
                }else {log.info("No Records found");}
                break;
        }
        return ResponseEntity.ok().build();
    }

    public void printEventDetails(RecordingUploadEvent event){
        log.info("Received event Type {}", event.getEventType());
        log.info("Recording ID: {}", event.getRecordingId());
        log.info("Domain ID:  {}", event.getDomainId());
    }
}
