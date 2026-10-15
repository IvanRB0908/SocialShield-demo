package com.socialshield.curso_defensa_api;

import com.socialshield.curso_defensa_api.controller.SecurityReportController;
import com.socialshield.curso_defensa_api.service.SocialPlatformService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SocialPlatformControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
            new SecurityReportController(new SocialPlatformService())
        ).build();
    }

    @Test
    void shouldCreateAndListSecurityReports() throws Exception {
        mockMvc.perform(post("/api/security-reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "category": "phishing",
                      "description": "Recibí un correo con un enlace sospechoso.",
                      "url": "https://example.com/suspicious"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.category").value("phishing"))
            .andExpect(jsonPath("$.url").value("https://example.com/suspicious"));

        mockMvc.perform(get("/api/security-reports"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldRejectNonHttpSecurityReportUrl() throws Exception {
        mockMvc.perform(post("/api/security-reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "category": "phishing",
                      "description": "Enlace sospechoso.",
                      "url": "javascript:alert(1)"
                    }
                    """))
            .andExpect(status().isBadRequest());
    }
}
