package com.medsync.hospital_management_service.service;

import com.medsync.common.enums.BedStatus;
import com.medsync.common.exception.BadRequestException;
import com.medsync.common.exception.ResourceNotFoundException;
import com.medsync.hospital_management_service.dto.RoomRequest;
import com.medsync.hospital_management_service.dto.RoomResponse;
import com.medsync.hospital_management_service.entity.Room;
import com.medsync.hospital_management_service.repository.BedRepository;
import com.medsync.hospital_management_service.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    @Transactional
    public RoomResponse addRoom(RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new BadRequestException("Room number already exists");
        }

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .floor(request.getFloor())
                .build();

        return toResponse(roomRepository.save(room));
    }

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RoomResponse getRoomById(Long id) {
        return toResponse(findRoom(id));
    }

    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = findRoom(id);
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setFloor(request.getFloor());
        return toResponse(roomRepository.save(room));
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room not found");
        }
        if (!bedRepository.findByRoomId(id).isEmpty()) {
            throw new BadRequestException("Cannot delete a room that still has beds assigned to it");
        }
        roomRepository.deleteById(id);
    }

    private Room findRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    private RoomResponse toResponse(Room room) {
        var beds = bedRepository.findByRoomId(room.getId());
        long available = beds.stream().filter(b -> b.getStatus() == BedStatus.AVAILABLE).count();

        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .floor(room.getFloor())
                .totalBeds(beds.size())
                .availableBeds((int) available)
                .build();
    }
}