package com.socialshield.curso_defensa_api;

import com.socialshield.curso_defensa_api.service.DatabaseCrudService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class DatabaseCrudControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private DatabaseCrudService databaseCrudService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldRejectEmptyGoogleIdToken() throws Exception {
        mockMvc.perform(post("/api/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idToken\":\" \"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldExposeGoogleClientConfiguration() throws Exception {
        mockMvc.perform(get("/api/auth/google/config"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientId").exists());
    }

    @Test
    void shouldCreateAndReuseGoogleUserByVerifiedEmail() {
        String email = "google-%s@example.test".formatted(java.util.UUID.randomUUID());

        var firstLogin = databaseCrudService.loginWithGoogle("Google User", email);
        var secondLogin = databaseCrudService.loginWithGoogle("Another Name", email);

        org.junit.jupiter.api.Assertions.assertEquals(firstLogin.id(), secondLogin.id());
        org.junit.jupiter.api.Assertions.assertEquals("Google User", secondLogin.name());
    }

    @Test
    void shouldCreateReadUpdateAndDeleteUserLessonEnrollmentAndEvaluation() throws Exception {
        int userId = postAndReadId("/api/users", """
            {
              "name": "Usuario CRUD temporal",
              "email": "crud-%s@example.test",
              "password": "temporary-password-1"
            }
            """.formatted(java.util.UUID.randomUUID()));
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id == " + userId + ")].password").doesNotExist());
        mockMvc.perform(get("/api/users/{id}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(org.hamcrest.Matchers.containsString("crud-")));

        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Usuario CRUD actualizado",
                      "email": "updated-%s@example.test",
                      "phone": "5551234567",
                      "bio": "Perfil de prueba",
                      "active": true
                    }
                    """.formatted(java.util.UUID.randomUUID())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Usuario CRUD actualizado"));

        int courseId = postAndReadId("/api/courses", """
            {
              "title": "Curso para CRUD integral",
              "duration": 12,
              "available": true,
              "icon": "shield",
              "tag": "Test",
              "description": "Curso temporal para validar referencias.",
              "level": "Básico"
            }
            """);
        int instructorId = postAndReadId("/api/instructors", """
            {"name":"Instructor CRUD temporal","email":"teacher-%s@example.test"}
            """.formatted(java.util.UUID.randomUUID()));

        int lessonId = postAndReadId("/api/lessons", """
            {
              "courseId": %d,
              "instructorId": %d,
              "number": 1,
              "title": "Lección temporal",
              "content": "Contenido de prueba",
              "durationMinutes": 25
            }
            """.formatted(courseId, instructorId));
        mockMvc.perform(get("/api/lessons/{id}", lessonId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.courseTitle").value("Curso para CRUD integral"))
            .andExpect(jsonPath("$.instructor").value("Instructor CRUD temporal"));
        mockMvc.perform(get("/api/courses/{id}", courseId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lessons").value(1));
        mockMvc.perform(put("/api/lessons/{id}", lessonId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "courseId": %d,
                      "instructorId": %d,
                      "number": 2,
                      "title": "Lección actualizada",
                      "content": "Contenido actualizado",
                      "durationMinutes": 30
                    }
                    """.formatted(courseId, instructorId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.number").value(2));

        int enrollmentId = postAndReadId("/api/enrollments", """
            {"userId":%d,"courseId":%d,"progress":35}
            """.formatted(userId, courseId));
        mockMvc.perform(get("/api/enrollments/{id}", enrollmentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userName").value("Usuario CRUD actualizado"));
        mockMvc.perform(put("/api/enrollments/{id}", enrollmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"userId":%d,"courseId":%d,"progress":100}
                    """.formatted(userId, courseId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.progress").value(100));
        mockMvc.perform(get("/api/users/{id}/progress", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.enrolledCourses").value(1))
            .andExpect(jsonPath("$.completedCourses").value(1))
            .andExpect(jsonPath("$.averageProgress").value(100.0));

        int evaluationId = postAndReadId("/api/evaluations", """
            {
              "userId":%d,
              "courseId":%d,
              "score":92.50,
              "attempts":1,
              "passed":true
            }
            """.formatted(userId, courseId));
        mockMvc.perform(get("/api/evaluations/{id}", evaluationId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score").value(92.5));
        mockMvc.perform(put("/api/evaluations/{id}", evaluationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "userId":%d,
                      "courseId":%d,
                      "score":95,
                      "attempts":2,
                      "passed":true
                    }
                    """.formatted(userId, courseId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.attempts").value(2));

        mockMvc.perform(delete("/api/evaluations/{id}", evaluationId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/enrollments/{id}", enrollmentId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/lessons/{id}", lessonId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/instructors/{id}", instructorId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/courses/{id}", courseId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/users/{id}", userId)).andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectInvalidReferencesAndConflictingDeletes() throws Exception {
        int userId = postAndReadId("/api/users", """
            {
              "name":"Usuario para conflicto",
              "email":"conflict-%s@example.test",
              "password":"temporary-password-1"
            }
            """.formatted(java.util.UUID.randomUUID()));
        int courseId = postAndReadId("/api/courses", """
            {
              "title":"Curso para conflicto",
              "duration":8,
              "available":true,
              "level":"Básico"
            }
            """);
        int instructorId = postAndReadId("/api/instructors", """
            {"name":"Instructor ocupado","email":"busy-%s@example.test"}
            """.formatted(java.util.UUID.randomUUID()));
        postAndReadId("/api/lessons", """
            {
              "courseId":%d,
              "instructorId":%d,
              "number":1,
              "title":"Lección del instructor ocupado",
              "durationMinutes":10
            }
            """.formatted(courseId, instructorId));
        postAndReadId("/api/enrollments", """
            {"userId":%d,"courseId":%d,"progress":0}
            """.formatted(userId, courseId));

        mockMvc.perform(delete("/api/users/{id}", userId)).andExpect(status().isConflict());
        mockMvc.perform(delete("/api/courses/{id}", courseId)).andExpect(status().isConflict());
        mockMvc.perform(delete("/api/instructors/{id}", instructorId)).andExpect(status().isConflict());
        mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"userId":99999999,"courseId":99999999,"progress":0}
                    """))
            .andExpect(status().isBadRequest());
    }

    private int postAndReadId(String path, String json) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andReturn();
        String response = result.getResponse().getContentAsString();
        java.util.regex.Matcher matcher = java.util.regex.Pattern
            .compile("\"id\":(?:\"(\\d+)\"|(\\d+))")
            .matcher(response);
        if (!matcher.find()) {
            throw new AssertionError("La respuesta no contiene un ID numérico.");
        }
        return Integer.parseInt(matcher.group(1) == null ? matcher.group(2) : matcher.group(1));
    }
}
