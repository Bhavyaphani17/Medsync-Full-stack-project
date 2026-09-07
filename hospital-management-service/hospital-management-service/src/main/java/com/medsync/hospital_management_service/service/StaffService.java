package com.medsync.hospital_management_service.service;

import com.medsync.common.exception.BadRequestException;
import com.medsync.common.exception.ResourceNotFoundException;
import com.medsync.hospital_management_service.dto.StaffRequest;
import com.medsync.hospital_management_service.dto.StaffResponse;
import com.medsync.hospital_management_service.entity.Staff;
import com.medsync.hospital_management_service.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;

    @Transactional
    public StaffResponse addStaff(StaffRequest request) {
        if (staffRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Staff member with this email already exists");
        }

        Staff staff = Staff.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole())
                .department(request.getDepartment())
                .shift(request.getShift())
                .build();

        return toResponse(staffRepository.save(staff));
    }

    public List<StaffResponse> getAllStaff() {
        return staffRepository.findAll().stream().map(this::toResponse).toList();
    }

    public StaffResponse getStaffById(Long id) {
        return toResponse(findStaff(id));
    }

    @Transactional
    public StaffResponse updateStaff(Long id, StaffRequest request) {
        Staff staff = findStaff(id);

        staffRepository.findAll().stream()
                .filter(s -> s.getEmail().equals(request.getEmail()) && !s.getId().equals(id))
                .findFirst()
                .ifPresent(s -> {
                    throw new BadRequestException("Email already in use by another staff member");
                });

        staff.setName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setPhone(request.getPhone());
        staff.setRole(request.getRole());
        staff.setDepartment(request.getDepartment());
        staff.setShift(request.getShift());

        return toResponse(staffRepository.save(staff));
    }

    @Transactional
    public void deleteStaff(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new ResourceNotFoundException("Staff member not found");
        }
        staffRepository.deleteById(id);
    }

    private Staff findStaff(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found"));
    }

    private StaffResponse toResponse(Staff staff) {
        return StaffResponse.builder()
                .id(staff.getId())
                .name(staff.getName())
                .email(staff.getEmail())
                .phone(staff.getPhone())
                .role(staff.getRole())
                .department(staff.getDepartment())
                .shift(staff.getShift())
                .build();
    }
}