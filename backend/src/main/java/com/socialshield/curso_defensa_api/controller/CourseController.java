package com.socialshield.curso_defensa_api.controller;

import com.socialshield.curso_defensa_api.model.CourseCrudRequest;
import com.socialshield.curso_defensa_api.model.CourseCrudResponse;
import com.socialshield.curso_defensa_api.model.HomePageResponse;
import com.socialshield.curso_defensa_api.service.CourseService;
import com.socialshield.curso_defensa_api.service.DatabaseCrudService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class CourseController {

    private final CourseService courseService;
    private final DatabaseCrudService databaseCrudService;

    public CourseController(CourseService courseService, DatabaseCrudService databaseCrudService) {
        this.courseService = courseService;
        this.databaseCrudService = databaseCrudService;
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/home")
    public HomePageResponse getHomePage() {
        return courseService.getHomePage();
    }

    @GetMapping("/courses")
    public List<CourseCrudResponse> getCourses() {
        return databaseCrudService.getCourses();
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseCrudResponse> createCourse(@RequestBody CourseCrudRequest request) {
        validateCourse(request, false);
        return ResponseEntity.status(HttpStatus.CREATED).body(databaseCrudService.createCourse(request));
    }

    @GetMapping("/courses/{id}")
    public CourseCrudResponse getCourse(@PathVariable int id) {
        return databaseCrudService.getCourse(id);
    }

    @PutMapping("/courses/{id}")
    public CourseCrudResponse updateCourse(@PathVariable int id, @RequestBody CourseCrudRequest request) {
        validateCourse(request, true);
        return databaseCrudService.updateCourse(id, request);
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable int id) {
        databaseCrudService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    private void validateCourse(CourseCrudRequest request, boolean requireAvailability) {
        if (request.title() == null || request.title().isBlank() || request.title().length() > 150
            || request.duration() == null || request.duration() < 1
            || request.level() == null || request.level().isBlank() || request.level().length() > 30) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El título, duración, lecciones y nivel del curso deben ser válidos."
            );
        }
        if (requireAvailability && request.available() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica si el curso está disponible.");
        }
        if (request.icon() != null && request.icon().length() > 50
            || request.tag() != null && request.tag().length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El icono o etiqueta excede el límite permitido.");
        }
    }
}
