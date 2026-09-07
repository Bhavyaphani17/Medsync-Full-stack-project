package com.medsync.hospital_management_service.repository;

import com.medsync.hospital_management_service.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomNumber(String roomNumber);
}