package com.socialshield.curso_defensa_api.controller;

import com.socialshield.curso_defensa_api.model.LessonCrudRequest;
import com.socialshield.curso_defensa_api.model.LessonCrudResponse;
import com.socialshield.curso_defensa_api.service.DatabaseCrudService;
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

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "http://localhost:4200")
public class LessonController {

    private final DatabaseCrudService databaseCrudService;

    public LessonController(DatabaseCrudService databaseCrudService) {
        this.databaseCrudService = databaseCrudService;
    }

    @GetMapping
    public List<LessonCrudResponse> getLessons() {
        return databaseCrudService.getLessons();
    }

    @GetMapping("/{id}")
    public LessonCrudResponse getLesson(@PathVariable int id) {
        return databaseCrudService.getLesson(id);
    }

    @PostMapping
    public ResponseEntity<LessonCrudResponse> createLesson(@RequestBody LessonCrudRequest request) {
        validate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(databaseCrudService.createLesson(request));
    }

    @PutMapping("/{id}")
    public LessonCrudResponse updateLesson(@PathVariable int id, @RequestBody LessonCrudRequest request) {
        validate(request);
        return databaseCrudService.updateLesson(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable int id) {
        databaseCrudService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    private void validate(LessonCrudRequest request) {
        if (request.courseId() == null || request.courseId() < 1
            || request.instructorId() == null || request.instructorId() < 1
            || request.number() == null || request.number() < 1
            || request.title() == null || request.title().isBlank() || request.title().length() > 150
            || request.durationMinutes() == null || request.durationMinutes() < 1) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Curso, instructor, número, título y duración positiva son obligatorios."
            );
        }
    }
}
