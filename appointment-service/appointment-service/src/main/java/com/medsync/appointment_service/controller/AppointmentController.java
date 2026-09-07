package com.medsync.appointment_service.controller;

import com.medsync.appointment_service.dto.AppointmentRequest;
import com.medsync.appointment_service.dto.AppointmentResponse;
import com.medsync.appointment_service.service.AppointmentService;
import com.medsync.appointment_service.service.DoctorScheduleNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;
    private final DoctorScheduleNotificationService doctorScheduleNotificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public AppointmentResponse bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        return service.bookAppointment(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public List<AppointmentResponse> getAppointments() {
        return service.getAppointments();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public AppointmentResponse getAppointmentById(@PathVariable Long id) {
        return service.getAppointmentById(id);
    }

    @PutMapping("/cancel/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public AppointmentResponse cancelAppointment(@PathVariable Long id) {
        return service.cancelAppointment(id);
    }

    @PutMapping("/confirm/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public AppointmentResponse confirmAppointment(@PathVariable Long id) {
        return service.confirmAppointment(id);
    }

    @PutMapping("/complete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public AppointmentResponse completeAppointment(@PathVariable Long id) {
        return service.completeAppointment(id);
    }

    @PostMapping("/notify-doctor-schedules")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> triggerDoctorScheduleEmails() {
        doctorScheduleNotificationService.sendSchedulesForDate(java.time.LocalDate.now());
        return Map.of("message", "Doctor schedule emails triggered for today");
    }
}