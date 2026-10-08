package com.socialshield.curso_defensa_api.model;

import java.util.List;

public record HomePageResponse(
    String platformName,
    List<Metric> stats,
    List<String> sections
) {
}
