package com.medsync.doctor_service.service;

import com.medsync.common.dto.DoctorDTO;
import com.medsync.common.exception.ResourceNotFoundException;
import com.medsync.doctor_service.dto.DoctorRequest;
import com.medsync.doctor_service.entity.Doctor;
import com.medsync.doctor_service.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Transactional
    public DoctorDTO addDoctor(DoctorRequest request) {
        Doctor doctor = toEntity(request);
        return toDto(doctorRepository.save(doctor));
    }

    public List<DoctorDTO> getDoctors() {
        return doctorRepository.findAll().stream().map(this::toDto).toList();
    }

    public DoctorDTO getDoctorById(Long id) {
        return toDto(findDoctor(id));
    }

    @Transactional
    public DoctorDTO updateDoctor(Long id, DoctorRequest request) {
        Doctor doctor = findDoctor(id);
        applyRequest(doctor, request);
        return toDto(doctorRepository.save(doctor));
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor not found");
        }
        doctorRepository.deleteById(id);
    }

    private Doctor findDoctor(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    private Doctor toEntity(DoctorRequest request) {
        Doctor doctor = new Doctor();
        applyRequest(doctor, request);
        return doctor;
    }

    private void applyRequest(Doctor doctor, DoctorRequest request) {
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setExperience(request.getExperience());
        doctor.setDepartment(request.getDepartment());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setPhone(request.getPhone());
        doctor.setAvailability(request.getAvailability());
    }

    private DoctorDTO toDto(Doctor doctor) {
        return DoctorDTO.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .email(doctor.getEmail())
                .specialization(doctor.getSpecialization())
                .experience(doctor.getExperience())
                .department(doctor.getDepartment())
                .consultationFee(doctor.getConsultationFee())
                .phone(doctor.getPhone())
                .availability(doctor.getAvailability())
                .build();
    }
}
