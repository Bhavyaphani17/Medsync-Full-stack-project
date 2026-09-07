package com.medsync.storage_service.service;

import com.medsync.common.dto.DoctorDTO;
import com.medsync.common.dto.PatientDTO;
import com.medsync.common.exception.BadRequestException;
import com.medsync.storage_service.dto.DischargeSummaryRequest;
import com.medsync.storage_service.dto.DischargeSummaryResponse;
import com.medsync.storage_service.feign.DoctorClient;
import com.medsync.storage_service.feign.PatientClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DischargeSummaryService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");

    private final PatientClient patientClient;
    private final DoctorClient doctorClient;
    private final PdfGenerationService pdfGenerationService;
    private final S3UploadService s3UploadService;

    public DischargeSummaryResponse generateDischargeSummary(DischargeSummaryRequest request) {
        PatientDTO patient = fetchPatient(request.getPatientId());
        DoctorDTO doctor = patient.getDoctorId() != null ? fetchDoctor(patient.getDoctorId()) : null;

        Map<String, Object> variables = new HashMap<>();
        variables.put("patientName", patient.getName());
        variables.put("patientEmail", patient.getEmail());
        variables.put("doctorName", doctor != null ? doctor.getName() : "Not assigned");
        variables.put("department", doctor != null ? doctor.getDepartment() : "N/A");
        variables.put("generatedDate", LocalDateTime.now().format(DATE_FORMATTER));
        variables.put("diagnosis", request.getDiagnosis());
        variables.put("treatmentSummary", nullSafe(request.getTreatmentSummary()));
        variables.put("medications", nullSafe(request.getMedications()));
        variables.put("followUpInstructions", nullSafe(request.getFollowUpInstructions()));

        byte[] pdfBytes = pdfGenerationService.generatePdf("discharge-summary", variables);

        String fileName = "discharge-summary-" + UUID.randomUUID() + ".pdf";
        String s3Key = "discharge-summaries/patient-" + patient.getId() + "/" + fileName;

        s3UploadService.upload(s3Key, pdfBytes, "application/pdf");
        String downloadUrl = s3UploadService.generatePresignedUrl(s3Key);

        return DischargeSummaryResponse.builder()
                .patientId(patient.getId())
                .patientName(patient.getName())
                .doctorName(doctor != null ? doctor.getName() : "Not assigned")
                .fileName(fileName)
                .s3Key(s3Key)
                .downloadUrl(downloadUrl)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    public String getDownloadUrl(String s3Key) {
        return s3UploadService.generatePresignedUrl(s3Key);
    }

    private PatientDTO fetchPatient(Long patientId) {
        try {
            return patientClient.getPatientById(patientId);
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException("Patient not found with id: " + patientId);
        }
    }

    private DoctorDTO fetchDoctor(Long doctorId) {
        try {
            return doctorClient.getDoctorById(doctorId);
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException("Doctor not found with id: " + doctorId);
        }
    }

    private String nullSafe(String value) {
        return value != null && !value.isBlank() ? value : "N/A";
    }
}