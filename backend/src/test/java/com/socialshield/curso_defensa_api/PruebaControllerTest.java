package com.socialshield.curso_defensa_api;

import com.socialshield.curso_defensa_api.controller.PruebaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PruebaControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PruebaController()).build();
    }

    @Test
    void shouldReturnBackendConfirmation() throws Exception {
        mockMvc.perform(get("/api/prueba"))
                .andExpect(status().isOk())
                .andExpect(content().string("El backend de SocialShield funciona correctamente."));
    }
}
