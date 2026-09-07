package com.medsync.hospital_management_service.dto;

import com.medsync.hospital_management_service.enums.StaffRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String phone;

    @NotNull
    private StaffRole role;

    private String department;

    private String shift;
}