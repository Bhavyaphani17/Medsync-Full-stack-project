package com.medsync.notification_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentConfirmationEmailRequest {

    @NotBlank
    @Email
    private String patientEmail;

    @NotBlank
    private String patientName;

    @NotBlank
    private String doctorName;

    @NotNull
    private LocalDateTime appointmentDate;
}