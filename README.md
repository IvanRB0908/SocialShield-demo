# SocialShield

Repositorio con el frontend Angular y el backend Spring Boot organizados en carpetas separadas.

## Requisitos

- Node.js y npm compatibles con Angular CLI 22.
- Java 25 o superior para Spring Boot.

## Ejecutar el proyecto

Abre dos terminales desde la raíz del repositorio.

### Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

El backend queda disponible en `http://localhost:8080`.

### Frontend

En la segunda terminal:

```powershell
cd frontend
npm ci
npm start
```

Abre `http://localhost:4200/` en el navegador. El frontend consulta el backend en `http://localhost:8080`.

## Endpoints del backend

| Controller | Endpoint | Respuesta |
| --- | --- | --- |
| `PruebaController` | `http://localhost:8080/api/prueba` | Mensaje de confirmación |
| `CourseController` | `http://localhost:8080/api/health` | `OK` |
| `CourseController` | `http://localhost:8080/api/home` | Datos de inicio en JSON |
| `CourseController` | `http://localhost:8080/api/courses` | Cursos en JSON |

## Pruebas

Frontend:

```powershell
cd frontend
npm test
```

Backend:

```powershell
cd backend
.\mvnw.cmd test
```

La evidencia de pruebas de los controllers está en [`docs/evidence/PRUEBAS DE LOS CONTROLLER.pdf`](./docs/evidence/PRUEBAS%20DE%20LOS%20CONTROLLER.pdf).
