package com.medsync.hospital_management_service.dto;

import com.medsync.hospital_management_service.enums.RoomType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private RoomType roomType;
    private Integer floor;
    private int totalBeds;
    private int availableBeds;
}