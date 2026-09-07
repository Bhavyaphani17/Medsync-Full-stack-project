package com.medsync.appointment_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentConfirmationEmailRequest {

    private String patientEmail;
    private String patientName;
    private String doctorName;
    private LocalDateTime appointmentDate;
}
