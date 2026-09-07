package com.medsync.notification_service.controller;

import com.medsync.notification_service.dto.AppointmentConfirmationEmailRequest;
import com.medsync.notification_service.dto.DoctorScheduleEmailRequest;
import com.medsync.notification_service.service.SendGridEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SendGridEmailService emailService;

    @PostMapping("/appointment-confirmation")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendAppointmentConfirmation(@Valid @RequestBody AppointmentConfirmationEmailRequest request) {
        emailService.sendAppointmentConfirmation(request);
    }

    @PostMapping("/doctor-schedule")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendDoctorSchedule(@Valid @RequestBody DoctorScheduleEmailRequest request) {
        emailService.sendDoctorDailySchedule(request);
    }
}