package com.hospitalqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This is the entry point. Running main() starts the embedded web server
// and turns on all of Spring's auto-configuration (this is what
// @SpringBootApplication does behind the scenes).
@SpringBootApplication
public class HospitalQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalQueueApplication.class, args);
    }
}
