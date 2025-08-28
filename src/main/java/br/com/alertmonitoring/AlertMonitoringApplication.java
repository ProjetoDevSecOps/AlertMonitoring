package br.com.alertmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlertMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertMonitoringApplication.class, args);
    }
}
