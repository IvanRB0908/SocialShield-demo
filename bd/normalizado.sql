CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    biografia TEXT
);

CREATE TABLE niveles (
    id SERIAL PRIMARY KEY,
    tipo_nivel VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE categorias_curso (
    id SERIAL PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE navegadores (
    id SERIAL PRIMARY KEY,
    tipo_navegador VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE instructores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL
);

CREATE TABLE cursos (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    duracion INTEGER NOT NULL,
    disponible BOOLEAN NOT NULL,
    icono VARCHAR(50),
    etiqueta VARCHAR(50),
    descripcion TEXT,
    nivel_id INTEGER NOT NULL,
    categoria_id INTEGER NOT NULL,
    CONSTRAINT fk_cursos_nivel
        FOREIGN KEY (nivel_id)
        REFERENCES niveles(id),
    CONSTRAINT fk_cursos_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias_curso(id)
);

CREATE TABLE inscripciones (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    curso_id INTEGER NOT NULL,
    fecha_inscripcion DATE NOT NULL,
    progreso INTEGER NOT NULL,
    CONSTRAINT fk_inscripciones_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),
    CONSTRAINT fk_inscripciones_curso
        FOREIGN KEY (curso_id)
        REFERENCES cursos(id),
    CONSTRAINT uq_inscripcion
        UNIQUE (usuario_id, curso_id)
);

CREATE TABLE lecciones (
    id SERIAL PRIMARY KEY,
    curso_id INTEGER NOT NULL,
    instructor_id INTEGER NOT NULL,
    numero INTEGER NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    contenido TEXT,
    duracion_min INTEGER NOT NULL,
    CONSTRAINT fk_lecciones_curso
        FOREIGN KEY (curso_id)
        REFERENCES cursos(id),
    CONSTRAINT fk_lecciones_instructor
        FOREIGN KEY (instructor_id)
        REFERENCES instructores(id)
);

CREATE TABLE evaluaciones (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    curso_id INTEGER NOT NULL,
    calificacion NUMERIC(5,2) NOT NULL,
    intentos INTEGER,
    aprobado BOOLEAN NOT NULL,
    fecha_evaluacion DATE NOT NULL,
    CONSTRAINT fk_evaluaciones_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),
    CONSTRAINT fk_evaluaciones_curso
        FOREIGN KEY (curso_id)
        REFERENCES cursos(id)
);

CREATE TABLE certificados (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    curso_id INTEGER NOT NULL,
    calificacion NUMERIC(5,2) NOT NULL,
    fecha_emision DATE NOT NULL,
    folio VARCHAR(30) NOT NULL UNIQUE,
    CONSTRAINT fk_certificados_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),
    CONSTRAINT fk_certificados_curso
        FOREIGN KEY (curso_id)
        REFERENCES cursos(id)
);

INSERT INTO usuarios
(nombre, correo, contrasena, telefono, biografia)
VALUES
('Ana López', 'ana.lopez@gmail.com', 'ana123', '5512345678', 'Estudiante interesada en programación.'),
('Carlos Martínez', 'carlos.martinez@gmail.com', 'carlos123', '5523456789', 'Desarrollador en formación.'),
('Sofía Hernández', 'sofia.hernandez@gmail.com', 'sofia123', '5534567890', 'Estudiante de desarrollo web.'),
('Miguel Ramírez', 'miguel.ramirez@gmail.com', 'miguel123', '5545678901', 'Interesado en bases de datos.'),
('Valeria Torres', 'valeria.torres@gmail.com', 'valeria123', '5556789012', 'Estudiante de tecnología.');

INSERT INTO niveles
(tipo_nivel)
VALUES
('Principiante'),
('Intermedio'),
('Avanzado');

INSERT INTO categorias_curso
(nombre_categoria)
VALUES
('Programación'),
('Desarrollo Web'),
('Bases de Datos'),
('Diseño'),
('Tecnologías Móviles');

INSERT INTO navegadores
(tipo_navegador)
VALUES
('Google Chrome'),
('Mozilla Firefox'),
('Microsoft Edge'),
('Safari'),
('Opera');

