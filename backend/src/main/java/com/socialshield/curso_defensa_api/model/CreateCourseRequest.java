package com.socialshield.curso_defensa_api.model;

public record CreateCourseRequest(
    String title,
    String duration,
    int lessons,
    boolean isAvailable
) {
}