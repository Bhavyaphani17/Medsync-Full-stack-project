package com.medsync.storage_service.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DischargeSummaryResponse {

    private Long patientId;
    private String patientName;
    private String doctorName;
    private String fileName;
    private String s3Key;
    private String downloadUrl;
    private LocalDateTime generatedAt;
}