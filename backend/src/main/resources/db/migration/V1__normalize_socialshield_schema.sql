CREATE TABLE niveles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
);

INSERT INTO niveles (nombre)
SELECT DISTINCT nivel FROM cursos
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO niveles (nombre)
VALUES ('Básico'), ('Intermedio'), ('Avanzado')
ON CONFLICT (nombre) DO NOTHING;

ALTER TABLE cursos ADD COLUMN nivel_id INTEGER;
UPDATE cursos c
SET nivel_id = n.id
FROM niveles n
WHERE n.nombre = c.nivel;
ALTER TABLE cursos ALTER COLUMN nivel_id SET NOT NULL;
ALTER TABLE cursos
    ADD CONSTRAINT fk_cursos_nivel FOREIGN KEY (nivel_id) REFERENCES niveles(id);
ALTER TABLE cursos DROP COLUMN nivel;

CREATE TABLE instructores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(120),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_instructores_nombre_correo
    ON instructores (nombre, COALESCE(correo, ''));

INSERT INTO instructores (nombre, correo)
SELECT DISTINCT ON (instructor, COALESCE(correo_instructor, ''))
    instructor, correo_instructor
FROM lecciones
ORDER BY instructor, COALESCE(correo_instructor, ''), id;

ALTER TABLE lecciones ADD COLUMN instructor_id INTEGER;
UPDATE lecciones l
SET instructor_id = i.id
FROM instructores i
WHERE i.nombre = l.instructor
  AND i.correo IS NOT DISTINCT FROM l.correo_instructor;
ALTER TABLE lecciones ALTER COLUMN instructor_id SET NOT NULL;
ALTER TABLE lecciones
    ADD CONSTRAINT fk_lecciones_instructor FOREIGN KEY (instructor_id) REFERENCES instructores(id);
ALTER TABLE lecciones
    DROP COLUMN titulo_curso,
    DROP COLUMN nivel_curso,
    DROP COLUMN instructor,
    DROP COLUMN correo_instructor;

ALTER TABLE usuarios DROP COLUMN cursos_inscritos;

ALTER TABLE inscripciones
    DROP COLUMN nombre_usuario,
    DROP COLUMN correo_usuario,
    DROP COLUMN titulo_curso,
    DROP COLUMN nivel_curso,
    DROP COLUMN duracion_curso;

ALTER TABLE evaluaciones
    DROP COLUMN nombre_usuario,
    DROP COLUMN correo_usuario,
    DROP COLUMN titulo_curso;

ALTER TABLE certificados
    DROP COLUMN usuario_id,
    DROP COLUMN curso_id,
    DROP COLUMN nombre_usuario,
    DROP COLUMN titulo_curso,
    DROP COLUMN calificacion;
ALTER TABLE certificados
    ADD CONSTRAINT uq_certificados_evaluacion UNIQUE (evaluacion_id);

ALTER TABLE intentos_login DROP COLUMN correo;

ALTER TABLE resenas
    DROP COLUMN nombre_usuario,
    DROP COLUMN titulo_curso;

CREATE INDEX ix_inscripciones_usuario_id ON inscripciones(usuario_id);
CREATE INDEX ix_inscripciones_curso_id ON inscripciones(curso_id);
CREATE INDEX ix_lecciones_curso_id ON lecciones(curso_id);
CREATE INDEX ix_lecciones_instructor_id ON lecciones(instructor_id);
CREATE INDEX ix_evaluaciones_usuario_id ON evaluaciones(usuario_id);
CREATE INDEX ix_evaluaciones_curso_id ON evaluaciones(curso_id);
CREATE INDEX ix_resenas_usuario_id ON resenas(usuario_id);
CREATE INDEX ix_resenas_curso_id ON resenas(curso_id);
CREATE INDEX ix_intentos_login_usuario_id ON intentos_login(usuario_id);
