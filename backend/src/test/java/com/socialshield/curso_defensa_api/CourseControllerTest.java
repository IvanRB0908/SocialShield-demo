package com.socialshield.curso_defensa_api;

import com.socialshield.curso_defensa_api.controller.CourseController;
import com.socialshield.curso_defensa_api.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourseControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CourseController(new CourseService())).build();
    }

    @Test
    void shouldReturnHomePageData() throws Exception {
        mockMvc.perform(get("/api/home").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.platformName").value("SocialShield"))
            .andExpect(jsonPath("$.stats.length()").value(4));
    }

    @Test
    void shouldReturnCoursesData() throws Exception {
        mockMvc.perform(get("/api/courses").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Detección de Phishing"))
            .andExpect(jsonPath("$[0].duration").value("4 semanas"))
            .andExpect(jsonPath("$.length()", greaterThan(0)));
    }
}
