package com.five9.notification.service;

import com.five9.notification.entity.Recording;
import com.five9.notification.repository.RecordingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecordingService {
    @Autowired
    private RecordingRepository repository;

    public Recording saveRecording(Recording recording) {
        return repository.save(recording);
    }

    public Recording findByDomainIdAndRecordingId(String domainId, String recordingId) {
        return repository.findByDomainIdAndRecordingId(domainId, recordingId);
    }

}
