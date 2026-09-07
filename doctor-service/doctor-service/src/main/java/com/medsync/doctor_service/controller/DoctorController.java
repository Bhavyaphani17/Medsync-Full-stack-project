package com.medsync.doctor_service.controller;

import com.medsync.common.dto.DoctorDTO;
import com.medsync.doctor_service.dto.DoctorRequest;
import com.medsync.doctor_service.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorDTO addDoctor(@Valid @RequestBody DoctorRequest request) {
        return doctorService.addDoctor(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public List<DoctorDTO> getDoctors() {
        return doctorService.getDoctors();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public DoctorDTO getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorDTO updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorRequest request) {
        return doctorService.updateDoctor(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return Map.of("message", "Doctor deleted");
    }
}
