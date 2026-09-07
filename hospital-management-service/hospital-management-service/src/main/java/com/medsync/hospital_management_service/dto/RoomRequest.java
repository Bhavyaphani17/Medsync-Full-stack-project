package com.medsync.hospital_management_service.dto;

import com.medsync.hospital_management_service.enums.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequest {

    @NotBlank
    private String roomNumber;

    @NotNull
    private RoomType roomType;

    @NotNull
    private Integer floor;
}