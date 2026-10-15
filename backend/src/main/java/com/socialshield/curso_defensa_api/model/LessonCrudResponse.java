package com.socialshield.curso_defensa_api.model;

public record LessonCrudResponse(
    int id,
    int courseId,
    String courseTitle,
    String courseLevel,
    int number,
    String title,
    String content,
    int durationMinutes,
    String instructor,
    String instructorEmail
) {
}
