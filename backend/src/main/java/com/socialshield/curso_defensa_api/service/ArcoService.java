package com.socialshield.curso_defensa_api.service;

import com.socialshield.curso_defensa_api.model.ArcoCrudRequest;
import com.socialshield.curso_defensa_api.model.ArcoCrudResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ArcoService {
    private static final RowMapper<ArcoCrudResponse> MAPPER = (rs, row) -> new ArcoCrudResponse(
        rs.getInt("id"), rs.getString("tipo"), rs.getString("descripcion"),
        rs.getString("estado"), rs.getString("respuesta")
    );
    private final JdbcTemplate jdbc;

    public ArcoService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<ArcoCrudResponse> getRequests() {
        return jdbc.query("SELECT * FROM solicitudes_arco ORDER BY id", MAPPER);
    }

    public ArcoCrudResponse getRequest(int id) {
        var rows = jdbc.query("SELECT * FROM solicitudes_arco WHERE id = ?", MAPPER, id);
        if (rows.isEmpty()) throw notFound();
        return rows.getFirst();
    }

    public ArcoCrudResponse createRequest(ArcoCrudRequest request) {
        return jdbc.queryForObject("""
            INSERT INTO solicitudes_arco (tipo, descripcion, estado, respuesta)
            VALUES (?, ?, ?, ?) RETURNING id, tipo, descripcion, estado, respuesta
            """, MAPPER, request.type(), request.description().strip(),
            request.status() == null ? "PENDIENTE" : request.status(), request.response());
    }

    public ArcoCrudResponse updateRequest(int id, ArcoCrudRequest request) {
        var rows = jdbc.query("""
            UPDATE solicitudes_arco SET tipo = ?, descripcion = ?, estado = ?, respuesta = ?
            WHERE id = ? RETURNING id, tipo, descripcion, estado, respuesta
            """, MAPPER, request.type(), request.description().strip(), request.status(), request.response(), id);
        if (rows.isEmpty()) throw notFound();
        return rows.getFirst();
    }

    public void deleteRequest(int id) {
        if (jdbc.update("DELETE FROM solicitudes_arco WHERE id = ?", id) == 0) throw notFound();
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la solicitud ARCO indicada.");
    }
}
