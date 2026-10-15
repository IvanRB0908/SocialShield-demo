package com.socialshield.curso_defensa_api.model;

import java.time.LocalDate;

public record EnrollmentCrudResponse(
    int id,
    int userId,
    String userName,
    String userEmail,
    int courseId,
    String courseTitle,
    String courseLevel,
    Integer courseDuration,
    LocalDate enrolledAt,
    int progress
) {
}
