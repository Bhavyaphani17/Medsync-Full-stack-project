package com.medsync.hospital_management_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BedRequest {

    @NotBlank
    private String bedNumber;

    @NotNull
    private Long roomId;
}