# SocialShield

Plataforma con frontend Angular y backend Spring Boot conectados a PostgreSQL.

## Requisitos

- Node.js y npm compatibles con Angular CLI 22.
- Java 25 o superior; el proyecto incluye Maven Wrapper.
- PostgreSQL con la base `socialshield_db`.

## Ejecutar

Configura las variables del backend en PowerShell. Usa la contraseña de tu instalación local; no la guardes en el repositorio:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/socialshield_db"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "<contraseña-local>"
cd backend
.\mvnw.cmd spring-boot:run
```

Spring Boot inicia en `http://localhost:8080` y Flyway aplica las migraciones pendientes al arrancar. En otra terminal ejecuta el frontend:

```powershell
cd frontend
npm ci
npm start
```

Abre `http://localhost:4200/`. El frontend consume el backend en `http://localhost:8080`.

## API y base de datos

El backend tiene CRUD persistente para cursos, usuarios, instructores, lecciones, inscripciones y evaluaciones. Consulta [la documentación detallada de endpoints, modelos y migraciones](./backend/README.md).

La base usa relaciones y claves foráneas para evitar duplicar nombres o datos de entidades relacionadas. Los conteos de lecciones se calculan a partir de las lecciones registradas. El endpoint de reportes de seguridad sigue siendo temporal y se pierde al reiniciar el backend.

## Pruebas

```powershell
cd frontend
npm test
```

Las pruebas del backend requieren PostgreSQL disponible y `DB_PASSWORD` configurada:

```powershell
cd backend
.\mvnw.cmd test
```
