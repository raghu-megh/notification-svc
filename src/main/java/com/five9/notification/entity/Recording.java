package com.five9.notification.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.errorprone.annotations.Immutable;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Builder
@Getter
@Table(name = "Recordings")
public class Recording {
    @Id
    @GeneratedValue
    private int id;

    @JsonProperty("domainId")
    private String domainId;

    @JsonProperty("recordingId")
    String recordingId;

    @JsonProperty("destination")
    String destination;

    @JsonProperty("recordingFilename")
    String recordingFilename;

    @JsonProperty("queuedTimestamp")
    String queuedTimestamp;
}
