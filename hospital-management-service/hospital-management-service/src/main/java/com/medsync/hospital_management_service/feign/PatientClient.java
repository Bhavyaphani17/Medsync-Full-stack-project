package com.medsync.hospital_management_service.feign;

import com.medsync.common.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service")
public interface PatientClient {

    @GetMapping("/patients/{id}")
    PatientDTO getPatientById(@PathVariable("id") Long id);
}