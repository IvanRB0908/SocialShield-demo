# SocialShield API

Backend REST de SocialShield con Spring Boot, JDBC y PostgreSQL.

## Requisitos y configuración

- Java 25 o superior; se incluye Maven Wrapper.
- PostgreSQL y una base de datos `socialshield_db`.
- Una cuenta PostgreSQL con permisos para leer/escribir datos y ejecutar las migraciones Flyway.

Por defecto se conecta a `jdbc:postgresql://localhost:5432/socialshield_db` como `postgres`. Configura las variables en PowerShell antes de iniciar el backend; nunca guardes credenciales reales en el código:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/socialshield_db"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "<contraseña-local>"
.\mvnw.cmd spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`. Flyway ejecuta las migraciones pendientes al iniciar. Si el puerto está ocupado:

```powershell
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.arguments=--server.port=18080'
```

## Endpoints

Las operaciones de creación responden `201 Created`, las consultas y actualizaciones exitosas responden `200 OK`, y los borrados exitosos responden `204 No Content`. Un identificador inexistente responde `404`; una referencia o entrada inválida responde `400`; duplicados o borrados bloqueados por actividad relacionada responden `409`.

| Recurso | Rutas |
| --- | --- |
| Sistema | `GET /api/health`, `GET /api/home` |
| Cursos | `GET/POST /api/courses`, `GET/PUT/DELETE /api/courses/{id}` |
| Usuarios y autenticación | `GET/POST /api/users`, `GET/PUT/DELETE /api/users/{id}`, `POST /api/auth/login` |
| Instructores | `GET/POST /api/instructors`, `GET/PUT/DELETE /api/instructors/{id}` |
| Lecciones | `GET/POST /api/lessons`, `GET/PUT/DELETE /api/lessons/{id}` |
| Inscripciones | `GET/POST /api/enrollments`, `GET/PUT/DELETE /api/enrollments/{id}`, `GET /api/users/{userId}/progress` |
| Evaluaciones | `GET/POST /api/evaluations`, `GET/PUT/DELETE /api/evaluations/{id}` |
| Reportes de seguridad | `GET/POST /api/security-reports` |

Cada recurso CRUD incluye lectura de colección y por ID, creación, actualización y borrado. Las contraseñas de usuario se almacenan con PBKDF2 y nunca se devuelven en respuestas. El inicio de sesión valida credenciales, pero todavía no emite tokens ni protege las rutas mediante autorización. Los reportes de seguridad permanecen en memoria; no son persistentes ni forman parte de los CRUD de PostgreSQL.

## Modelos JSON principales

Crear o actualizar un curso (el número de lecciones en la respuesta se calcula desde las filas de `/api/lessons` asociadas):

```json
{
  "title": "Seguridad digital",
  "duration": 12,
  "available": true,
  "icon": "shield",
  "tag": "Nuevo",
  "description": "Fundamentos de seguridad digital.",
  "level": "Básico"
}
```

`level` debe coincidir con un nivel existente (`Básico`, `Intermedio` o `Avanzado` por defecto).

Crear un usuario con `POST /api/users`:

```json
{
  "name": "María López",
  "email": "maria@example.com",
  "password": "una-clave-segura"
}
```

Actualizar un usuario usa el mismo perfil; `password` puede omitirse para conservar la contraseña actual. Para iniciar sesión, `POST /api/auth/login` acepta `{"email":"maria@example.com","password":"una-clave-segura"}`.

Crear un instructor:

```json
{"name":"Ana García","email":"ana@example.com"}
```

Crear una lección relacionándola con curso e instructor:

```json
{
  "courseId": 1,
  "instructorId": 1,
  "number": 1,
  "title": "Protege tus cuentas",
  "content": "Contenido de la lección.",
  "durationMinutes": 25
}
```

Crear una inscripción o evaluación requiere referencias existentes:

```json
{"userId":1,"courseId":1,"progress":0}
```

```json
{"userId":1,"courseId":1,"score":92.5,"attempts":1,"passed":true}
```

Las fechas `enrolledAt` y `evaluatedAt` son opcionales al crear; si se omiten, se usa la fecha actual. En una actualización, omitirlas conserva la fecha ya guardada.

## Esquema y normalización

Las migraciones versionadas están en `src/main/resources/db/migration`. `V1` normalizó relaciones de nivel e instructor y eliminó snapshots que duplicaban datos disponibles mediante sus claves foráneas (incluidos usuario/curso en inscripciones, evaluaciones, reseñas y certificados). `V2` elimina el contador redundante de lecciones de `cursos`; el total ahora se calcula desde `lecciones`, que además garantiza un único número de lección por curso. Las migraciones se aplican de forma incremental; no edites migraciones que ya se ejecutaron, agrega una nueva versión.

## Pruebas

Las pruebas de integración ejecutan contra PostgreSQL y revierten sus cambios:

```powershell
$env:DB_PASSWORD = "<contraseña-local>"
try {
    .\mvnw.cmd test
} finally {
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
}
```
