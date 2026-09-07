package com.medsync.hospital_management_service;

import com.medsync.common.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.medsync.hospital_management_service", "com.medsync.common"})
@EnableFeignClients
@EnableConfigurationProperties(JwtProperties.class)
public class HospitalManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalManagementServiceApplication.class, args);
    }
}