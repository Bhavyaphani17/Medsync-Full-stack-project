package com.medsync.doctor_service;

import com.medsync.common.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.medsync.doctor_service", "com.medsync.common"})
@EnableConfigurationProperties(JwtProperties.class)
public class DoctorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoctorServiceApplication.class, args);
    }
}
