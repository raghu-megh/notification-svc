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
                Optional<List<Recording>> domainExists = recordingService.findByQueuedTimestampBetweenAndDomainIdAndSucceeded(start,queuedTime,event.getDomainId(), true);
                log.info("Received event Type {}", event.getEventType());
                if(domainExists.isPresent() && domainExists.get().size() > 0){
                    log.info("Received Success Event for at least 1 recording for the domain: {}", event.getDomainId());}
                else {log.info("Did not Receive Success Event for at least 1 recording for the domain: {}", event.getDomainId());}
                break;
            case CHECK_STATUS:
                Optional<Recording> recordingsExists = recordingService.findByDomainIdAndRecordingId(event.getDomainId(),event.getRecordingId());
                log.info("Received event Type {}", event.getEventType());
                if(recordingsExists.isPresent() && recordingsExists.get().isSucceeded()){
                    log.info("Received Success Event for the recording: {}", event.getRecordingId());}
                   else {log.info("Did not Receive Success Event for the recording: {}", event.getRecordingId());}
                break;
        }
        return ResponseEntity.ok().build();
    }
}
