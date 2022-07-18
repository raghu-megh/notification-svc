package com.five9.notification.config;

import com.five9.notification.service.QueuedSubscriber;

import org.springframework.context.annotation.Bean;

public class NotificationsSubscriberConfig {

    @Bean
    public QueuedSubscriber publishService() {
        return new QueuedSubscriber();
    }
}
