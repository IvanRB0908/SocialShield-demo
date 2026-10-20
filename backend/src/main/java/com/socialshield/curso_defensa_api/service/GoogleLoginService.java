package com.socialshield.curso_defensa_api.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.socialshield.curso_defensa_api.model.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class GoogleLoginService {

    private final DatabaseCrudService databaseCrudService;
    private final GoogleIdTokenVerifier verifier;

    public GoogleLoginService(
        DatabaseCrudService databaseCrudService,
        @Value("${GOOGLE_CLIENT_ID:}") String clientId
    ) {
        this.databaseCrudService = databaseCrudService;
        this.verifier = clientId.isBlank()
            ? null
            : new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(List.of(clientId))
                .build();
    }

    public UserResponse login(String idToken) {
        if (verifier == null) {
            throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "El inicio de sesión con Google no está configurado. Define GOOGLE_CLIENT_ID."
            );
        }

        GoogleIdToken verifiedToken;
        try {
            verifiedToken = verifier.verify(idToken);
        } catch (GeneralSecurityException | IOException | IllegalArgumentException exception) {
            throw invalidToken();
        }
        if (verifiedToken == null) {
            throw invalidToken();
        }

        GoogleIdToken.Payload payload = verifiedToken.getPayload();
        String email = payload.getEmail();
        if (!Boolean.TRUE.equals(payload.getEmailVerified()) || !isValidEmail(email)) {
            throw invalidToken();
        }

        Object nameClaim = payload.get("name");
        String name = nameClaim instanceof String value && !value.isBlank()
            ? value.trim()
            : email.substring(0, email.indexOf('@'));
        if (name.length() > 100) {
            name = name.substring(0, 100);
        }
        return databaseCrudService.loginWithGoogle(name, email);
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

    private ResponseStatusException invalidToken() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El token de Google no es válido.");
    }
}
