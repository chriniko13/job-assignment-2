package com.chriniko.job.assignment;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2

@SpringBootApplication
public class AssignmentApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AssignmentApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Application is up and running...");
    }
}
