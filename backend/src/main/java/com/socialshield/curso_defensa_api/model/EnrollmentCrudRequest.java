package com.socialshield.curso_defensa_api.model;

import java.time.LocalDate;

public record EnrollmentCrudRequest(
    Integer userId,
    Integer courseId,
    LocalDate enrolledAt,
    Integer progress
) {
}
