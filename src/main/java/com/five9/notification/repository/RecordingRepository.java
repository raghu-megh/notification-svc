package com.five9.notification.repository;

import com.five9.notification.entity.Recording;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface RecordingRepository extends CrudRepository<Recording,String> {
    @Nullable
    Optional<Recording> findByDomainIdAndRecordingId(String domainId, String recordingId);

//    Recording findByDomainIdAndRecordingIdAndSucceeded(String domainId, String recordingId, int succeeded);

    Optional<Recording> findByRecordingId(String recordingId);

    Optional<List<Recording>> findByQueuedTimestampBetweenAndDomainId(Timestamp start, Timestamp end, String domainId);
}
