package com.socialshield.curso_defensa_api.service;

import com.socialshield.curso_defensa_api.model.Course;
import com.socialshield.curso_defensa_api.model.CreateCourseRequest;
import com.socialshield.curso_defensa_api.model.HomePageResponse;
import com.socialshield.curso_defensa_api.model.Metric;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class CourseService {

    private final List<Course> courses = new ArrayList<>(List.of(
        new Course(
            "phishing",
            "Detección de Phishing",
            "Aprende a identificar correos, mensajes y sitios web fraudulentos antes de que sea tarde.",
            "4 semanas",
            "Principiante",
            12,
            "Más popular",
            "✉"
        ),
        new Course(
            "social-media",
            "Seguridad en Redes Sociales",
            "Protege tu identidad digital y reconoce perfiles falsos y estafas en plataformas sociales.",
            "3 semanas",
            "Intermedio",
            9,
            "Nuevo",
            "◎"
        ),
        new Course(
            "vishing",
            "Llamadas de Fraude",
            "Identifica vishing, llamadas de suplantación y técnicas de presión psicológica.",
            "2 semanas",
            "Principiante",
            7,
            "Esencial",
            "⌕"
        ),
        new Course(
            "psychology",
            "Manipulación Psicológica",
            "Comprende las tácticas de persuasión, urgencia y miedo que usan los atacantes.",
            "3 semanas",
            "Intermedio",
            10,
            "Avanzado",
            "▤"
        ),
        new Course(
            "messaging",
            "Seguridad en Mensajería",
            "Detecta cadenas de desinformación, enlaces maliciosos y estafas en WhatsApp y Telegram.",
            "2 semanas",
            "Principiante",
            8,
            "Nuevo",
            "▧"
        )
    ));

    public List<Course> getCourses() {
        return courses;
    }

    public Course createCourse(CreateCourseRequest request) {
        Course course = new Course(
            UUID.randomUUID().toString(),
            request.title(),
            "Curso añadido desde la plataforma SocialShield.",
            request.duration(),
            "Por definir",
            request.lessons(),
            request.isAvailable() ? "Nuevo" : "Próximamente",
            "＋"
        );
        courses.add(course);
        return course;
    }

    public HomePageResponse getHomePage() {
        return new HomePageResponse(
            "SocialShield",
            List.of(
                new Metric("12,000+", "Estudiantes formados"),
                new Metric("94%", "Tasa de satisfacción"),
                new Metric("3 cursos", "Especializados"),
                new Metric("Certificado", "Reconocido")
            ),
            List.of("Cursos", "¿Por qué nosotros?", "Recursos")
        );
    }
}
