package com.medsync.patient_service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DietRecommendationResponse {

    private Long patientId;
    private String patientName;
    private String condition;
    private String recommendations;
}
