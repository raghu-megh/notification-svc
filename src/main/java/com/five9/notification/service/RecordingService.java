package com.five9.notification.service;

import com.five9.notification.entity.Recording;
import com.five9.notification.repository.RecordingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class RecordingService {
    @Autowired
    private RecordingRepository repository;

    public Recording saveRecording(Recording recording) {
        return repository.save(recording);
    }

    public Optional<Recording> findByDomainIdAndRecordingId(String domainId, String recordingId) {
        return repository.findByDomainIdAndRecordingId(domainId, recordingId);
    }
    public Optional<Recording> findByRecordingId(String recordingId) {
        return repository.findByRecordingId(recordingId);
    }

    public Optional<List<Recording>> findByQueuedTimestampBetweenAndDomainIdAndSucceeded(Timestamp start, Timestamp end, String domainId, boolean succeeded) {
        return repository.findByQueuedTimestampBetweenAndDomainIdAndSucceeded(start, end, domainId, succeeded);
    }

}
