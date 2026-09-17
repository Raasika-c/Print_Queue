package com.printqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Digital Printing Queue Management System
 * Course: 23IT723 - DevOps Laboratory
 * Academic Year: 2026-2027
 */
@SpringBootApplication
@EnableScheduling
public class PrintQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrintQueueApplication.class, args);
    }
}
