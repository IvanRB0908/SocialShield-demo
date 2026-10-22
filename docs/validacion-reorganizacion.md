# Validación de la reorganización

Verificación realizada el 22 de octubre de 2026 sobre el estado local de SocialShield, conservando las modificaciones previas del usuario.

| Comprobación | Resultado |
| --- | --- |
| Controladores REST de la aplicación | 5 |
| Combinaciones de método HTTP y ruta | Las 38 originales, comprobadas antes y después |
| Contratos HTTP antes de reorganizar | 8 pruebas correctas |
| Suite completa del backend tras reorganizar | 18 pruebas, 0 fallos, 0 errores, ninguna omitida |
| Suite Angular | 9 pruebas correctas en 5 archivos |
| Compilación Angular de producción, navegador y servidor | Correcta |
| Empaquetado del backend | Correcto |
| Peticiones de la colección Postman contra la API real de pruebas | 54 respuestas con el código esperado |
| Revisión de espacios y conflictos del diff | Correcta |
| Contenido de las tres migraciones aplicadas | Sin cambios, verificado por SHA-256 |

La colección se ejecutó con un cliente HTTP independiente que leyó sus peticiones, sustituyó los identificadores generados y comprobó sus códigos esperados. También verificó el cuerpo vacío de las respuestas 204. No se utilizó la interfaz de Postman ni su motor JavaScript. Las pruebas MockMvc complementan esa comprobación con los modelos JSON, el inventario de rutas, CORS y la cabecera Location de reportes.

## Protección de la base existente

Se utilizó `pg_dump --schema-only --no-owner --no-privileges` para leer únicamente las definiciones de la base local. No se copiaron registros reales.

Se inicializó un clúster PostgreSQL 18 separado, escuchando solo en 127.0.0.1 y en un puerto temporal, con una base `socialshield_test`. En ella se restauraron las definiciones y se insertaron únicamente niveles ficticios de referencia. La suite de integración y la API utilizada para las 54 peticiones apuntaron exclusivamente a esa base.

Flyway estuvo deshabilitado en ese entorno temporal porque ya se había restaurado la estructura actual; esta comprobación no pretende verificar la ejecución de las migraciones desde una base vacía. No se modificaron migraciones ni se ejecutaron sobre la base original. La API y PostgreSQL temporales se detuvieron al terminar.

## Límites y entorno

El flujo interactivo de Google con una cuenta y un token emitido por Google no se ejecutó. Se conservaron la validación criptográfica, las rutas y los servicios existentes; las pruebas cubren la configuración, el rechazo de un token vacío, la delegación del controlador y la reutilización de cuentas por correo verificado.

La instalación Java predeterminada del equipo apunta a un JDK incompleto de NetBeans (`lib/jvm.cfg` no está disponible). Para compilar y probar se usó el JDK 25 incluido en la extensión Java de VS Code, sin cambiar la configuración global del equipo:

```powershell
$env:JAVA_HOME = 'C:\Users\ivane\.vscode\extensions\redhat.java-1.58.0-win32-x64\jre\25.0.4.1-win32-x86_64'
```

Se usaron las dependencias locales instaladas. No se actualizaron versiones ni archivos de bloqueo y no se realizaron commits.

Consulta [la organización y sus límites funcionales](./organizacion-controladores.md) y [la colección importable en Postman](./SocialShield.postman_collection.json).
