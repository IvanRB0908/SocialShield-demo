package com.socialshield.curso_defensa_api.controller;

import com.socialshield.curso_defensa_api.model.SecurityReportRequest;
import com.socialshield.curso_defensa_api.model.SecurityReportResponse;
import com.socialshield.curso_defensa_api.service.SocialPlatformService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/security-reports")
@CrossOrigin(origins = "http://localhost:4200")
public class SecurityReportController {

    private final SocialPlatformService socialPlatformService;

    public SecurityReportController(SocialPlatformService socialPlatformService) {
        this.socialPlatformService = socialPlatformService;
    }

    @PostMapping
    public ResponseEntity<SecurityReportResponse> createReport(@RequestBody SecurityReportRequest request) {
        if (request.category() == null || request.category().isBlank()
            || request.description() == null || request.description().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría y la descripción son obligatorias.");
        }
        if (request.category().length() > 80 || request.description().length() > 2_000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría o descripción excede el límite permitido.");
        }
        if (request.url() != null && !request.url().isBlank()
            && !request.url().trim().matches("^https?://.{1,2000}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La URL debe usar HTTP o HTTPS.");
        }

        SecurityReportResponse report = socialPlatformService.createReport(request);
        return ResponseEntity.created(URI.create("/api/security-reports/" + report.id())).body(report);
    }

    @GetMapping
    public List<SecurityReportResponse> getReports() {
        return socialPlatformService.getReports();
    }
}
