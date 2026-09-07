package com.medsync.patient_service.repository;

import com.medsync.patient_service.entity.Patient;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository
        extends JpaRepository<
        Patient,
        Long
        >{

}