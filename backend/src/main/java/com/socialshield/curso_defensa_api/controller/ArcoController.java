package com.socialshield.curso_defensa_api.controller;

import com.socialshield.curso_defensa_api.model.ArcoCrudRequest;
import com.socialshield.curso_defensa_api.model.ArcoCrudResponse;
import com.socialshield.curso_defensa_api.service.ArcoService;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/arco-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class ArcoController {
    private final ArcoService service;

    public ArcoController(ArcoService service) { this.service = service; }

    @GetMapping
    public List<ArcoCrudResponse> getRequests() { return service.getRequests(); }

    @GetMapping("/{id}")
    public ArcoCrudResponse getRequest(@PathVariable int id) { return service.getRequest(id); }

    @PostMapping
    public ResponseEntity<ArcoCrudResponse> createRequest(@RequestBody ArcoCrudRequest request) {
        validate(request, false);
        var created = service.createRequest(request);
        return ResponseEntity.created(URI.create("/api/arco-requests/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ArcoCrudResponse updateRequest(@PathVariable int id, @RequestBody ArcoCrudRequest request) {
        validate(request, true);
        return service.updateRequest(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable int id) {
        service.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    private void validate(ArcoCrudRequest request, boolean updating) {
        if (request == null || request.type() == null
            || !List.of("ACCESO", "RECTIFICACION", "CANCELACION", "OPOSICION").contains(request.type())
            || request.description() == null || request.description().isBlank() || request.description().length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica un derecho ARCO y una descripción de hasta 2000 caracteres.");
        }
        if (updating && request.status() == null || request.status() != null
            && !List.of("PENDIENTE", "EN_REVISION", "RESUELTA", "RECHAZADA").contains(request.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica un estado ARCO válido.");
        }
        if (request.response() != null && request.response().length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La respuesta no puede superar los 2000 caracteres.");
        }
    }
}
