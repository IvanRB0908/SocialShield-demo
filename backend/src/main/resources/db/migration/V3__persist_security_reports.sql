CREATE TABLE reportes_seguridad (
    id UUID PRIMARY KEY,
    categoria VARCHAR(80) NOT NULL,
    descripcion VARCHAR(2000) NOT NULL,
    url VARCHAR(2000),
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
