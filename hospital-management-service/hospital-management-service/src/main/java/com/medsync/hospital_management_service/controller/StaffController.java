package com.medsync.hospital_management_service.controller;

import com.medsync.hospital_management_service.dto.StaffRequest;
import com.medsync.hospital_management_service.dto.StaffResponse;
import com.medsync.hospital_management_service.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hospital/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public StaffResponse addStaff(@Valid @RequestBody StaffRequest request) {
        return staffService.addStaff(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public List<StaffResponse> getAllStaff() {
        return staffService.getAllStaff();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public StaffResponse getStaffById(@PathVariable Long id) {
        return staffService.getStaffById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StaffResponse updateStaff(@PathVariable Long id, @Valid @RequestBody StaffRequest request) {
        return staffService.updateStaff(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return Map.of("message", "Staff member deleted");
    }
}