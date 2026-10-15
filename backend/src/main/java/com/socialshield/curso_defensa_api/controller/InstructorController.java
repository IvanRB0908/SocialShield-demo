package com.socialshield.curso_defensa_api.controller;

import com.socialshield.curso_defensa_api.model.InstructorCrudRequest;
import com.socialshield.curso_defensa_api.model.InstructorCrudResponse;
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
@RequestMapping("/api/instructors")
@CrossOrigin(origins = "http://localhost:4200")
public class InstructorController {

    private final DatabaseCrudService databaseCrudService;

    public InstructorController(DatabaseCrudService databaseCrudService) {
        this.databaseCrudService = databaseCrudService;
    }

    @GetMapping
    public List<InstructorCrudResponse> getInstructors() {
        return databaseCrudService.getInstructors();
    }

    @GetMapping("/{id}")
    public InstructorCrudResponse getInstructor(@PathVariable int id) {
        return databaseCrudService.getInstructor(id);
    }

    @PostMapping
    public ResponseEntity<InstructorCrudResponse> createInstructor(@RequestBody InstructorCrudRequest request) {
        validate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(databaseCrudService.createInstructor(request));
    }

    @PutMapping("/{id}")
    public InstructorCrudResponse updateInstructor(
        @PathVariable int id,
        @RequestBody InstructorCrudRequest request
    ) {
        validate(request);
        return databaseCrudService.updateInstructor(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable int id) {
        databaseCrudService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }

    private void validate(InstructorCrudRequest request) {
        if (request.name() == null || request.name().isBlank() || request.name().length() > 100
            || request.email() != null && request.email().length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre o correo del instructor no es válido.");
        }
    }
}
