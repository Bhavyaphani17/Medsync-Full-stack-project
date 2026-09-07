package com.medsync.hospital_management_service.service;

import com.medsync.common.dto.PatientDTO;
import com.medsync.common.enums.BedStatus;
import com.medsync.common.exception.BadRequestException;
import com.medsync.common.exception.ResourceNotFoundException;
import com.medsync.hospital_management_service.dto.BedRequest;
import com.medsync.hospital_management_service.dto.BedResponse;
import com.medsync.hospital_management_service.entity.Bed;
import com.medsync.hospital_management_service.entity.Room;
import com.medsync.hospital_management_service.feign.PatientClient;
import com.medsync.hospital_management_service.repository.BedRepository;
import com.medsync.hospital_management_service.repository.RoomRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BedService {

    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final PatientClient patientClient;

    @Transactional
    public BedResponse addBed(BedRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        Bed bed = Bed.builder()
                .bedNumber(request.getBedNumber())
                .room(room)
                .status(BedStatus.AVAILABLE)
                .build();

        return toResponse(bedRepository.save(bed));
    }

    public List<BedResponse> getAllBeds() {
        return bedRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<BedResponse> getAvailableBeds() {
        return bedRepository.findByStatus(BedStatus.AVAILABLE).stream().map(this::toResponse).toList();
    }

    public BedResponse getBedById(Long id) {
        return toResponse(findBed(id));
    }

    @Transactional
    public BedResponse assignBed(Long id, Long patientId) {
        Bed bed = findBed(id);

        if (bed.getStatus() == BedStatus.OCCUPIED) {
            throw new BadRequestException("Bed is already occupied");
        }

        validatePatient(patientId);

        bed.setPatientId(patientId);
        bed.setStatus(BedStatus.OCCUPIED);

        return toResponse(bedRepository.save(bed));
    }

    @Transactional
    public BedResponse releaseBed(Long id) {
        Bed bed = findBed(id);

        if (bed.getStatus() == BedStatus.AVAILABLE) {
            throw new BadRequestException("Bed is already available");
        }

        bed.setPatientId(null);
        bed.setStatus(BedStatus.AVAILABLE);

        return toResponse(bedRepository.save(bed));
    }

    @Transactional
    public void deleteBed(Long id) {
        Bed bed = findBed(id);
        if (bed.getStatus() == BedStatus.OCCUPIED) {
            throw new BadRequestException("Cannot delete an occupied bed");
        }
        bedRepository.deleteById(id);
    }

    private void validatePatient(Long patientId) {
        try {
            patientClient.getPatientById(patientId);
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException("Patient not found with id: " + patientId);
        }
    }

    private Bed findBed(Long id) {
        return bedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found"));
    }

    private BedResponse toResponse(Bed bed) {
        return BedResponse.builder()
                .id(bed.getId())
                .bedNumber(bed.getBedNumber())
                .roomId(bed.getRoom().getId())
                .roomNumber(bed.getRoom().getRoomNumber())
                .status(bed.getStatus())
                .patientId(bed.getPatientId())
                .build();
    }
}