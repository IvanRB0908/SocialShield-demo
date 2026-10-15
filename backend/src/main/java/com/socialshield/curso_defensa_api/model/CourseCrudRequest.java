package com.socialshield.curso_defensa_api.model;

public record CourseCrudRequest(
    String title,
    Integer duration,
    Boolean available,
    String icon,
    String tag,
    String description,
    String level
) {
}
