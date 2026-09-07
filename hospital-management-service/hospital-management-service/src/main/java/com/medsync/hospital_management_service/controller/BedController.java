package com.medsync.hospital_management_service.controller;

import com.medsync.hospital_management_service.dto.BedRequest;
import com.medsync.hospital_management_service.dto.BedResponse;
import com.medsync.hospital_management_service.service.BedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hospital/beds")
@RequiredArgsConstructor
public class BedController {

    private final BedService bedService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public BedResponse addBed(@Valid @RequestBody BedRequest request) {
        return bedService.addBed(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public List<BedResponse> getAllBeds() {
        return bedService.getAllBeds();
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public List<BedResponse> getAvailableBeds() {
        return bedService.getAvailableBeds();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public BedResponse getBedById(@PathVariable Long id) {
        return bedService.getBedById(id);
    }

    @PutMapping("/assign/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public BedResponse assignBed(@PathVariable("id") Long id, @RequestParam("patientId") Long patientId) {
        return bedService.assignBed(id, patientId);
    }

    @PutMapping("/release/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public BedResponse releaseBed(@PathVariable("id") Long id) {
        return bedService.releaseBed(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> deleteBed(@PathVariable Long id) {
        bedService.deleteBed(id);
        return Map.of("message", "Bed deleted");
    }
}