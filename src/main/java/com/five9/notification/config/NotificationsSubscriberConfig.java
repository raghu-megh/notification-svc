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
    private String PROJECT_ID;
    @Value("${gcp.task.queue.locationId}")
    private String LOCATION_ID;
    @Value("${gcp.task.queue.id}")
    private String QUEUE_ID;
    @Value("${recording.check.status.interval.secs}")
    private long CHECK_STATUS_INTERVAL;
    @Value("${recording.check.upload.link.interval.secs}")
    private long CHECK_UPLOAD_LINK_INTERVAL;
    @Value("${gcp.publish-url}")
    private String publishUrl;
    @Value("${gcp.sa.email}")
    private String saEmail;

    @Bean
    public String queuePath() {
        return QueueName.of(PROJECT_ID, LOCATION_ID, QUEUE_ID).toString();
    }

    @Bean
    public Duration checkStatusInterval() {
        return Duration.ofSeconds(CHECK_STATUS_INTERVAL);
    }

    @Bean
    public Duration checkUploadLinkInterval() {
        return Duration.ofSeconds(CHECK_UPLOAD_LINK_INTERVAL);
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
