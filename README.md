# SocialShield

Repositorio con los dos proyectos de SocialShield:

- Frontend Angular en la raíz del repositorio.
- Backend Spring Boot en [`backend/`](./backend/).

## Requisitos

- Node.js y npm, en versiones compatibles con Angular CLI 22.
- Java 25 o superior para Spring Boot.

## Ejecutar el proyecto

Abre dos terminales en la carpeta raíz del repositorio.

### 1. Iniciar el backend

En la primera terminal:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

El backend queda disponible en `http://localhost:8080`. Mantén esta terminal abierta.

### 2. Iniciar el frontend

En la segunda terminal, desde la carpeta raíz del repositorio:

```powershell
npm ci
npm start
```

Abre `http://localhost:4200/` en el navegador.

## Controllers y endpoints

Todos los endpoints implementados para esta actividad usan GET:

| Controller | Endpoint | Respuesta |
| --- | --- | --- |
| `PruebaController` | `http://localhost:8080/api/prueba` | Mensaje de confirmación del backend |
| `CourseController` | `http://localhost:8080/api/health` | `OK` |
| `CourseController` | `http://localhost:8080/api/home` | Datos de inicio de SocialShield en JSON |
| `CourseController` | `http://localhost:8080/api/courses` | Cursos en formato JSON |

Puedes abrir estas direcciones en un navegador o crear una petición GET a cada una en Postman.

> La actividad solicita usar exclusivamente GET. Por ello, el backend no incluye una operación POST para guardar cursos; la función de guardar del frontend no está disponible con esta configuración.

## Pruebas automatizadas

Desde la carpeta `backend`:

```powershell
.\mvnw.cmd test
```

## Evidencia de pruebas

El PDF con las capturas de navegador y Postman se encuentra en [`docs/evidence/PRUEBAS DE LOS CONTROLLER.pdf`](./docs/evidence/PRUEBAS%20DE%20LOS%20CONTROLLER.pdf).

---

## Comandos adicionales del frontend

Para compilar el frontend:

```bash
npm run build
```

Para ejecutar sus pruebas:

```bash
npm test
```

## Recursos de Angular

Consulta la [documentación de Angular CLI](https://angular.dev/tools/cli) para más información.

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 22.1.7.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
