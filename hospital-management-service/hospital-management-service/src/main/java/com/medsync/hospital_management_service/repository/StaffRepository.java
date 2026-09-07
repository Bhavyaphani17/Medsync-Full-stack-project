package com.medsync.hospital_management_service.repository;

import com.medsync.hospital_management_service.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    boolean existsByEmail(String email);
}