package com.socialshield.curso_defensa_api.service;

import com.socialshield.curso_defensa_api.model.HomePageResponse;
import com.socialshield.curso_defensa_api.model.Metric;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final DatabaseCrudService databaseCrudService;

    public CourseService(DatabaseCrudService databaseCrudService) {
        this.databaseCrudService = databaseCrudService;
    }

    public HomePageResponse getHomePage() {
        return new HomePageResponse(
            "SocialShield",
            List.of(
                new Metric("12,000+", "Estudiantes formados"),
                new Metric("94%", "Tasa de satisfacción"),
                new Metric(databaseCrudService.getCourseCount() + " cursos", "Especializados"),
                new Metric("Certificado", "Reconocido")
            ),
            List.of("Cursos", "¿Por qué nosotros?", "Recursos")
        );
    }
}
