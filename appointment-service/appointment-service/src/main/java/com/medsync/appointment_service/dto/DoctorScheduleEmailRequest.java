package com.medsync.appointment_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DoctorScheduleEmailRequest {

    private String doctorEmail;
    private String doctorName;
    private List<ScheduleEntry> appointments;

    @Getter
    @Setter
    public static class ScheduleEntry {
        private String patientName;
        private String time;
    }
}