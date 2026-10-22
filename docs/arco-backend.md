# Solicitudes ARCO — CRUD local

ARCO está únicamente en el backend. Se quitaron la pantalla `/perfil/arco`, su enlace y sus pruebas Angular. Se conserva la configuración SSR del perfil.

El módulo administra registros de solicitudes de Acceso, Rectificación, Cancelación y Oposición. No exporta datos, modifica perfiles ni cancela cuentas automáticamente. `RESUELTA` es un estado administrativo, no una prueba de que se ejecutó una acción sobre datos personales.

Es un CRUD universitario local, con el mismo esquema de acceso que los CRUD existentes. No añade autenticación: usa exclusivamente descripciones y respuestas ficticias. No incluyas nombres, correos, contraseñas ni documentos reales.

## Archivos

- `backend/src/main/java/com/socialshield/curso_defensa_api/controller/ArcoController.java`: rutas y validaciones.
- `backend/src/main/java/com/socialshield/curso_defensa_api/service/ArcoService.java`: SQL parametrizado con `JdbcTemplate`.
- `backend/src/main/java/com/socialshield/curso_defensa_api/model/ArcoCrudRequest.java`: cuerpo de entrada.
- `backend/src/main/java/com/socialshield/curso_defensa_api/model/ArcoCrudResponse.java`: respuesta con el identificador generado.
- `backend/src/main/resources/db/migration/V4__create_arco_requests.sql`: tabla nueva `solicitudes_arco`, sin relaciones con datos de usuarios.

## Endpoints

| Método | Ruta | Resultado |
| --- | --- | --- |
| GET | `/api/arco-requests` | Lista de solicitudes, `200` |
| GET | `/api/arco-requests/{id}` | Consulta por ID, `200` o `404` |
| POST | `/api/arco-requests` | Crear, `201` y cabecera `Location` |
| PUT | `/api/arco-requests/{id}` | Reemplazar campos, `200` o `404` |
| DELETE | `/api/arco-requests/{id}` | Borrar solo la solicitud, `204` o `404` |

Tipos: `ACCESO`, `RECTIFICACION`, `CANCELACION`, `OPOSICION`.

Estados: `PENDIENTE`, `EN_REVISION`, `RESUELTA`, `RECHAZADA`. No hay una máquina de estados: PUT puede cambiar entre estos valores.

La descripción es obligatoria y admite hasta 2000 caracteres. La respuesta es opcional y admite hasta 2000 caracteres. El estado puede omitirse al crear (se utiliza `PENDIENTE`); es obligatorio al actualizar. El ID lo genera PostgreSQL.

POST con `Content-Type: application/json`:

```json
{
  "type": "ACCESO",
  "description": "Ejemplo ficticio: consultar las categorías de datos de una cuenta de prueba."
}
```

PUT con `Content-Type: application/json`:

```json
{
  "type": "ACCESO",
  "description": "Ejemplo ficticio: consultar las categorías de datos de una cuenta de prueba.",
  "status": "RESUELTA",
  "response": "Respuesta de prueba para la demostración académica."
}
```

Los campos inválidos producen `400`. No hay historial, roles, control de versiones, servicios externos ni dependencias nuevas.

## Migración y pruebas

Flyway aplicará V4 al iniciar el backend sobre una base compatible con V1–V3. No se ejecutó la migración contra la base local durante esta implementación. El repositorio conserva la dependencia de un esquema previo compatible; V4 no resuelve las diferencias del antiguo `bd/normalizado.sql`.

Las pruebas aisladas no usan PostgreSQL ni ejecutan migraciones:

```powershell
cd backend
.\mvnw.cmd '-Dtest=ArcoControllerTest,ArcoServiceTest,ControllerContractTest' test
```

Verifican contratos HTTP, validación, SQL parametrizado y registros inexistentes con mocks. La migración y las consultas PostgreSQL requieren una verificación posterior en una base desechable.

En esta implementación pasaron las 16 pruebas aisladas del backend y las 7 pruebas Angular, además de la compilación del frontend. Para las pruebas Maven se utilizó temporalmente el JDK 21 disponible con `-Djava.version=21`, porque el JDK 25 configurado no encuentra `lib/jvm.cfg`. No se cambió la versión declarada en el proyecto ni la configuración Java del sistema.
