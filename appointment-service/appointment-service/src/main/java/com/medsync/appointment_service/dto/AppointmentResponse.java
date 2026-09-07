package com.medsync.appointment_service.dto;

import com.medsync.common.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppointmentResponse {

    private Long id;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private Long doctorId;
    private String doctorName;
    private String doctorEmail;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
}
