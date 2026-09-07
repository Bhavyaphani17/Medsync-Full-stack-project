package com.medsync.hospital_management_service.dto;

import com.medsync.common.enums.BedStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BedResponse {
    private Long id;
    private String bedNumber;
    private Long roomId;
    private String roomNumber;
    private BedStatus status;
    private Long patientId;
}