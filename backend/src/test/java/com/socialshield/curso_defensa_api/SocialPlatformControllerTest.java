package com.socialshield.curso_defensa_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class SocialPlatformControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void shouldCreateAndListSecurityReports() throws Exception {
        MockMvc mockMvc = webAppContextSetup(webApplicationContext).build();
        String category = "phishing-" + java.util.UUID.randomUUID();
        mockMvc.perform(post("/api/security-reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "category": "%s",
                      "description": "Recibí un correo con un enlace sospechoso.",
                      "url": "https://example.com/suspicious"
                    }
                    """.formatted(category)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.category").value(category))
            .andExpect(jsonPath("$.url").value("https://example.com/suspicious"));

        mockMvc.perform(get("/api/security-reports"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.category == '" + category + "')]", hasSize(1)));
    }

    @Test
    void shouldRejectNonHttpSecurityReportUrl() throws Exception {
        MockMvc mockMvc = webAppContextSetup(webApplicationContext).build();
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
