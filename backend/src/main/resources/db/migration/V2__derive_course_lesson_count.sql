ALTER TABLE cursos DROP COLUMN lecciones;

ALTER TABLE lecciones
    ADD CONSTRAINT uq_lecciones_curso_numero UNIQUE (curso_id, numero);
