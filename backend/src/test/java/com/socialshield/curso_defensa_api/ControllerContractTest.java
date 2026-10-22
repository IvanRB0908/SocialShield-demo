package com.socialshield.curso_defensa_api;

import com.socialshield.curso_defensa_api.controller.*;
import com.socialshield.curso_defensa_api.model.*;
import com.socialshield.curso_defensa_api.service.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Contratos HTTP sin conexión a PostgreSQL ni llamadas externas a Google.
class ControllerContractTest {
    private DatabaseCrudService db;
    private CourseService courses;
    private GoogleLoginService google;
    private SocialPlatformService reports;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        db = mock(DatabaseCrudService.class);
        courses = mock(CourseService.class);
        google = mock(GoogleLoginService.class);
        reports = mock(SocialPlatformService.class);
        mvc = standaloneSetup(
            new CourseController(courses, db), new UserAuthController(db, google),
            new LessonController(db), new EnrollmentController(db),
            new SecurityReportController(reports)
        ).build();
    }

    @Test
    void usersCrudPreservesJsonAndStatuses() throws Exception {
        var row = new UserCrudResponse(1, "Ana", "ana@example.test", null, null, true);
        when(db.getUsers()).thenReturn(List.of(row));
        when(db.getUser(1)).thenReturn(row);
        when(db.updateUser(eq(1), any())).thenReturn(row);
        when(db.register(any())).thenReturn(new UserResponse("1", "Ana", "ana@example.test"));
        String request = "{\"name\":\"Ana\",\"email\":\"ana@example.test\",\"password\":\"password-123\"}";
        String expected = "{\"id\":1,\"name\":\"Ana\",\"email\":\"ana@example.test\",\"phone\":null,\"bio\":null,\"active\":true}";
        mvc.perform(get("/api/users")).andExpect(status().isOk())
            .andExpect(content().json("[" + expected + "]"));
        mvc.perform(get("/api/users/1")).andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isCreated()).andExpect(content().json("{\"id\":\"1\",\"name\":\"Ana\",\"email\":\"ana@example.test\"}"));
        mvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(delete("/api/users/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(db).deleteUser(1);
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        when(db.getUser(999)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe"));
        mvc.perform(get("/api/users/999")).andExpect(status().isNotFound());
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Relacionado")).when(db).deleteUser(999);
        mvc.perform(delete("/api/users/999")).andExpect(status().isConflict());
    }

    @Test
    void coursesCrudPreservesJsonAndStatuses() throws Exception {
        var row = new CourseCrudResponse(1, "Curso", 12, 0, true, null, null, null, "Básico");
        when(db.getCourses()).thenReturn(List.of(row));
        when(db.getCourse(1)).thenReturn(row);
        when(db.updateCourse(eq(1), any())).thenReturn(row);
        when(db.createCourse(any())).thenReturn(row);
        String request = "{\"title\":\"Curso\",\"duration\":12,\"available\":true,\"level\":\"Básico\"}";
        String expected = "{\"id\":1,\"title\":\"Curso\",\"duration\":12,\"lessons\":0,\"available\":true,\"icon\":null,\"tag\":null,\"description\":null,\"level\":\"Básico\"}";
        mvc.perform(get("/api/courses")).andExpect(status().isOk())
            .andExpect(content().json("[" + expected + "]"));
        mvc.perform(get("/api/courses/1")).andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(post("/api/courses").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isCreated()).andExpect(content().json("{\"id\":1,\"title\":\"Curso\",\"duration\":12,\"lessons\":0,\"available\":true,\"icon\":null,\"tag\":null,\"description\":null,\"level\":\"Básico\"}"));
        mvc.perform(put("/api/courses/1").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(delete("/api/courses/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(db).deleteCourse(1);
        mvc.perform(post("/api/courses").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        when(db.getCourse(999)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe"));
        mvc.perform(get("/api/courses/999")).andExpect(status().isNotFound());
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Relacionado")).when(db).deleteCourse(999);
        mvc.perform(delete("/api/courses/999")).andExpect(status().isConflict());
    }

    @Test
    void lessonsCrudPreservesJsonAndStatuses() throws Exception {
        var row = new LessonCrudResponse(1, 1, "Curso", "Básico", 1, "Lección", null, 10, "Ana", "ana@example.test");
        when(db.getLessons()).thenReturn(List.of(row));
        when(db.getLesson(1)).thenReturn(row);
        when(db.updateLesson(eq(1), any())).thenReturn(row);
        when(db.createLesson(any())).thenReturn(row);
        String request = "{\"courseId\":1,\"instructorId\":1,\"number\":1,\"title\":\"Lección\",\"durationMinutes\":10}";
        String expected = "{\"id\":1,\"courseId\":1,\"courseTitle\":\"Curso\",\"courseLevel\":\"Básico\",\"number\":1,\"title\":\"Lección\",\"content\":null,\"durationMinutes\":10,\"instructor\":\"Ana\",\"instructorEmail\":\"ana@example.test\"}";
        mvc.perform(get("/api/lessons")).andExpect(status().isOk())
            .andExpect(content().json("[" + expected + "]"));
        mvc.perform(get("/api/lessons/1")).andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(post("/api/lessons").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isCreated()).andExpect(content().json("{\"id\":1,\"courseId\":1,\"courseTitle\":\"Curso\",\"courseLevel\":\"Básico\",\"number\":1,\"title\":\"Lección\",\"content\":null,\"durationMinutes\":10,\"instructor\":\"Ana\",\"instructorEmail\":\"ana@example.test\"}"));
        mvc.perform(put("/api/lessons/1").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(delete("/api/lessons/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(db).deleteLesson(1);
        mvc.perform(post("/api/lessons").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        when(db.getLesson(999)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe"));
        mvc.perform(get("/api/lessons/999")).andExpect(status().isNotFound());
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Relacionado")).when(db).deleteLesson(999);
        mvc.perform(delete("/api/lessons/999")).andExpect(status().isConflict());
    }

    @Test
    void instructorsCrudPreservesJsonAndStatuses() throws Exception {
        var row = new InstructorCrudResponse(1, "Ana", "ana@example.test");
        when(db.getInstructors()).thenReturn(List.of(row));
        when(db.getInstructor(1)).thenReturn(row);
        when(db.updateInstructor(eq(1), any())).thenReturn(row);
        when(db.createInstructor(any())).thenReturn(row);
        String request = "{\"name\":\"Ana\",\"email\":\"ana@example.test\"}";
        String expected = "{\"id\":1,\"name\":\"Ana\",\"email\":\"ana@example.test\"}";
        mvc.perform(get("/api/instructors")).andExpect(status().isOk())
            .andExpect(content().json("[" + expected + "]"));
        mvc.perform(get("/api/instructors/1")).andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(post("/api/instructors").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isCreated()).andExpect(content().json("{\"id\":1,\"name\":\"Ana\",\"email\":\"ana@example.test\"}"));
        mvc.perform(put("/api/instructors/1").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(delete("/api/instructors/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(db).deleteInstructor(1);
        mvc.perform(post("/api/instructors").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        when(db.getInstructor(999)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe"));
        mvc.perform(get("/api/instructors/999")).andExpect(status().isNotFound());
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Relacionado")).when(db).deleteInstructor(999);
        mvc.perform(delete("/api/instructors/999")).andExpect(status().isConflict());
    }

    @Test
    void enrollmentsCrudPreservesJsonAndStatuses() throws Exception {
        var row = new EnrollmentCrudResponse(1, 1, "Ana", "ana@example.test", 1, "Curso", "Básico", 12, java.time.LocalDate.of(2026, 1, 1), 30);
        when(db.getEnrollments()).thenReturn(List.of(row));
        when(db.getEnrollment(1)).thenReturn(row);
        when(db.updateEnrollment(eq(1), any())).thenReturn(row);
        when(db.createEnrollment(any())).thenReturn(row);
        String request = "{\"userId\":1,\"courseId\":1,\"progress\":30}";
        String expected = "{\"id\":1,\"userId\":1,\"userName\":\"Ana\",\"userEmail\":\"ana@example.test\",\"courseId\":1,\"courseTitle\":\"Curso\",\"courseLevel\":\"Básico\",\"courseDuration\":12,\"enrolledAt\":\"2026-01-01\",\"progress\":30}";
        mvc.perform(get("/api/enrollments")).andExpect(status().isOk())
            .andExpect(content().json("[" + expected + "]"));
        mvc.perform(get("/api/enrollments/1")).andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(post("/api/enrollments").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isCreated()).andExpect(content().json("{\"id\":1,\"userId\":1,\"userName\":\"Ana\",\"userEmail\":\"ana@example.test\",\"courseId\":1,\"courseTitle\":\"Curso\",\"courseLevel\":\"Básico\",\"courseDuration\":12,\"enrolledAt\":\"2026-01-01\",\"progress\":30}"));
        mvc.perform(put("/api/enrollments/1").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(delete("/api/enrollments/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(db).deleteEnrollment(1);
        mvc.perform(post("/api/enrollments").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        when(db.getEnrollment(999)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe"));
        mvc.perform(get("/api/enrollments/999")).andExpect(status().isNotFound());
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Relacionado")).when(db).deleteEnrollment(999);
        mvc.perform(delete("/api/enrollments/999")).andExpect(status().isConflict());
    }

    @Test
    void evaluationsCrudPreservesJsonAndStatuses() throws Exception {
        var row = new EvaluationCrudResponse(1, 1, "Ana", "ana@example.test", 1, "Curso", new java.math.BigDecimal("90"), 1, true, java.time.LocalDate.of(2026, 1, 1));
        when(db.getEvaluations()).thenReturn(List.of(row));
        when(db.getEvaluation(1)).thenReturn(row);
        when(db.updateEvaluation(eq(1), any())).thenReturn(row);
        when(db.createEvaluation(any())).thenReturn(row);
        String request = "{\"userId\":1,\"courseId\":1,\"score\":90,\"attempts\":1,\"passed\":true}";
        String expected = "{\"id\":1,\"userId\":1,\"userName\":\"Ana\",\"userEmail\":\"ana@example.test\",\"courseId\":1,\"courseTitle\":\"Curso\",\"score\":90,\"attempts\":1,\"passed\":true,\"evaluatedAt\":\"2026-01-01\"}";
        mvc.perform(get("/api/evaluations")).andExpect(status().isOk())
            .andExpect(content().json("[" + expected + "]"));
        mvc.perform(get("/api/evaluations/1")).andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(post("/api/evaluations").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isCreated()).andExpect(content().json("{\"id\":1,\"userId\":1,\"userName\":\"Ana\",\"userEmail\":\"ana@example.test\",\"courseId\":1,\"courseTitle\":\"Curso\",\"score\":90,\"attempts\":1,\"passed\":true,\"evaluatedAt\":\"2026-01-01\"}"));
        mvc.perform(put("/api/evaluations/1").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isOk()).andExpect(content().json(expected));
        mvc.perform(delete("/api/evaluations/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(db).deleteEvaluation(1);
        mvc.perform(post("/api/evaluations").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        when(db.getEvaluation(999)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe"));
        mvc.perform(get("/api/evaluations/999")).andExpect(status().isNotFound());
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Relacionado")).when(db).deleteEvaluation(999);
        mvc.perform(delete("/api/evaluations/999")).andExpect(status().isConflict());
    }

    @Test
    void homeHealthAuthenticationProgressAndReportsKeepTheirContracts() throws Exception {
        when(courses.getHomePage()).thenReturn(new HomePageResponse("SocialShield", List.of(), List.of()));
        when(google.getClientId()).thenReturn("configured-client-id");
        var user = new UserResponse("1", "Ana", "ana@example.test");
        when(db.login(any())).thenReturn(user);
        when(google.login("verified-token")).thenReturn(user);
        when(db.getProgress(1)).thenReturn(new UserProgressResponse("1", 0, 0, 0, List.of()));
        var report = new SecurityReportResponse("report-1", "phishing", "Mensaje de prueba", null,
            java.time.Instant.parse("2026-01-01T00:00:00Z"));
        when(reports.createReport(any())).thenReturn(report);
        when(reports.getReports()).thenReturn(List.of(report));
        mvc.perform(get("/api/health")).andExpect(status().isOk()).andExpect(content().string("OK"));
        mvc.perform(get("/api/home")).andExpect(status().isOk())
            .andExpect(content().json("{\"platformName\":\"SocialShield\",\"stats\":[],\"sections\":[]}"));
        mvc.perform(get("/api/auth/google/config")).andExpect(status().isOk())
            .andExpect(jsonPath("$.clientId").value("configured-client-id"));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"ana@example.test\",\"password\":\"password-123\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value("1"));
        mvc.perform(post("/api/auth/google").contentType(MediaType.APPLICATION_JSON)
                .content("{\"idToken\":\"verified-token\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("ana@example.test"));
        verify(google).login("verified-token");
        mvc.perform(post("/api/auth/google").contentType(MediaType.APPLICATION_JSON).content("{\"idToken\":\" \"}"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/users/1/progress")).andExpect(status().isOk())
            .andExpect(jsonPath("$.enrollments").isEmpty()).andExpect(jsonPath("$.averageProgress").value(0));
        mvc.perform(post("/api/security-reports").contentType(MediaType.APPLICATION_JSON)
                .content("{\"category\":\"phishing\",\"description\":\"Mensaje de prueba\"}"))
            .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/security-reports/report-1"))
            .andExpect(jsonPath("$.createdAt").value("2026-01-01T00:00:00Z"));
        mvc.perform(get("/api/security-reports")).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("report-1"));
        mvc.perform(get("/api/lessons").header("Origin", "http://localhost:4200"))
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void exposesExactlyTheOriginal38MethodAndPathPairs() {
        var mapping = mvc.getDispatcherServlet().getWebApplicationContext()
            .getBean(RequestMappingHandlerMapping.class);
        Set<String> actual = new TreeSet<>();
        mapping.getHandlerMethods().keySet().forEach(info -> {
            for (var method : info.getMethodsCondition().getMethods()) {
                for (String path : info.getPatternValues()) {
                    actual.add(method.name() + " " + path);
                }
            }
        });
        assertEquals(Set.of("DELETE /api/courses/{id}",
            "DELETE /api/enrollments/{id}",
            "DELETE /api/evaluations/{id}",
            "DELETE /api/instructors/{id}",
            "DELETE /api/lessons/{id}",
            "DELETE /api/users/{id}",
            "GET /api/auth/google/config",
            "GET /api/courses",
            "GET /api/courses/{id}",
            "GET /api/enrollments",
            "GET /api/enrollments/{id}",
            "GET /api/evaluations",
            "GET /api/evaluations/{id}",
            "GET /api/health",
            "GET /api/home",
            "GET /api/instructors",
            "GET /api/instructors/{id}",
            "GET /api/lessons",
            "GET /api/lessons/{id}",
            "GET /api/security-reports",
            "GET /api/users",
            "GET /api/users/{id}",
            "GET /api/users/{userId}/progress",
            "POST /api/auth/google",
            "POST /api/auth/login",
            "POST /api/courses",
            "POST /api/enrollments",
            "POST /api/evaluations",
            "POST /api/instructors",
            "POST /api/lessons",
            "POST /api/security-reports",
            "POST /api/users",
            "PUT /api/courses/{id}",
            "PUT /api/enrollments/{id}",
            "PUT /api/evaluations/{id}",
            "PUT /api/instructors/{id}",
            "PUT /api/lessons/{id}",
            "PUT /api/users/{id}"), actual);
    }
}
