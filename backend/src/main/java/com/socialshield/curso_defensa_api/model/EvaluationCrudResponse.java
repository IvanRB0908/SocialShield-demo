package com.socialshield.curso_defensa_api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EvaluationCrudResponse(
    int id,
    int userId,
    String userName,
    String userEmail,
    int courseId,
    String courseTitle,
    BigDecimal score,
    int attempts,
    boolean passed,
    LocalDate evaluatedAt
) {
}
