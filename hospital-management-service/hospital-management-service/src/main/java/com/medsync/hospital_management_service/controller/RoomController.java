package com.medsync.hospital_management_service.controller;

import com.medsync.hospital_management_service.dto.RoomRequest;
import com.medsync.hospital_management_service.dto.RoomResponse;
import com.medsync.hospital_management_service.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hospital/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse addRoom(@Valid @RequestBody RoomRequest request) {
        return roomService.addRoom(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public List<RoomResponse> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public RoomResponse getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return roomService.updateRoom(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return Map.of("message", "Room deleted");
    }
}