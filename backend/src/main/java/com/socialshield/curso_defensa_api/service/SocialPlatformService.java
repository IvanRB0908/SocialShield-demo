package com.socialshield.curso_defensa_api.service;

import com.socialshield.curso_defensa_api.model.SecurityReportRequest;
import com.socialshield.curso_defensa_api.model.SecurityReportResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SocialPlatformService {

    private final List<SecurityReportResponse> reports = new CopyOnWriteArrayList<>();

    public SecurityReportResponse createReport(SecurityReportRequest request) {
        String url = request.url() == null || request.url().isBlank() ? null : request.url().trim();
        SecurityReportResponse report = new SecurityReportResponse(
            UUID.randomUUID().toString(),
            request.category().trim(),
            request.description().trim(),
            url,
            Instant.now()
        );
        reports.add(report);
        return report;
    }

    public List<SecurityReportResponse> getReports() {
        return List.copyOf(reports);
    }
}
