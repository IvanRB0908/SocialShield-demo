package com.socialshield.curso_defensa_api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EvaluationCrudRequest(
    Integer userId,
    Integer courseId,
    BigDecimal score,
    Integer attempts,
    Boolean passed,
    LocalDate evaluatedAt
) {
}
