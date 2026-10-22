package com.socialshield.curso_defensa_api.controller;

import com.socialshield.curso_defensa_api.model.GoogleLoginRequest;
import com.socialshield.curso_defensa_api.model.GoogleAuthConfigResponse;
import com.socialshield.curso_defensa_api.model.LoginRequest;
import com.socialshield.curso_defensa_api.model.RegisterRequest;
import com.socialshield.curso_defensa_api.model.UserCrudRequest;
import com.socialshield.curso_defensa_api.model.UserCrudResponse;
import com.socialshield.curso_defensa_api.model.UserResponse;
import com.socialshield.curso_defensa_api.service.DatabaseCrudService;
import com.socialshield.curso_defensa_api.service.GoogleLoginService;
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
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class UserAuthController {

    private final DatabaseCrudService databaseCrudService;
    private final GoogleLoginService googleLoginService;

    public UserAuthController(DatabaseCrudService databaseCrudService, GoogleLoginService googleLoginService) {
        this.databaseCrudService = databaseCrudService;
        this.googleLoginService = googleLoginService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        if (request.name() == null || request.name().isBlank()
            || !isValidEmail(request.email())
            || request.password() == null || request.password().length() < 8 || request.password().length() > 128) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Proporciona un nombre, correo válido y contraseña de entre 8 y 128 caracteres."
            );
        }
        if (request.name().length() > 100 || request.email().length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre o correo excede el límite permitido.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(databaseCrudService.register(request));
    }

    @PostMapping("/auth/login")
    public UserResponse login(@RequestBody LoginRequest request) {
        if (request.email() == null || request.email().isBlank()
            || request.password() == null || request.password().isBlank() || request.password().length() > 128) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo y la contraseña son obligatorios.");
        }
        return databaseCrudService.login(request);
    }

    @PostMapping("/auth/google")
    public UserResponse googleLogin(@RequestBody GoogleLoginRequest request) {
        if (request == null || request.idToken() == null
            || request.idToken().isBlank() || request.idToken().length() > 10_000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proporciona un ID token de Google válido.");
        }
        return googleLoginService.login(request.idToken());
    }

    @GetMapping("/auth/google/config")
    public GoogleAuthConfigResponse googleAuthConfig() {
        return new GoogleAuthConfigResponse(googleLoginService.getClientId());
    }

    @GetMapping("/users")
    public List<UserCrudResponse> getUsers() {
        return databaseCrudService.getUsers();
    }

    @GetMapping("/users/{id}")
    public UserCrudResponse getUser(@PathVariable int id) {
        return databaseCrudService.getUser(id);
    }

    @PutMapping("/users/{id}")
    public UserCrudResponse updateUser(@PathVariable int id, @RequestBody UserCrudRequest request) {
        validateProfile(request);
        return databaseCrudService.updateUser(id, request);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        databaseCrudService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private void validateProfile(UserCrudRequest request) {
        if (request.name() == null || request.name().isBlank() || request.name().length() > 100
            || !isValidEmail(request.email()) || request.email().length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre y correo deben ser válidos.");
        }
        if (request.password() != null && !request.password().isBlank()
            && (request.password().length() < 8 || request.password().length() > 128)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La contraseña debe tener entre 8 y 128 caracteres."
            );
        }
        if (request.phone() != null && request.phone().length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El teléfono excede el límite permitido.");
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.length() > 120 || email.chars().anyMatch(Character::isWhitespace)) {
            return false;
        }
        int separator = email.indexOf('@');
        if (separator <= 0 || separator != email.lastIndexOf('@') || separator == email.length() - 1) {
            return false;
        }
        String domain = email.substring(separator + 1);
        int dot = domain.indexOf('.');
        return dot > 0 && dot < domain.length() - 1;
    }
}
