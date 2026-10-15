package com.socialshield.curso_defensa_api.model;

public record UserCrudRequest(
    String name,
    String email,
    String password,
    String phone,
    String bio,
    Boolean active
) {
}
