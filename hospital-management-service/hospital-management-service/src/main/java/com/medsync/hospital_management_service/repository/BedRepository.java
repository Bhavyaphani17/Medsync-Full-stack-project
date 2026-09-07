package com.medsync.hospital_management_service.repository;

import com.medsync.common.enums.BedStatus;
import com.medsync.hospital_management_service.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByStatus(BedStatus status);
    List<Bed> findByRoomId(Long roomId);
}