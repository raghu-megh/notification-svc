package com.five9.notification.repository;

import com.five9.notification.entity.Recording;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface RecordingRepository extends JpaRepository<Recording, String> {

    Optional<Recording> findByDomainIdAndRecordingId(String domainId, String recordingId);

    @Query(value = "select * from recording r where r.succeeded = :succeeded and r.domain_id = :domainId and " +
                    "r.start_timestamp >= :start and r.end_timestamp <= :end", nativeQuery = true)
    List<Recording> findRecordingsUsing(@Param("domainId") String domainId, @Param("start") Timestamp start,
        @Param("end") Timestamp end, @Param("succeeded") boolean succeeded);
}
