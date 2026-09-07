package com.medsync.notification_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DoctorScheduleEmailRequest {

    @NotBlank
    @Email
    private String doctorEmail;

    @NotBlank
    private String doctorName;

    @NotEmpty
    @Valid
    private List<ScheduleEntry> appointments;

    @Getter
    @Setter
    public static class ScheduleEntry {
        @NotBlank
        private String patientName;

        @NotBlank
        private String time;
    }
}