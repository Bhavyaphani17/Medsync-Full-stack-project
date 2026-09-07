package com.medsync.appointment_service.feign;

import com.medsync.common.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "doctor-service")
public interface DoctorClient {

    @GetMapping("/doctors/{id}")
    DoctorDTO getDoctorById(@PathVariable("id") Long id);
}
