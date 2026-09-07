package com.medsync.appointment_service.feign;

import com.medsync.appointment_service.dto.AppointmentConfirmationEmailRequest;
import com.medsync.appointment_service.dto.DoctorScheduleEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/notifications/appointment-confirmation")
    void sendAppointmentConfirmation(@RequestBody AppointmentConfirmationEmailRequest request);

    @PostMapping("/notifications/doctor-schedule")
    void sendDoctorSchedule(@RequestBody DoctorScheduleEmailRequest request);
}