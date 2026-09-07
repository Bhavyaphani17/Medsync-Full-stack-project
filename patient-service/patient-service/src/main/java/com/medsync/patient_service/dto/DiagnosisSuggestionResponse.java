package com.medsync.patient_service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiagnosisSuggestionResponse {

    private Long patientId;
    private String patientName;
    private String symptoms;
    private String aiSuggestions;
    private String disclaimer;
}