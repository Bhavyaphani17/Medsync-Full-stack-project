package com.medsync.hospital_management_service.dto;

import com.medsync.hospital_management_service.enums.StaffRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private StaffRole role;
    private String department;
    private String shift;
}