package com.medsync.appointment_service.service;

import com.medsync.appointment_service.dto.DoctorScheduleEmailRequest;
import com.medsync.appointment_service.entity.Appointment;
import com.medsync.appointment_service.feign.DoctorClient;
import com.medsync.appointment_service.feign.NotificationClient;
import com.medsync.appointment_service.repository.AppointmentRepository;
import com.medsync.common.dto.DoctorDTO;
import com.medsync.common.enums.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorScheduleNotificationService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    private final AppointmentRepository appointmentRepository;
    private final DoctorClient doctorClient;
    private final NotificationClient notificationClient;

    // Runs every day at 7:00 AM server time. Cron format: sec min hour day month weekday
    @Scheduled(cron = "0 0 7 * * *")
    public void sendTodaysSchedules() {
        log.info("Running scheduled doctor daily-schedule job");
        sendSchedulesForDate(LocalDate.now());
    }

    public void sendSchedulesForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Appointment> todaysAppointments = appointmentRepository
                .findByAppointmentDateBetween(startOfDay, endOfDay)
                .stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .toList();

        Map<Long, List<Appointment>> byDoctor = todaysAppointments.stream()
                .collect(Collectors.groupingBy(Appointment::getDoctorId));

        if (byDoctor.isEmpty()) {
            log.info("No appointments scheduled for {}, skipping doctor schedule emails", date);
            return;
        }

        byDoctor.forEach((doctorId, appointments) -> {
            try {
                sendScheduleForDoctor(doctorId, appointments);
            } catch (Exception ex) {
                log.warn("Failed to send daily schedule email for doctor {}: {}", doctorId, ex.getMessage());
            }
        });
    }

    private void sendScheduleForDoctor(Long doctorId, List<Appointment> appointments) {
        DoctorDTO doctor = doctorClient.getDoctorById(doctorId);

        List<DoctorScheduleEmailRequest.ScheduleEntry> entries = appointments.stream()
                .sorted((a, b) -> a.getAppointmentDate().compareTo(b.getAppointmentDate()))
                .map(a -> {
                    DoctorScheduleEmailRequest.ScheduleEntry entry = new DoctorScheduleEmailRequest.ScheduleEntry();
                    entry.setPatientName("Patient #" + a.getPatientId());
                    entry.setTime(a.getAppointmentDate().format(TIME_FORMATTER));
                    return entry;
                })
                .toList();

        DoctorScheduleEmailRequest request = new DoctorScheduleEmailRequest();
        request.setDoctorEmail(doctor.getEmail());
        request.setDoctorName(doctor.getName());
        request.setAppointments(entries);

        notificationClient.sendDoctorSchedule(request);
        log.info("Sent daily schedule email to {} for {} appointment(s)", doctor.getEmail(), entries.size());
    }
}