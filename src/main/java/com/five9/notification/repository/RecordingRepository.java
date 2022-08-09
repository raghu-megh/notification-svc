package com.five9.notification.repository;

import com.five9.notification.entity.Recording;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RecordingRepository extends CrudRepository<Recording,String> {
    Recording findByDomainIdAndRecordingId(String domainId, String recordingId);
}
