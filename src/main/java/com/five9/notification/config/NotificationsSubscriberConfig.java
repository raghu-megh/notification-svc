package com.five9.notification.config;


import java.time.Duration;

import lombok.extern.slf4j.Slf4j;

import com.google.cloud.tasks.v2beta3.QueueName;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class NotificationsSubscriberConfig {

    @Value("${gcp.projectId}")
    private String projectId;
    @Value("${gcp.task.queue.locationId}")
    private String locationId;
    @Value("${gcp.task.queue.id}")
    private String queueId;
    @Value("${recording.check.status.interval.secs}")
    private long checkStatusInterval;
    @Value("${recording.check.upload.link.interval.secs}")
    private long checkUploadLinkInterval;

    @Value("${recording.check.delete.records.interval.secs}")
    private long deleteRecordsInterval;

    @Value("${gcp.publish-url}")
    private String publishUrl;
    @Value("${gcp.sa.email}")
    private String saEmail;

    @Bean
    public String queuePath() {
        return QueueName.of(projectId, locationId, queueId).toString();
    }

    @Bean
    public Duration checkStatusInterval() {
        return Duration.ofSeconds(checkStatusInterval);
    }

    @Bean
    public Duration checkUploadLinkInterval() {
        return Duration.ofSeconds(checkUploadLinkInterval);
    }

    @Bean
    public Duration deleteRecordsInterval() {
        return Duration.ofSeconds(deleteRecordsInterval);
    }

    @Bean
    public String publishUrl() {
        return publishUrl;
    }

    @Bean
    public String saEmail() {
        return saEmail;
    }
}
