package com.socialshield.curso_defensa_api.model;

public record CourseCrudResponse(
    int id,
    String title,
    int duration,
    int lessons,
    boolean available,
    String icon,
    String tag,
    String description,
    String level
) {
}
