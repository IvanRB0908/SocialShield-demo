package com.socialshield.curso_defensa_api;

import com.socialshield.curso_defensa_api.controller.ArcoController;
import com.socialshield.curso_defensa_api.model.ArcoCrudResponse;
import com.socialshield.curso_defensa_api.service.ArcoService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

// Pruebas HTTP con datos ficticios, sin PostgreSQL.
class ArcoControllerTest {
    private ArcoService service;
    private MockMvc mvc;
    private final ArcoCrudResponse row = new ArcoCrudResponse(1, "ACCESO", "Ejemplo ficticio", "PENDIENTE", null);
    private static final String BODY = "{\"type\":\"ACCESO\",\"description\":\"Ejemplo ficticio\",\"status\":\"PENDIENTE\"}";

    @BeforeEach
    void setUp() {
        service = mock(ArcoService.class);
        mvc = standaloneSetup(new ArcoController(service)).build();
    }

    @Test
    void supportsGetPostPutAndDelete() throws Exception {
        when(service.getRequests()).thenReturn(List.of(row));
        when(service.getRequest(1)).thenReturn(row);
        when(service.createRequest(any())).thenReturn(row);
        when(service.updateRequest(eq(1), any())).thenReturn(row);
        mvc.perform(get("/api/arco-requests")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1));
        mvc.perform(get("/api/arco-requests/1")).andExpect(status().isOk()).andExpect(jsonPath("$.type").value("ACCESO"));
        mvc.perform(post("/api/arco-requests").contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/arco-requests/1"));
        mvc.perform(put("/api/arco-requests/1").contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PENDIENTE"));
        mvc.perform(delete("/api/arco-requests/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(service).deleteRequest(1);
    }

    @Test
    void acceptsAllFourRightsAndDefaultsStatusOnPost() throws Exception {
        when(service.createRequest(any())).thenReturn(row);
        for (String type : List.of("ACCESO", "RECTIFICACION", "CANCELACION", "OPOSICION")) {
            mvc.perform(post("/api/arco-requests").contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"" + type + "\",\"description\":\"Ejemplo ficticio\"}"))
                .andExpect(status().isCreated());
        }
    }

    @Test
    void rejectsInvalidBodiesBeforeCallingService() throws Exception {
        for (String body : List.of("{}", "null", BODY.replace("ACCESO", "OTRO"),
            BODY.replace("Ejemplo ficticio", "   "), BODY.replace("PENDIENTE", "OTRO"),
            BODY.replace("Ejemplo ficticio", "x".repeat(2001)),
            BODY.replace("}", ",\"response\":\"" + "x".repeat(2001) + "\"}"))) {
            mvc.perform(post("/api/arco-requests").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        }
        mvc.perform(put("/api/arco-requests/1").contentType(MediaType.APPLICATION_JSON)
            .content("{\"type\":\"ACCESO\",\"description\":\"Ejemplo ficticio\"}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void returnsNotFoundForMissingRequests() throws Exception {
        var missing = new ResponseStatusException(HttpStatus.NOT_FOUND);
        when(service.getRequest(99)).thenThrow(missing);
        when(service.updateRequest(eq(99), any())).thenThrow(missing);
        doThrow(missing).when(service).deleteRequest(99);
        mvc.perform(get("/api/arco-requests/99")).andExpect(status().isNotFound());
        mvc.perform(put("/api/arco-requests/99").contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isNotFound());
        mvc.perform(delete("/api/arco-requests/99")).andExpect(status().isNotFound());
    }
}
