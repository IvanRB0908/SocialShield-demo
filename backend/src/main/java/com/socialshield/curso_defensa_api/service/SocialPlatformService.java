package com.socialshield.curso_defensa_api.service;

import com.socialshield.curso_defensa_api.model.SecurityReportRequest;
import com.socialshield.curso_defensa_api.model.SecurityReportResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class SocialPlatformService {

    private static final RowMapper<SecurityReportResponse> REPORT_MAPPER = (resultSet, rowNumber) ->
        new SecurityReportResponse(
            resultSet.getObject("id", UUID.class).toString(),
            resultSet.getString("categoria"),
            resultSet.getString("descripcion"),
            resultSet.getString("url"),
            resultSet.getTimestamp("creado_en").toInstant()
        );

    private final JdbcTemplate jdbc;

    public SocialPlatformService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public SecurityReportResponse createReport(SecurityReportRequest request) {
        UUID id = UUID.randomUUID();
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        String url = request.url() == null || request.url().isBlank() ? null : request.url().trim();

        return jdbc.queryForObject("""
            INSERT INTO reportes_seguridad (id, categoria, descripcion, url, creado_en)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id, categoria, descripcion, url, creado_en
            """, REPORT_MAPPER, id, request.category().trim(), request.description().trim(), url, createdAt);
    }

    public List<SecurityReportResponse> getReports() {
        return jdbc.query("""
            SELECT id, categoria, descripcion, url, creado_en
            FROM reportes_seguridad
            ORDER BY creado_en, id
            """, REPORT_MAPPER);
    }
}
