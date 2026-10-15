package com.socialshield.curso_defensa_api.service;

import com.socialshield.curso_defensa_api.model.CourseCrudRequest;
import com.socialshield.curso_defensa_api.model.CourseCrudResponse;
import com.socialshield.curso_defensa_api.model.EnrollmentCrudRequest;
import com.socialshield.curso_defensa_api.model.EnrollmentCrudResponse;
import com.socialshield.curso_defensa_api.model.EvaluationCrudRequest;
import com.socialshield.curso_defensa_api.model.EvaluationCrudResponse;
import com.socialshield.curso_defensa_api.model.InstructorCrudRequest;
import com.socialshield.curso_defensa_api.model.InstructorCrudResponse;
import com.socialshield.curso_defensa_api.model.LessonCrudRequest;
import com.socialshield.curso_defensa_api.model.LessonCrudResponse;
import com.socialshield.curso_defensa_api.model.LoginRequest;
import com.socialshield.curso_defensa_api.model.RegisterRequest;
import com.socialshield.curso_defensa_api.model.UserCrudRequest;
import com.socialshield.curso_defensa_api.model.UserCrudResponse;
import com.socialshield.curso_defensa_api.model.UserProgressResponse;
import com.socialshield.curso_defensa_api.model.UserResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class DatabaseCrudService {

    private static final int PASSWORD_ITERATIONS = 120_000;
    private static final int PASSWORD_KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final RowMapper<UserCrudResponse> USER_MAPPER = (rs, row) -> new UserCrudResponse(
        rs.getInt("id"),
        rs.getString("nombre"),
        rs.getString("correo"),
        rs.getString("telefono"),
        rs.getString("biografia"),
        rs.getBoolean("activo")
    );
    private static final RowMapper<CourseCrudResponse> COURSE_MAPPER = (rs, row) -> new CourseCrudResponse(
        rs.getInt("id"),
        rs.getString("titulo"),
        rs.getInt("duracion"),
        rs.getInt("lecciones"),
        rs.getBoolean("disponible"),
        rs.getString("icono"),
        rs.getString("etiqueta"),
        rs.getString("descripcion"),
        rs.getString("nivel")
    );
    private static final RowMapper<LessonCrudResponse> LESSON_MAPPER = (rs, row) -> new LessonCrudResponse(
        rs.getInt("id"),
        rs.getInt("curso_id"),
        rs.getString("titulo_curso"),
        rs.getString("nivel_curso"),
        rs.getInt("numero"),
        rs.getString("titulo"),
        rs.getString("contenido"),
        rs.getInt("duracion_min"),
        rs.getString("instructor"),
        rs.getString("correo_instructor")
    );
    private static final RowMapper<EnrollmentCrudResponse> ENROLLMENT_MAPPER = (rs, row) ->
        new EnrollmentCrudResponse(
            rs.getInt("id"),
            rs.getInt("usuario_id"),
            rs.getString("nombre_usuario"),
            rs.getString("correo_usuario"),
            rs.getInt("curso_id"),
            rs.getString("titulo_curso"),
            rs.getString("nivel_curso"),
            (Integer) rs.getObject("duracion_curso"),
            rs.getObject("fecha_inscripcion", LocalDate.class),
            rs.getInt("progreso")
        );
    private static final RowMapper<EvaluationCrudResponse> EVALUATION_MAPPER = (rs, row) ->
        new EvaluationCrudResponse(
            rs.getInt("id"),
            rs.getInt("usuario_id"),
            rs.getString("nombre_usuario"),
            rs.getString("correo_usuario"),
            rs.getInt("curso_id"),
            rs.getString("titulo_curso"),
            rs.getBigDecimal("calificacion"),
            rs.getInt("intentos"),
            rs.getBoolean("aprobado"),
            rs.getObject("fecha_evaluacion", LocalDate.class)
        );
    private static final RowMapper<InstructorCrudResponse> INSTRUCTOR_MAPPER = (rs, row) ->
        new InstructorCrudResponse(rs.getInt("id"), rs.getString("nombre"), rs.getString("correo"));

    private static final String COURSE_SELECT = """
        SELECT c.id, c.titulo, c.duracion,
               (SELECT COUNT(*)::INTEGER FROM lecciones l WHERE l.curso_id = c.id) AS lecciones,
               c.disponible, c.icono, c.etiqueta,
               c.descripcion, n.nombre AS nivel
        FROM cursos c JOIN niveles n ON n.id = c.nivel_id
        """;
    private static final String LESSON_SELECT = """
        SELECT l.id, l.curso_id, c.titulo AS titulo_curso, n.nombre AS nivel_curso,
               l.numero, l.titulo, l.contenido, l.duracion_min,
               i.nombre AS instructor, i.correo AS correo_instructor
        FROM lecciones l
        JOIN cursos c ON c.id = l.curso_id
        JOIN niveles n ON n.id = c.nivel_id
        JOIN instructores i ON i.id = l.instructor_id
        """;
    private static final String ENROLLMENT_SELECT = """
        SELECT i.id, i.usuario_id, u.nombre AS nombre_usuario, u.correo AS correo_usuario,
               i.curso_id, c.titulo AS titulo_curso, n.nombre AS nivel_curso,
               c.duracion AS duracion_curso, i.fecha_inscripcion, i.progreso
        FROM inscripciones i
        JOIN usuarios u ON u.id = i.usuario_id
        JOIN cursos c ON c.id = i.curso_id
        JOIN niveles n ON n.id = c.nivel_id
        """;
    private static final String EVALUATION_SELECT = """
        SELECT e.id, e.usuario_id, u.nombre AS nombre_usuario, u.correo AS correo_usuario,
               e.curso_id, c.titulo AS titulo_curso, e.calificacion, e.intentos,
               e.aprobado, e.fecha_evaluacion
        FROM evaluaciones e
        JOIN usuarios u ON u.id = e.usuario_id
        JOIN cursos c ON c.id = e.curso_id
        """;

    private final JdbcTemplate jdbc;

    public DatabaseCrudService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<UserCrudResponse> getUsers() {
        return jdbc.query("SELECT id, nombre, correo, telefono, biografia, activo FROM usuarios ORDER BY id", USER_MAPPER);
    }

    public UserCrudResponse getUser(int id) {
        return findOne(
            "SELECT id, nombre, correo, telefono, biografia, activo FROM usuarios WHERE id = ?",
            USER_MAPPER,
            id
        );
    }

    @Transactional
    public UserCrudResponse createUser(UserCrudRequest request) {
        return write(() -> {
            String passwordHash = hashPassword(request.password());
            int id = jdbc.queryForObject("""
                INSERT INTO usuarios (nombre, correo, contrasena_hash, telefono, biografia, activo)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """, Integer.class, request.name().trim(), normalizeEmail(request.email()), passwordHash,
                request.phone(), request.bio(), request.active() == null || request.active());
            return getUser(id);
        });
    }

    @Transactional
    public UserCrudResponse updateUser(int id, UserCrudRequest request) {
        return write(() -> {
            String passwordHash = request.password() == null || request.password().isBlank()
                ? null
                : hashPassword(request.password());
            int updated = jdbc.update("""
                UPDATE usuarios
                SET nombre = ?, correo = ?, contrasena_hash = COALESCE(?, contrasena_hash),
                    telefono = ?, biografia = ?, activo = COALESCE(?, activo), actualizado_en = CURRENT_TIMESTAMP
                WHERE id = ?
                """, request.name().trim(), normalizeEmail(request.email()), passwordHash,
                request.phone(), request.bio(), request.active(), id);
            requireUpdated(updated, "No existe el usuario indicado.");
            return getUser(id);
        });
    }

    @Transactional
    public void deleteUser(int id) {
        write(() -> {
            if (exists("""
                SELECT EXISTS (
                    SELECT 1 FROM inscripciones WHERE usuario_id = ?
                    UNION ALL SELECT 1 FROM evaluaciones WHERE usuario_id = ?
                    UNION ALL SELECT 1 FROM resenas WHERE usuario_id = ?
                    UNION ALL SELECT 1 FROM intentos_login WHERE usuario_id = ?
                )
                """, id, id, id, id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede borrar el usuario porque tiene actividad relacionada.");
            }
            deleteById("DELETE FROM usuarios WHERE id = ?", id);
            return null;
        });
    }

    public UserResponse register(RegisterRequest request) {
        UserCrudResponse created = createUser(new UserCrudRequest(
            request.name(), request.email(), request.password(), null, null, true
        ));
        return new UserResponse(Integer.toString(created.id()), created.name(), created.email());
    }

    public UserResponse login(LoginRequest request) {
        List<StoredPassword> credentials = jdbc.query(
            "SELECT id, nombre, correo, contrasena_hash FROM usuarios WHERE correo = ? AND activo = TRUE",
            (rs, row) -> new StoredPassword(
                rs.getInt("id"), rs.getString("nombre"), rs.getString("correo"), rs.getString("contrasena_hash")
            ),
            normalizeEmail(request.email())
        );
        if (credentials.isEmpty() || !verifyPassword(request.password(), credentials.getFirst().passwordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
        }
        StoredPassword user = credentials.getFirst();
        return new UserResponse(Integer.toString(user.id()), user.name(), user.email());
    }

    public List<CourseCrudResponse> getCourses() {
        return jdbc.query(COURSE_SELECT + " ORDER BY c.id", COURSE_MAPPER);
    }

    public CourseCrudResponse getCourse(int id) {
        return findOne(COURSE_SELECT + " WHERE c.id = ?", COURSE_MAPPER, id);
    }

    public int getCourseCount() {
        Integer count = jdbc.queryForObject("SELECT count(*) FROM cursos", Integer.class);
        return count == null ? 0 : count;
    }

    @Transactional
    public CourseCrudResponse createCourse(CourseCrudRequest request) {
        return write(() -> {
            int levelId = getLevelId(request.level());
            int id = jdbc.queryForObject("""
                INSERT INTO cursos (titulo, duracion, disponible, icono, etiqueta, descripcion, nivel_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """, Integer.class, request.title().trim(), request.duration(),
                request.available() == null || request.available(), request.icon(), request.tag(),
                request.description(), levelId);
            return getCourse(id);
        });
    }

    @Transactional
    public CourseCrudResponse updateCourse(int id, CourseCrudRequest request) {
        return write(() -> {
            int levelId = getLevelId(request.level());
            int updated = jdbc.update("""
                UPDATE cursos
                SET titulo = ?, duracion = ?, disponible = ?, icono = ?, etiqueta = ?,
                    descripcion = ?, nivel_id = ?, actualizado_en = CURRENT_TIMESTAMP
                WHERE id = ?
                """, request.title().trim(), request.duration(), request.available(), request.icon(),
                request.tag(), request.description(), levelId, id);
            requireUpdated(updated, "No existe el curso indicado.");
            return getCourse(id);
        });
    }

    @Transactional
    public void deleteCourse(int id) {
        write(() -> {
            if (exists("""
                SELECT EXISTS (
                    SELECT 1 FROM lecciones WHERE curso_id = ?
                    UNION ALL SELECT 1 FROM inscripciones WHERE curso_id = ?
                    UNION ALL SELECT 1 FROM evaluaciones WHERE curso_id = ?
                    UNION ALL SELECT 1 FROM resenas WHERE curso_id = ?
                )
                """, id, id, id, id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede borrar el curso porque tiene lecciones o actividad relacionada.");
            }
            deleteById("DELETE FROM cursos WHERE id = ?", id);
            return null;
        });
    }

    public List<LessonCrudResponse> getLessons() {
        return jdbc.query(LESSON_SELECT + " ORDER BY l.curso_id, l.numero, l.id", LESSON_MAPPER);
    }

    public LessonCrudResponse getLesson(int id) {
        return findOne(LESSON_SELECT + " WHERE l.id = ?", LESSON_MAPPER, id);
    }

    @Transactional
    public LessonCrudResponse createLesson(LessonCrudRequest request) {
        return write(() -> {
            int id = jdbc.queryForObject("""
                INSERT INTO lecciones (curso_id, instructor_id, numero, titulo, contenido, duracion_min)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """, Integer.class, request.courseId(), request.instructorId(), request.number(), request.title().trim(),
                request.content(), request.durationMinutes());
            return getLesson(id);
        });
    }

    @Transactional
    public LessonCrudResponse updateLesson(int id, LessonCrudRequest request) {
        return write(() -> {
            int updated = jdbc.update("""
                UPDATE lecciones
                SET curso_id = ?, instructor_id = ?, numero = ?, titulo = ?, contenido = ?,
                    duracion_min = ?, actualizado_en = CURRENT_TIMESTAMP
                WHERE id = ?
                """, request.courseId(), request.instructorId(), request.number(), request.title().trim(),
                request.content(), request.durationMinutes(), id);
            requireUpdated(updated, "No existe la lección indicada.");
            return getLesson(id);
        });
    }

    @Transactional
    public void deleteLesson(int id) {
        deleteById("DELETE FROM lecciones WHERE id = ?", id);
    }

    public List<InstructorCrudResponse> getInstructors() {
        return jdbc.query("SELECT id, nombre, correo FROM instructores ORDER BY id", INSTRUCTOR_MAPPER);
    }

    public InstructorCrudResponse getInstructor(int id) {
        return findOne("SELECT id, nombre, correo FROM instructores WHERE id = ?", INSTRUCTOR_MAPPER, id);
    }

    @Transactional
    public InstructorCrudResponse createInstructor(InstructorCrudRequest request) {
        return write(() -> {
            int id = jdbc.queryForObject("""
                INSERT INTO instructores (nombre, correo) VALUES (?, ?) RETURNING id
                """, Integer.class, request.name().trim(), request.email());
            return getInstructor(id);
        });
    }

    @Transactional
    public InstructorCrudResponse updateInstructor(int id, InstructorCrudRequest request) {
        return write(() -> {
            int updated = jdbc.update("""
                UPDATE instructores SET nombre = ?, correo = ?, actualizado_en = CURRENT_TIMESTAMP
                WHERE id = ?
                """, request.name().trim(), request.email(), id);
            requireUpdated(updated, "No existe el instructor indicado.");
            return getInstructor(id);
        });
    }

    @Transactional
    public void deleteInstructor(int id) {
        write(() -> {
            if (exists("SELECT EXISTS (SELECT 1 FROM lecciones WHERE instructor_id = ?)", id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede borrar el instructor porque tiene lecciones relacionadas.");
            }
            deleteById("DELETE FROM instructores WHERE id = ?", id);
            return null;
        });
    }

    public List<EnrollmentCrudResponse> getEnrollments() {
        return jdbc.query(ENROLLMENT_SELECT + " ORDER BY i.id", ENROLLMENT_MAPPER);
    }

    public EnrollmentCrudResponse getEnrollment(int id) {
        return findOne(ENROLLMENT_SELECT + " WHERE i.id = ?", ENROLLMENT_MAPPER, id);
    }

    @Transactional
    public EnrollmentCrudResponse createEnrollment(EnrollmentCrudRequest request) {
        return write(() -> {
            int id = jdbc.queryForObject("""
                INSERT INTO inscripciones (usuario_id, curso_id, fecha_inscripcion, progreso)
                VALUES (?, ?, COALESCE(?, CURRENT_DATE), COALESCE(?, 0))
                RETURNING id
                """, Integer.class, request.userId(), request.courseId(), request.enrolledAt(), request.progress());
            return getEnrollment(id);
        });
    }

    @Transactional
    public EnrollmentCrudResponse updateEnrollment(int id, EnrollmentCrudRequest request) {
        return write(() -> {
            int updated = jdbc.update("""
                UPDATE inscripciones
                SET usuario_id = ?, curso_id = ?, fecha_inscripcion = COALESCE(?, fecha_inscripcion),
                    progreso = ?,
                    actualizado_en = CURRENT_TIMESTAMP
                WHERE id = ?
                """, request.userId(), request.courseId(), request.enrolledAt(), request.progress(), id);
            requireUpdated(updated, "No existe la inscripción indicada.");
            return getEnrollment(id);
        });
    }

    @Transactional
    public void deleteEnrollment(int id) {
        deleteById("DELETE FROM inscripciones WHERE id = ?", id);
    }

    public List<EnrollmentCrudResponse> getEnrollmentsForUser(int userId) {
        getUser(userId);
        return jdbc.query(ENROLLMENT_SELECT + " WHERE i.usuario_id = ? ORDER BY i.fecha_inscripcion, i.id",
            ENROLLMENT_MAPPER, userId);
    }

    public UserProgressResponse getProgress(int userId) {
        List<EnrollmentCrudResponse> userEnrollments = getEnrollmentsForUser(userId);
        int completed = 0;
        int total = 0;
        List<com.socialshield.curso_defensa_api.model.EnrollmentResponse> progressRows = new ArrayList<>();
        for (EnrollmentCrudResponse enrollment : userEnrollments) {
            if (enrollment.progress() == 100) {
                completed++;
            }
            total += enrollment.progress();
            progressRows.add(new com.socialshield.curso_defensa_api.model.EnrollmentResponse(
                Integer.toString(enrollment.id()),
                Integer.toString(enrollment.userId()),
                Integer.toString(enrollment.courseId()),
                enrollment.courseTitle(),
                enrollment.enrolledAt(),
                enrollment.progress()
            ));
        }
        double average = userEnrollments.isEmpty() ? 0 : (double) total / userEnrollments.size();
        return new UserProgressResponse(
            Integer.toString(userId), userEnrollments.size(), completed, average, progressRows
        );
    }

    public List<EvaluationCrudResponse> getEvaluations() {
        return jdbc.query(EVALUATION_SELECT + " ORDER BY e.id", EVALUATION_MAPPER);
    }

    public EvaluationCrudResponse getEvaluation(int id) {
        return findOne(EVALUATION_SELECT + " WHERE e.id = ?", EVALUATION_MAPPER, id);
    }

    @Transactional
    public EvaluationCrudResponse createEvaluation(EvaluationCrudRequest request) {
        return write(() -> {
            int id = jdbc.queryForObject("""
                INSERT INTO evaluaciones (usuario_id, curso_id, calificacion, intentos, aprobado, fecha_evaluacion)
                VALUES (?, ?, ?, ?, ?, COALESCE(?, CURRENT_DATE))
                RETURNING id
                """, Integer.class, request.userId(), request.courseId(), request.score(), request.attempts(),
                request.passed(), request.evaluatedAt());
            return getEvaluation(id);
        });
    }

    @Transactional
    public EvaluationCrudResponse updateEvaluation(int id, EvaluationCrudRequest request) {
        return write(() -> {
            int updated = jdbc.update("""
                UPDATE evaluaciones
                SET usuario_id = ?, curso_id = ?, calificacion = ?, intentos = ?, aprobado = ?,
                    fecha_evaluacion = COALESCE(?, fecha_evaluacion), actualizado_en = CURRENT_TIMESTAMP
                WHERE id = ?
                """, request.userId(), request.courseId(), request.score(), request.attempts(),
                request.passed(), request.evaluatedAt(), id);
            requireUpdated(updated, "No existe la evaluación indicada.");
            return getEvaluation(id);
        });
    }

    @Transactional
    public void deleteEvaluation(int id) {
        write(() -> {
            if (exists("SELECT EXISTS (SELECT 1 FROM certificados WHERE evaluacion_id = ?)", id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede borrar la evaluación porque tiene un certificado asociado.");
            }
            deleteById("DELETE FROM evaluaciones WHERE id = ?", id);
            return null;
        });
    }

    private int getLevelId(String name) {
        List<Integer> ids = jdbc.query("SELECT id FROM niveles WHERE nombre = ?", (rs, row) -> rs.getInt(1), name);
        if (ids.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nivel del curso no existe.");
        }
        return ids.getFirst();
    }

    private <T> T findOne(String sql, RowMapper<T> mapper, Object... args) {
        List<T> rows = jdbc.query(sql, mapper, args);
        if (rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el registro solicitado.");
        }
        return rows.getFirst();
    }

    private boolean exists(String sql, Object... args) {
        Boolean result = jdbc.queryForObject(sql, Boolean.class, args);
        return Boolean.TRUE.equals(result);
    }

    private void deleteById(String sql, int id) {
        int deleted = write(() -> jdbc.update(sql, id));
        requireUpdated(deleted, "No existe el registro indicado.");
    }

    private void requireUpdated(int rows, String message) {
        if (rows == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
    }

    private <T> T write(WriteOperation<T> operation) {
        try {
            return operation.execute();
        } catch (DuplicateKeyException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El registro duplica un dato único.", exception);
        } catch (DataIntegrityViolationException exception) {
            Throwable cause = exception.getMostSpecificCause();
            if (cause instanceof SQLException sqlException && "23503".equals(sqlException.getSQLState())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Una relación indicada no existe.", exception);
            }
            if (cause instanceof SQLException sqlException && "23514".equals(sqlException.getSQLState())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los datos no cumplen las reglas de la base de datos.", exception);
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El registro tiene relaciones que impiden la operación.", exception);
        }
    }

    private String hashPassword(String password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        char[] characters = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(characters, salt, PASSWORD_ITERATIONS, PASSWORD_KEY_LENGTH);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .getEncoded();
            return "pbkdf2$" + PASSWORD_ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(hash);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("No se pudo proteger la contraseña.", exception);
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(characters, '\0');
        }
    }

    private boolean verifyPassword(String password, String encoded) {
        String[] parts = encoded.split("\\$", -1);
        if (parts.length != 4 || !"pbkdf2".equals(parts[0])) {
            return false;
        }
        try {
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 1 || iterations > 1_000_000) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
            char[] characters = password.toCharArray();
            PBEKeySpec spec = new PBEKeySpec(characters, salt, iterations, expectedHash.length * 8);
            try {
                byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
                return MessageDigest.isEqual(expectedHash, actual);
            } finally {
                spec.clearPassword();
                java.util.Arrays.fill(characters, '\0');
            }
        } catch (IllegalArgumentException | GeneralSecurityException exception) {
            return false;
        }
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(java.util.Locale.ROOT);
    }

    @FunctionalInterface
    private interface WriteOperation<T> {
        T execute() throws DataAccessException;
    }

    private record StoredPassword(int id, String name, String email, String passwordHash) {
    }
}
