package com.socialshield.curso_defensa_api.model;

public record UserCrudResponse(
    int id,
    String name,
    String email,
    String phone,
    String bio,
    boolean active
) {
}
