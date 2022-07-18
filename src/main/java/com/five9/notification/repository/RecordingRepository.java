package com.five9.notification.repository;

import com.five9.notification.entity.Recording;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordingRepository extends JpaRepository<Recording,Integer> {

}