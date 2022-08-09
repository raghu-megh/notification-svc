package com.five9.notification.controller;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tasks")
public class TaskController {

    @PostMapping("/submit")
    public ResponseEntity<String> submit(@RequestBody String body) throws Exception {
        // check db for success of this recordingId + domainId
        // if no success then fire a notification
        JsonObject convertedObject = new Gson().fromJson(body, JsonObject.class);
        String recordingId = String.valueOf(convertedObject.get("recordingId")).replaceAll("^\"|\"$", "");
        String domainId = String.valueOf(convertedObject.get("domainId")).replaceAll("^\"|\"$", "");
        int successVal = 1;
//
        if (body.contains("ERROR")) {

            printPayload(body);

            String sql = "SELECT succeeded from recordings r where r.recording_id = ? and r.succeeded = ?";
            PreparedStatement RecordingUploadTimeout_SqlStmt = prepareQuery(sql);
            RecordingUploadTimeout_SqlStmt.setString(1, recordingId);
            RecordingUploadTimeout_SqlStmt.setInt(2, successVal);

            ResultSet rs = RecordingUploadTimeout_SqlStmt.executeQuery();
            if (rs.next() == true) {log.info("Received Success Event for the recording");}
            else {log.info("Did not Receive Success Event for the recording");}
        }
        else if (body.contains("UNKNOWN")) {

            printPayload(body);

            String sql = "SELECT succeeded from recordings where domain_id = ? and succeeded = ? and queued_timestamp is NOT NULL";
            PreparedStatement StorageLocationUnreachable_SqlStmt = prepareQuery(sql);
            StorageLocationUnreachable_SqlStmt.setString(1, domainId);
            StorageLocationUnreachable_SqlStmt.setInt(2, successVal);

            ResultSet rs1 = StorageLocationUnreachable_SqlStmt.executeQuery();
            if (rs1.next() == true) {log.info("Received Success Event for at least 1 domain");}
            else {log.info("Did not Receive Success Event at least 1 domain");}
        }

        return ResponseEntity.ok().build();
    }

    public void printPayload(String body) {
        String output;
        output = String.format("Received task %s", body);
        System.out.println(output);
    }

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;
    public PreparedStatement prepareQuery(String sql) throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);
        PreparedStatement myStmt;
        myStmt = conn.prepareStatement(sql);
        return myStmt;
    }
}
