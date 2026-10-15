package com.socialshield.curso_defensa_api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class CourseControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldCreateReadUpdateAndDeleteDatabaseCourse() throws Exception {
        String course = """
            {
              "title": "Curso CRUD de prueba",
              "duration": 40,
              "available": true,
              "icon": "shield",
              "tag": "Prueba",
              "description": "Registro temporal para probar el CRUD.",
              "level": "Básico"
            }
            """;
        String response = mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(course))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Curso CRUD de prueba"))
            .andExpect(jsonPath("$.duration").value(40))
            .andExpect(jsonPath("$.lessons").value(0))
            .andReturn()
            .getResponse()
            .getContentAsString();
        int id = readId(response);

        mockMvc.perform(get("/api/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id == " + id + ")].title", hasItem("Curso CRUD de prueba")));
        mockMvc.perform(get("/api/courses/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.level").value("Básico"));

        mockMvc.perform(put("/api/courses/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(course.replace("Curso CRUD de prueba", "Curso actualizado")
                    .replace("\"duration\": 40", "\"duration\": 45")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Curso actualizado"))
            .andExpect(jsonPath("$.duration").value(45));

        mockMvc.perform(delete("/api/courses/{id}", id))
            .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/courses/{id}", id))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidCourseAndMissingCourse() throws Exception {
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"", "duration":0, "level":"No existe"}
                    """))
            .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/courses/2147483000"))
            .andExpect(status().isNotFound());
    }

    private int readId(String json) throws Exception {
        java.util.regex.Matcher matcher = java.util.regex.Pattern
            .compile("\"id\":(?:\"(\\d+)\"|(\\d+))")
            .matcher(json);
        if (!matcher.find()) {
            throw new AssertionError("La respuesta no contiene un ID numérico.");
        }
        return Integer.parseInt(matcher.group(1) == null ? matcher.group(2) : matcher.group(1));
    }
}
