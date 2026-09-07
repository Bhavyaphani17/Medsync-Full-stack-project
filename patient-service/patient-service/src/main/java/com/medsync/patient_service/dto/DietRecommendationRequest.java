package com.medsync.patient_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DietRecommendationRequest {

    @NotNull
    private Long patientId;

    @NotBlank
    private String condition;
}
