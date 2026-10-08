# SocialShield API

Backend REST de SocialShield desarrollado con Spring Boot.

## Requisitos

- Java 25 o superior.
- No es necesario instalar Maven; el proyecto incluye Maven Wrapper.

## Ejecutar el backend

En PowerShell, desde esta carpeta, ejecuta:

```powershell
.\mvnw.cmd spring-boot:run
```

Spring Boot inicia en `http://localhost:8080`. Mantén abierta la terminal mientras pruebas los endpoints.

Si el puerto 8080 ya está ocupado, puedes iniciar esta copia en otro puerto:

```powershell
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.arguments=--server.port=18080'
```

En ese caso, reemplaza `8080` por `18080` en las URLs de prueba.

## Controllers y endpoints GET

| Controller | Método | URL | Respuesta |
| --- | --- | --- | --- |
| `PruebaController` | GET | `http://localhost:8080/api/prueba` | Mensaje de confirmación del backend |
| `CourseController` | GET | `http://localhost:8080/api/health` | `OK` |
| `CourseController` | GET | `http://localhost:8080/api/home` | Datos de la página principal en JSON |
| `CourseController` | GET | `http://localhost:8080/api/courses` | Cursos disponibles en JSON |

Para verificar una ruta en el navegador, pega la URL en la barra de direcciones. En Postman, crea una petición GET con la misma URL y pulsa **Send**. Las respuestas correctas deben mostrar el contenido esperado y el estado HTTP `200 OK`.

## Ejecutar las pruebas

```powershell
.\mvnw.cmd test
```