INSERT INTO instructores
(nombre, correo)
VALUES
('Laura González', 'laura.gonzalez@cursos.com'),
('Daniel Pérez', 'daniel.perez@cursos.com'),
('Mariana Sánchez', 'mariana.sanchez@cursos.com'),
('Jorge Ramírez', 'jorge.ramirez@cursos.com'),
('Fernanda Castillo', 'fernanda.castillo@cursos.com');

INSERT INTO cursos
(titulo, duracion, disponible, icono, etiqueta, descripcion, nivel_id, categoria_id)
VALUES
('Introducción a Java', 40, TRUE, 'java.png', 'Nuevo', 'Curso introductorio al lenguaje de programación Java.', 1, 1),
('Desarrollo Web con HTML y CSS', 30, TRUE, 'web.png', 'Popular', 'Aprende a crear páginas web utilizando HTML y CSS.', 1, 2),
('PostgreSQL desde Cero', 35, TRUE, 'postgresql.png', 'Recomendado', 'Curso para aprender bases de datos y consultas SQL.', 2, 3),
('JavaScript Avanzado', 45, TRUE, 'javascript.png', 'Avanzado', 'Conceptos avanzados de programación con JavaScript.', 3, 2),
('Diseño de Interfaces', 25, TRUE, 'diseno.png', 'Nuevo', 'Principios básicos para diseñar interfaces de usuario.', 2, 4);

INSERT INTO inscripciones
(usuario_id, curso_id, fecha_inscripcion, progreso)
VALUES
(1, 1, '2026-09-01', 80),
(1, 2, '2026-09-05', 60),
(2, 1, '2026-09-03', 100),
(2, 3, '2026-09-10', 45),
(3, 2, '2026-09-08', 90),
(4, 3, '2026-09-12', 70),
(5, 4, '2026-09-15', 35);

INSERT INTO lecciones
(curso_id, instructor_id, numero, titulo, contenido, duracion_min)
VALUES
(1, 1, 1, 'Introducción a Java', 'Conceptos básicos del lenguaje Java.', 30),
(1, 1, 2, 'Variables y tipos de datos', 'Uso de variables y tipos de datos en Java.', 35),
(1, 1, 3, 'Condicionales', 'Estructuras if, else y switch.', 40),
(2, 2, 1, 'Introducción a HTML', 'Estructura básica de una página HTML.', 30),
(2, 2, 2, 'CSS básico', 'Uso de estilos y propiedades CSS.', 40),
(3, 3, 1, 'Introducción a PostgreSQL', 'Conceptos básicos de bases de datos.', 35),
(3, 3, 2, 'Consultas SELECT', 'Consultas básicas utilizando SELECT.', 40),
(4, 4, 1, 'Funciones en JavaScript', 'Creación y utilización de funciones.', 45),
(5, 5, 1, 'Principios de diseño', 'Principios básicos para diseñar interfaces.', 30);

INSERT INTO evaluaciones
(usuario_id, curso_id, calificacion, intentos, aprobado, fecha_evaluacion)
VALUES
(1, 1, 90.00, 1, TRUE, '2026-09-20'),
(1, 2, 85.00, 2, TRUE, '2026-09-22'),
(2, 1, 95.00, 1, TRUE, '2026-09-21'),
(2, 3, 78.00, 2, TRUE, '2026-09-25'),
(3, 2, 88.00, 1, TRUE, '2026-09-24'),
(4, 3, 65.00, 3, FALSE, '2026-09-27'),
(5, 4, 92.00, 1, TRUE, '2026-09-29');

INSERT INTO certificados
(usuario_id, curso_id, calificacion, fecha_emision, folio)
VALUES
(1, 1, 90.00, '2026-09-21', 'CERT-2026-0001'),
(2, 1, 95.00, '2026-09-22', 'CERT-2026-0002'),
(3, 2, 88.00, '2026-09-25', 'CERT-2026-0003'),
(2, 3, 78.00, '2026-09-26', 'CERT-2026-0004'),
(5, 4, 92.00, '2026-09-30', 'CERT-2026-0005');