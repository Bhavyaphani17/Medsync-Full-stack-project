package com.medsync.patient_service.controller;

import com.medsync.common.dto.PatientDTO;
import com.medsync.patient_service.dto.DietRecommendationRequest;
import com.medsync.patient_service.dto.DietRecommendationResponse;
import com.medsync.patient_service.dto.PatientRequest;
import com.medsync.patient_service.dto.DiagnosisSuggestionRequest;
import com.medsync.patient_service.dto.DiagnosisSuggestionResponse;
import com.medsync.patient_service.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public PatientDTO addPatient(@Valid @RequestBody PatientRequest request) {
        return patientService.addPatient(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public List<PatientDTO> getPatients() {
        return patientService.getPatients();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public PatientDTO getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public PatientDTO updatePatient(@PathVariable Long id, @Valid @RequestBody PatientRequest request) {
        return patientService.updatePatient(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return Map.of("message", "Patient deleted");
    }

    @PutMapping("/assign/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public PatientDTO assignDoctor(@PathVariable Long id, @RequestParam Long doctorId) {
        return patientService.assignDoctor(id, doctorId);
    }

    @PostMapping("/diet-recommendations")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public DietRecommendationResponse dietRecommendations(@Valid @RequestBody DietRecommendationRequest request) {
        return patientService.generateDietRecommendations(request);
    }

    @PostMapping("/diagnosis-suggestions")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public DiagnosisSuggestionResponse diagnosisSuggestions(@Valid @RequestBody DiagnosisSuggestionRequest request) {
        return patientService.generateDiagnosisSuggestions(request);
    }
}
