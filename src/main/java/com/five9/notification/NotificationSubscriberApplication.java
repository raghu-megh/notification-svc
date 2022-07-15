package com.five9.notification;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class NotificationSubscriberApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationSubscriberApplication.class, args);
    }
}
