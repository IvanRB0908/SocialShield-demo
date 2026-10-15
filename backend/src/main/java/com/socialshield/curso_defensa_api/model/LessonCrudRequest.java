package com.socialshield.curso_defensa_api.model;

public record LessonCrudRequest(
    Integer courseId,
    Integer instructorId,
    Integer number,
    String title,
    String content,
    Integer durationMinutes
) {
}
