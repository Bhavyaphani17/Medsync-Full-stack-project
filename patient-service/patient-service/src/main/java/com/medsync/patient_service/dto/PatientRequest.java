package com.medsync.patient_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Integer age;

    @NotBlank
    private String gender;

    private String disease;

    private Long doctorId;
}
