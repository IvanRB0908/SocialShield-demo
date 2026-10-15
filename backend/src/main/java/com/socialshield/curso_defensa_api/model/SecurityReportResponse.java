package com.socialshield.curso_defensa_api.model;

import java.time.Instant;

public record SecurityReportResponse(
    String id,
    String category,
    String description,
    String url,
    Instant createdAt
) {
}
