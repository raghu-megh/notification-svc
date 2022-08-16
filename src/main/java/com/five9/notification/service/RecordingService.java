package com.five9.notification.service;

import com.five9.notification.entity.Recording;
import com.five9.notification.repository.RecordingRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RecordingService {

    private final RecordingRepository repository;

    public void save(Recording recording) {
        repository.save(recording);
    }

    public Optional<Recording> findByDomainIdAndRecordingId(String domainId, String recordingId) {
        return repository.findByDomainIdAndRecordingId(domainId, recordingId);
    }

    public Optional<List<Recording>> findRecordingsUsing(String domainId, Timestamp start, Timestamp end,
        boolean succeeded) {
        return Optional.ofNullable(repository.findRecordingsUsing(domainId, start, end, succeeded));
    }

    public Optional<List<Recording>> deleteRecordingsByQueuedTimestampGreaterThanAndSucceeded(Timestamp start, boolean succeeded) {
         repository.deleteRecordingsByQueuedTimestampGreaterThanAndSucceeded(start, succeeded);
        return null;
    }

}
