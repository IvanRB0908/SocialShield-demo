# Notas personales para explicar los cambios de SocialShield

Este documento es una guía personal para presentar los cambios en clase. Resume qué se hizo, dónde está y cómo explicar las decisiones principales.

## Resumen para empezar la exposición

El backend pasó de manejar algunos datos temporalmente en memoria a consultar y modificar PostgreSQL. Se implementaron seis recursos CRUD: cursos, usuarios, instructores, lecciones, inscripciones y evaluaciones. Las claves foráneas conectan los registros relacionados, y las migraciones de Flyway retiraron datos repetidos para mantener el esquema normalizado. El frontend ahora carga y crea cursos usando el backend. Las pruebas de integración realizan operaciones contra PostgreSQL y revierten sus cambios.

## Qué significa CRUD

- **Create:** crear un registro, normalmente con `POST`.
- **Read:** consultar una colección o un registro, con `GET`.
- **Update:** modificar un registro existente, con `PUT`.
- **Delete:** eliminarlo, con `DELETE`.

Las rutas por ID suelen devolver `404` si no existe el registro; los datos o referencias inválidas responden `400`; los duplicados o eliminaciones que romperían relaciones responden `409`. Una creación responde `201` y un borrado correcto `204`.

## Controllers y endpoints

El código está en `backend/src/main/java/com/socialshield/curso_defensa_api/controller/`.

| Controller | Recurso y rutas principales | Qué explicar |
| --- | --- | --- |
| `CourseController.java` | `/api/courses` y `/api/courses/{id}` | Mantiene health y home y delega el CRUD de cursos al servicio conectado a PostgreSQL. |
| `UserAuthController.java` | `/api/users`, `/api/users/{id}`, `/api/auth/login` | Combina CRUD de perfiles con registro e inicio de sesión. Las respuestas no incluyen contraseñas. |
| `InstructorController.java` | `/api/instructors` y `/api/instructors/{id}` | Administra los instructores que se referencian desde las lecciones. |
| `LessonController.java` | `/api/lessons` y `/api/lessons/{id}` | Crea y modifica lecciones enlazadas a un curso y un instructor. |
| `EnrollmentController.java` | `/api/enrollments`, `/api/enrollments/{id}`, `/api/users/{userId}/progress` | Administra inscripciones y calcula el resumen de progreso de un usuario. |
| `EvaluationController.java` | `/api/evaluations` y `/api/evaluations/{id}` | Administra la calificación, los intentos y el resultado de evaluaciones. |
| `SecurityReportController.java` | `/api/security-reports` | Conserva la API de reportes de riesgo, que todavía usa almacenamiento temporal en memoria. |

Los cuerpos de entrada y las respuestas tienen records separados en `backend/src/main/java/com/socialshield/curso_defensa_api/model/`. Por ejemplo, `CourseCrudRequest` es lo que recibe el backend y `CourseCrudResponse` es lo que devuelve. Esta separación evita devolver accidentalmente campos internos, como el hash de contraseña.

## Acceso a datos y reglas

`backend/src/main/java/com/socialshield/curso_defensa_api/service/DatabaseCrudService.java` contiene las consultas JDBC, las operaciones CRUD y varias reglas compartidas:

- Usa parámetros SQL (`?`) para valores recibidos en las peticiones.
- Une tablas relacionadas para devolver nombres y títulos sin guardar copias redundantes.
- Guarda contraseñas con PBKDF2; el login compara el hash sin devolverlo al cliente.
- Conserva las fechas de inscripción/evaluación existentes si un `PUT` no envía una fecha nueva.
- Responde con errores adecuados cuando una relación no existe, se repite una clave única o una eliminación no es válida.
- Evita borrar usuarios o cursos que tienen actividad relacionada; bloquea también el borrado de instructores asociados a lecciones.

La configuración de conexión está en `backend/src/main/resources/application.properties`. La URL, el usuario y la contraseña se pueden configurar con `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`; la contraseña real no debe escribirse en el código ni en este documento.

## Normalización de la base de datos

Las migraciones están en `backend/src/main/resources/db/migration/` y las ejecuta Flyway al arrancar el backend:

- `V1__normalize_socialshield_schema.sql` creó tablas para niveles e instructores y conectó las entidades mediante claves foráneas. También eliminó snapshots redundantes, por ejemplo nombres y correos de usuario/cursos copiados en inscripciones, evaluaciones y reseñas.
- `V2__derive_course_lesson_count.sql` quitó de `cursos` el contador duplicado de lecciones. El total que se muestra para un curso ahora se calcula contando las filas relacionadas en `lecciones`. La restricción única por `(curso_id, numero)` evita repetir el número de lección dentro de un curso.

Ejemplo para presentar: una inscripción guarda `usuario_id` y `curso_id`; para mostrar el nombre del usuario y el título del curso, el backend hace un `JOIN`. Si el nombre cambiara, se actualiza en la tabla del usuario, en vez de tener que sincronizar varias copias.

Las migraciones V1 y V2 ya se ejecutaron en `socialshield_db`. Para cambios de esquema futuros se agrega otra migración versionada; no se edita una migración que ya se aplicó.

## Frontend y APIs retiradas

- `frontend/src/app/courses/courses.ts` carga los cursos al iniciar y envía el formulario de creación mediante el servicio HTTP.
- `frontend/src/app/courses/courses.html` presenta los cursos, estados de carga/error y el formulario.
- `frontend/src/app/models/ApiCourse.ts` define los tipos de curso que intercambian frontend y backend. El formulario dejó de pedir un número de lecciones redundante; ese total proviene de las lecciones existentes.
- `backend/src/main/java/com/socialshield/curso_defensa_api/service/CourseService.java` conserva los datos de la página de inicio.
- `frontend/src/server.ts` ya no monta el servidor API temporal competidor: la API persistente está en Spring Boot.
- Se retiró `PruebaController` porque `/api/health` ya comprueba si el backend responde. También se quitó el modelo antiguo `CreateCourseRequest` después de que el contrato persistente lo reemplazara.

De las APIs de productividad agregadas anteriormente, registro/login e inscripciones/progreso ya consultan PostgreSQL. Los reportes de seguridad todavía están en memoria, por lo que no sobreviven al reinicio.

## Pruebas y cómo repetirlas

Las pruebas de `backend/src/test/java/com/socialshield/curso_defensa_api/CourseControllerTest.java` y `DatabaseCrudControllerTest.java` verifican creación, consulta, actualización y borrado contra PostgreSQL, además de referencias inválidas y conflictos por relaciones. La transacción de prueba revierte los registros temporales al finalizar. `SocialPlatformControllerTest.java` prueba el API temporal de reportes.

Para backend, configura `DB_PASSWORD` en la terminal local y ejecuta desde `backend`:

```powershell
.\mvnw.cmd test
```

Para frontend, desde `frontend`:

```powershell
npm test
npm run build
```

Al verificar estos cambios, pasaron las 7 pruebas del backend, las 3 pruebas del frontend y el build de Angular.

## Posible orden para explicar en clase

1. Enseñar las rutas de los controllers y explicar qué operación CRUD corresponde a cada verbo HTTP.
2. Abrir `DatabaseCrudService.java` para mostrar cómo JDBC ejecuta las consultas y combina las tablas.
3. Presentar las claves foráneas y las migraciones Flyway para justificar la normalización.
4. Mostrar en el frontend el formulario de cursos y cómo consume el backend.
5. Cerrar con las pruebas de integración y señalar que los reportes aún no tienen persistencia.
