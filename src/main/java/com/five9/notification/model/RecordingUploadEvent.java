package com.five9.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

@AllArgsConstructor
@Builder
@Getter
public class RecordingUploadEvent {

    // Order matters
    private String recordingId;
    private final String domainId;
    private final String recordingFilename;
    private final String destination;

    @With
    private EventType eventType;

}
