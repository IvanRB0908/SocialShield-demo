# Organización de SocialShield

Los siete controladores originales se agruparon en cinco sin eliminar ninguna de las 38 combinaciones de método HTTP y ruta. El módulo ARCO añade después un sexto controlador independiente:

| Controlador | Responsabilidad y rutas |
| --- | --- |
| UserAuthController | CRUD de usuarios, login y configuración/autenticación Google |
| CourseController | CRUD de cursos, inicio y estado de la API |
| LessonController | CRUD de lecciones e instructores, que forman el contenido educativo |
| EnrollmentController | CRUD de inscripciones y evaluaciones, y consulta del progreso del alumno |
| SecurityReportController | Creación y consulta de reportes persistidos en PostgreSQL |

Cada método conserva su ruta original. La agrupación no cambia los modelos JSON, las validaciones, los servicios, CORS ni los códigos HTTP. Se conservaron también los endpoints usados desde Postman aunque una vista no los consuma actualmente.

Cinco controladores es una organización del código, no una limitación a cinco entidades ni a CRUD exclusivamente. El login Google, `/api/home`, `/api/health` y los reportes siguen siendo necesarios para conservar el comportamiento. Exigir cinco controladores exclusivamente CRUD y eliminar cualquier otra función sería incompatible con esa conservación.

## Simplificaciones

- La actualización del usuario ya no recibe un booleano de validación que siempre era `false`; sigue permitiendo omitir la contraseña y mantiene los mismos límites si se proporciona una.
- Login, registro y perfil leen los valores mediante `getRawValue()`, ya utilizado en el formulario de cursos. Se retiraron las interfaces y las asignaciones manuales que duplicaban el formulario; el correo mantiene su normalización.
- Las validaciones de los recursos agrupados tienen nombres explícitos para evitar confusiones.
- Se mantienen JDBC parametrizado, las transacciones, las claves foráneas, PBKDF2 y la verificación criptográfica de Google. Son necesarios para la integridad y seguridad del comportamiento actual.

## Alcance funcional existente

Cursos y estadísticas consultan la API. Google consulta la configuración y envía su credencial al backend. El login/registro manual y la edición/eliminación del perfil en Angular siguen usando almacenamiento local; esta reorganización no los convierte en operaciones PostgreSQL. El backend conserva sus operaciones HTTP de usuarios y login para Postman.

ARCO se retiró del frontend. El nuevo `ArcoController` añade un CRUD independiente en `/api/arco-requests`, sin modificar las 38 rutas anteriores. Consulta `docs/arco-backend.md` para ver sus campos y ejemplos.

## Verificación

`ControllerContractTest` comprueba el inventario exacto de las 38 rutas y las operaciones CRUD con JSON y códigos 200/201/204/400/404/409, además de autenticación delegada, Google config, progreso, reportes y CORS, sin acceder a bases ni servicios externos.

Las pruebas Angular cubren perfil, registro y login, además de las pruebas existentes. Las pruebas ARCO verifican el CRUD del backend con mocks y datos ficticios. La colección `SocialShield.postman_collection.json` conserva las rutas anteriores; los ejemplos ARCO están en `docs/arco-backend.md`. Usa una instancia de pruebas: los reportes permanecen en esa base porque su API no ofrece DELETE.

La autenticación real con una cuenta Google requiere la configuración OAuth y una credencial válida emitida por Google; un servicio simulado no sustituye esa comprobación manual.
