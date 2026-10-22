package com.socialshield.curso_defensa_api.controller;

import com.socialshield.curso_defensa_api.model.EnrollmentCrudRequest;
import com.socialshield.curso_defensa_api.model.EnrollmentCrudResponse;
import com.socialshield.curso_defensa_api.model.EvaluationCrudRequest;
import com.socialshield.curso_defensa_api.model.EvaluationCrudResponse;
import com.socialshield.curso_defensa_api.model.UserProgressResponse;
import com.socialshield.curso_defensa_api.service.DatabaseCrudService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class EnrollmentController {

    private final DatabaseCrudService databaseCrudService;

    public EnrollmentController(DatabaseCrudService databaseCrudService) {
        this.databaseCrudService = databaseCrudService;
    }

    @PostMapping("/enrollments")
    public ResponseEntity<EnrollmentCrudResponse> enroll(@RequestBody EnrollmentCrudRequest request) {
        validate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(databaseCrudService.createEnrollment(request));
    }

    @GetMapping("/enrollments")
    public List<EnrollmentCrudResponse> getEnrollments() {
        return databaseCrudService.getEnrollments();
    }

    @GetMapping("/enrollments/{id}")
    public EnrollmentCrudResponse getEnrollment(@PathVariable int id) {
        return databaseCrudService.getEnrollment(id);
    }

    @PutMapping("/enrollments/{id}")
    public EnrollmentCrudResponse updateEnrollment(
        @PathVariable int id,
        @RequestBody EnrollmentCrudRequest request
    ) {
        validate(request);
        return databaseCrudService.updateEnrollment(id, request);
    }

    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable int id) {
        databaseCrudService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/progress")
    public UserProgressResponse getProgress(@PathVariable int userId) {
        return databaseCrudService.getProgress(userId);
    }

    private void validate(EnrollmentCrudRequest request) {
        if (request.userId() == null || request.userId() < 1
            || request.courseId() == null || request.courseId() < 1
            || request.progress() == null || request.progress() < 0 || request.progress() > 100) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Usuario, curso y progreso entre 0 y 100 son obligatorios."
            );
        }
    }

    // Inscripciones y evaluaciones describen el avance del alumno.
    @GetMapping("/evaluations")
    public List<EvaluationCrudResponse> getEvaluations() {
        return databaseCrudService.getEvaluations();
    }

    @GetMapping("/evaluations/{id}")
    public EvaluationCrudResponse getEvaluation(@PathVariable int id) {
        return databaseCrudService.getEvaluation(id);
    }

    @PostMapping("/evaluations")
    public ResponseEntity<EvaluationCrudResponse> createEvaluation(@RequestBody EvaluationCrudRequest request) {
        validateEvaluation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(databaseCrudService.createEvaluation(request));
    }

    @PutMapping("/evaluations/{id}")
    public EvaluationCrudResponse updateEvaluation(
        @PathVariable int id,
        @RequestBody EvaluationCrudRequest request
    ) {
        validateEvaluation(request);
        return databaseCrudService.updateEvaluation(id, request);
    }

    @DeleteMapping("/evaluations/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable int id) {
        databaseCrudService.deleteEvaluation(id);
        return ResponseEntity.noContent().build();
    }

    private void validateEvaluation(EvaluationCrudRequest request) {
        if (request.userId() == null || request.userId() < 1
            || request.courseId() == null || request.courseId() < 1
            || request.score() == null
            || request.score().compareTo(BigDecimal.ZERO) < 0
            || request.score().compareTo(BigDecimal.valueOf(100)) > 0
            || request.attempts() == null || request.attempts() < 1
            || request.passed() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Usuario, curso, calificación de 0 a 100, intentos y resultado son obligatorios."
            );
        }
    }
}
