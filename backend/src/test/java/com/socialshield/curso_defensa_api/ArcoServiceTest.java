package com.socialshield.curso_defensa_api;

import com.socialshield.curso_defensa_api.model.*;
import com.socialshield.curso_defensa_api.service.ArcoService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ArcoServiceTest {
    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
    private final ArcoService service = new ArcoService(jdbc);
    private final ArcoCrudResponse row = new ArcoCrudResponse(1, "ACCESO", "Ejemplo ficticio", "PENDIENTE", null);

    @Test
    void createsWithDefaultStatusAndParameterizedDescription() {
        when(jdbc.queryForObject(anyString(), any(RowMapper.class), eq("ACCESO"),
            eq("Ejemplo ficticio"), eq("PENDIENTE"), isNull())).thenReturn(row);
        assertEquals(row, service.createRequest(new ArcoCrudRequest("ACCESO", "  Ejemplo ficticio  ", null, null)));
        verify(jdbc).queryForObject(contains("VALUES (?, ?, ?, ?)"), any(RowMapper.class),
            eq("ACCESO"), eq("Ejemplo ficticio"), eq("PENDIENTE"), isNull());
    }

    @Test
    void updatesUsingIdAndReturnsUpdatedRecord() {
        when(jdbc.query(anyString(), any(RowMapper.class), eq("ACCESO"), eq("Ejemplo ficticio"),
            eq("RESUELTA"), eq("Respuesta ficticia"), eq(1))).thenReturn(List.of(row));
        assertEquals(row, service.updateRequest(1,
            new ArcoCrudRequest("ACCESO", "Ejemplo ficticio", "RESUELTA", "Respuesta ficticia")));
        verify(jdbc).query(contains("WHERE id = ? RETURNING"), any(RowMapper.class), eq("ACCESO"),
            eq("Ejemplo ficticio"), eq("RESUELTA"), eq("Respuesta ficticia"), eq(1));
    }

    @Test
    void missingReadUpdateAndDeleteReturn404() {
        when(jdbc.query(anyString(), any(RowMapper.class), eq(99))).thenReturn(List.of());
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> service.getRequest(99)).getStatusCode().value());
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> service.updateRequest(99,
            new ArcoCrudRequest("ACCESO", "Ejemplo ficticio", "PENDIENTE", null))).getStatusCode().value());
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> service.deleteRequest(99)).getStatusCode().value());
    }

    @Test
    void deletesOnlyRequestedId() {
        when(jdbc.update("DELETE FROM solicitudes_arco WHERE id = ?", 1)).thenReturn(1);
        service.deleteRequest(1);
        verify(jdbc).update("DELETE FROM solicitudes_arco WHERE id = ?", 1);
    }
}
