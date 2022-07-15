package com.five9.notification.config;

import com.five9.notification.service.NotificationSubscriberService;

import org.springframework.context.annotation.Bean;

public class NotificationsSubscriberConfig {

    @Bean
    public NotificationSubscriberService publishService() {
        return new NotificationSubscriberService();
    }
}
