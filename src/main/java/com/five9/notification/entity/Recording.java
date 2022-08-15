package com.five9.notification.entity;

import java.sql.Timestamp;

import javax.persistence.*;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
@Table(name = "Recording")
public class Recording {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    // @Id
    @JsonProperty("domainId")
    private String domainId;

    @JsonProperty("recordingId")
    String recordingId;

    @JsonProperty("destination")
    String destination;

    @JsonProperty("recordingFilename")
    String recordingFilename;

    @JsonProperty("queuedTimestamp")
    Timestamp queuedTimestamp;

    @JsonProperty("attempts")
    String attempts;

    @JsonProperty("startTimestamp")
    Timestamp startTimestamp;

    @JsonProperty("endTimestamp")
    Timestamp endTimestamp;

    @JsonProperty("succeeded")
    boolean succeeded;
}
