package com.medsync.appointment_service.service;

import com.medsync.appointment_service.dto.*;
import com.medsync.appointment_service.entity.Appointment;
import com.medsync.appointment_service.feign.DoctorClient;
import com.medsync.appointment_service.feign.NotificationClient;
import com.medsync.appointment_service.feign.PatientClient;
import com.medsync.appointment_service.repository.AppointmentRepository;
import com.medsync.common.dto.DoctorDTO;
import com.medsync.common.dto.PatientDTO;
import com.medsync.common.enums.AppointmentStatus;
import com.medsync.common.exception.BadRequestException;
import com.medsync.common.exception.ResourceNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository repository;
    private final DoctorClient doctorClient;
    private final PatientClient patientClient;
    private final NotificationClient notificationClient;

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        PatientDTO patient = fetchPatient(request.getPatientId());
        DoctorDTO doctor = fetchDoctor(request.getDoctorId());

        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .appointmentDate(request.getAppointmentDate())
                .status(AppointmentStatus.BOOKED)
                .build();

        Appointment saved = repository.save(appointment);
        sendConfirmationEmail(patient, doctor, saved.getAppointmentDate());

        return toResponse(saved, patient, doctor);
    }

    public List<AppointmentResponse> getAppointments() {
        return repository.findAll().stream()
                .map(this::toResponseWithLookup)
                .toList();
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = findAppointment(id);
        return toResponseWithLookup(appointment);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment appointment = findAppointment(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Appointment is already cancelled");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Completed appointments cannot be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return toResponseWithLookup(repository.save(appointment));
    }

    @Transactional
    public AppointmentResponse confirmAppointment(Long id) {
        Appointment appointment = findAppointment(id);
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new BadRequestException("Only booked appointments can be confirmed");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return toResponseWithLookup(repository.save(appointment));
    }

    @Transactional
    public AppointmentResponse completeAppointment(Long id) {
        Appointment appointment = findAppointment(id);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Cancelled appointments cannot be completed");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return toResponseWithLookup(repository.save(appointment));
    }

    private Appointment findAppointment(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    private PatientDTO fetchPatient(Long patientId) {
        try {
            return patientClient.getPatientById(patientId);
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException("Patient not found with id: " + patientId);
        }
    }

    private DoctorDTO fetchDoctor(Long doctorId) {
        try {
            return doctorClient.getDoctorById(doctorId);
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException("Doctor not found with id: " + doctorId);
        }
    }

    private AppointmentResponse toResponseWithLookup(Appointment appointment) {
        PatientDTO patient = fetchPatient(appointment.getPatientId());
        DoctorDTO doctor = fetchDoctor(appointment.getDoctorId());
        return toResponse(appointment, patient, doctor);
    }

    private AppointmentResponse toResponse(Appointment appointment, PatientDTO patient, DoctorDTO doctor) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .patientName(patient.getName())
                .patientEmail(patient.getEmail())
                .doctorId(appointment.getDoctorId())
                .doctorName(doctor.getName())
                .doctorEmail(doctor.getEmail())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .build();
    }

    private void sendConfirmationEmail(PatientDTO patient, DoctorDTO doctor, java.time.LocalDateTime appointmentDate) {
        try {
            AppointmentConfirmationEmailRequest emailRequest = new AppointmentConfirmationEmailRequest();
            emailRequest.setPatientEmail(patient.getEmail());
            emailRequest.setPatientName(patient.getName());
            emailRequest.setDoctorName(doctor.getName());
            emailRequest.setAppointmentDate(appointmentDate);
            notificationClient.sendAppointmentConfirmation(emailRequest);
        } catch (Exception ex) {
            log.warn("Failed to send appointment confirmation email: {}", ex.getMessage());
        }
    }
}
