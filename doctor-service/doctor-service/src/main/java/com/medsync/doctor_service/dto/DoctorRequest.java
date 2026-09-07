package com.medsync.doctor_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String specialization;

    @NotNull
    private Integer experience;

    @NotBlank
    private String department;

    @NotNull
    private Double consultationFee;

    private String phone;

    private String availability;
}
