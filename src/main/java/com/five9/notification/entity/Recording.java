package com.five9.notification.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.errorprone.annotations.Immutable;
import lombok.*;

import javax.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    //    @Id
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

    @JsonProperty("attempts")
    String attempts;

    @JsonProperty("startTimestamp")
    String startTimestamp;

    @JsonProperty("endTimestamp")
    String endTimestamp;

    @JsonProperty("succeeded")
    int succeeded;
}
