package com.five9.notification.config;


import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Slf4j
@Configuration
@Import({GcpPubSubAutoConfiguration.class})

public class NotificationsSubscriberConfig {
}
