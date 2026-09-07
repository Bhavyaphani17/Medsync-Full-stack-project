package com.medsync.patient_service.service;

import com.medsync.common.dto.DoctorDTO;
import com.medsync.common.dto.PatientDTO;
import com.medsync.common.exception.BadRequestException;
import com.medsync.common.exception.ResourceNotFoundException;
import com.medsync.patient_service.dto.DiagnosisSuggestionRequest;
import com.medsync.patient_service.dto.DiagnosisSuggestionResponse;
import com.medsync.patient_service.dto.DietRecommendationRequest;
import com.medsync.patient_service.dto.DietRecommendationResponse;
import com.medsync.patient_service.dto.PatientRequest;
import com.medsync.patient_service.entity.Patient;
import com.medsync.patient_service.feign.DoctorClient;
import com.medsync.patient_service.repository.PatientRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorClient doctorClient;
    private final ChatClient.Builder chatClientBuilder;

    @Transactional
    public PatientDTO addPatient(PatientRequest request) {
        validateDoctor(request.getDoctorId());

        Patient patient = Patient.builder()
                .name(request.getName())
                .email(request.getEmail())
                .age(request.getAge())
                .gender(request.getGender())
                .disease(request.getDisease())
                .doctorId(request.getDoctorId())
                .build();

        return toDto(patientRepository.save(patient));
    }

    public List<PatientDTO> getPatients() {
        return patientRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public PatientDTO getPatientById(Long id) {
        return toDto(findPatient(id));
    }

    @Transactional
    public PatientDTO updatePatient(Long id, PatientRequest request) {
        Patient patient = findPatient(id);
        validateDoctor(request.getDoctorId());

        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());
        patient.setDisease(request.getDisease());
        patient.setDoctorId(request.getDoctorId());

        return toDto(patientRepository.save(patient));
    }

    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found");
        }
        patientRepository.deleteById(id);
    }

    @Transactional
    public PatientDTO assignDoctor(Long id, Long doctorId) {
        Patient patient = findPatient(id);
        validateDoctor(doctorId);
        patient.setDoctorId(doctorId);
        return toDto(patientRepository.save(patient));
    }

    public DietRecommendationResponse generateDietRecommendations(DietRecommendationRequest request) {
        Patient patient = findPatient(request.getPatientId());

        String prompt = """
                You are a clinical nutrition assistant. Based on the patient's condition, provide practical diet recommendations.
                Patient name: %s
                Age: %d
                Gender: %s
                Condition: %s
                Existing disease notes: %s

                Provide a concise, structured response with breakfast, lunch, dinner, snacks, and foods to avoid.
                """.formatted(
                patient.getName(),
                patient.getAge(),
                patient.getGender(),
                request.getCondition(),
                patient.getDisease() != null ? patient.getDisease() : "N/A"
        );

        String recommendations = chatClientBuilder.build()
                .prompt()
                .user(prompt)
                .call()
                .content();

        return DietRecommendationResponse.builder()
                .patientId(patient.getId())
                .patientName(patient.getName())
                .condition(request.getCondition())
                .recommendations(recommendations)
                .build();
    }

    public DiagnosisSuggestionResponse generateDiagnosisSuggestions(DiagnosisSuggestionRequest request) {
        Patient patient = findPatient(request.getPatientId());

        String prompt = """
                You are a clinical decision-support assistant helping a doctor think through a case.
                You do NOT provide a definitive diagnosis and you are NOT a substitute for clinical judgment.

                Patient name: %s
                Age: %d
                Gender: %s
                Existing disease notes: %s
                Reported symptoms: %s

                Provide a concise, structured response with exactly these sections:
                1. Possible conditions to consider (not a diagnosis, just possibilities to rule in/out)
                2. Suggested questions to ask or tests to consider
                3. General treatment/prescription considerations a doctor might explore

                Keep it clinically grounded, avoid overconfident claims, and explicitly note where uncertainty remains.
                """.formatted(
                patient.getName(),
                patient.getAge(),
                patient.getGender(),
                patient.getDisease() != null ? patient.getDisease() : "N/A",
                request.getSymptoms()
        );

        String suggestions = chatClientBuilder.build()
                .prompt()
                .user(prompt)
                .call()
                .content();

        return DiagnosisSuggestionResponse.builder()
                .patientId(patient.getId())
                .patientName(patient.getName())
                .symptoms(request.getSymptoms())
                .aiSuggestions(suggestions)
                .disclaimer("AI-generated suggestion. Not a diagnosis. Doctor must independently review and verify before clinical use.")
                .build();
    }

    private Patient findPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    private void validateDoctor(Long doctorId) {
        if (doctorId == null) {
            return;
        }
        try {
            doctorClient.getDoctorById(doctorId);
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException("Doctor not found with id: " + doctorId);
        }
    }

    private PatientDTO toDto(Patient patient) {
        String doctorName = null;
        if (patient.getDoctorId() != null) {
            try {
                DoctorDTO doctor = doctorClient.getDoctorById(patient.getDoctorId());
                doctorName = doctor.getName();
            } catch (FeignException ex) {
                doctorName = "Unknown";
            }
        }

        return PatientDTO.builder()
                .id(patient.getId())
                .name(patient.getName())
                .email(patient.getEmail())
                .age(patient.getAge())
                .gender(patient.getGender())
                .disease(patient.getDisease())
                .doctorId(patient.getDoctorId())
                .doctorName(doctorName)
                .build();
    }
}