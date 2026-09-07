package com.medsync.storage_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DischargeSummaryRequest {

    @NotNull
    private Long patientId;

    @NotBlank
    private String diagnosis;

    private String treatmentSummary;

    private String medications;

    private String followUpInstructions;
}