package com.socialshield.curso_defensa_api.model;

public record Course(
    String id,
    String title,
    String description,
    String duration,
    String level,
    int lessons,
    String tag,
    String icon
) {
}
