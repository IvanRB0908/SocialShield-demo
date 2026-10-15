package com.socialshield.curso_defensa_api.model;

import java.time.LocalDate;

public record EnrollmentResponse(
    String id,
    String userId,
    String courseId,
    String courseTitle,
    LocalDate enrolledAt,
    int progress
) {
}
