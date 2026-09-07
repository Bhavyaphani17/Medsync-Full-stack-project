package com.medsync.storage_service.controller;

import com.medsync.storage_service.dto.DischargeSummaryRequest;
import com.medsync.storage_service.dto.DischargeSummaryResponse;
import com.medsync.storage_service.service.DischargeSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

    private final DischargeSummaryService dischargeSummaryService;

    @PostMapping("/discharge-summary")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public DischargeSummaryResponse generate(@Valid @RequestBody DischargeSummaryRequest request) {
        return dischargeSummaryService.generateDischargeSummary(request);
    }

    @GetMapping("/discharge-summary/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public Map<String, String> download(@RequestParam String key) {
        return Map.of("downloadUrl", dischargeSummaryService.getDownloadUrl(key));
    }
}