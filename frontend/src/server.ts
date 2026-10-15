import {
  AngularNodeAppEngine,
  createNodeRequestHandler,
  isMainModule,
  writeResponseToNodeResponse,
} from '@angular/ssr/node';
import express from 'express';
import { join } from 'node:path';
import type Course from './app/models/Course';

const browserDistFolder = join(import.meta.dirname, '../browser');

const app = express();
const angularApp = new AngularNodeAppEngine();

// Almacenamiento temporal en memoria. Los cursos se pierden cuando se reinicia el servidor.
const apiCourses: Course[] = [];

// Permite recibir cuerpos JSON en las peticiones POST de la API.
app.use(express.json());

/**
 * Devuelve todos los cursos registrados en la API.
 *
 * Método: GET
 * Ruta: /api/courses
 * Respuesta: 200 con un array de cursos.
 */
app.get('/api/courses', (_req, res) => {
  res.json(apiCourses);
});

/**
 * Crea un curso nuevo.
 *
 * Método: POST
 * Ruta: /api/courses
 * Cuerpo esperado: { title, duration, lessons, isAvailable }
 * Respuestas:
 * - 201 si el curso se crea correctamente.
 * - 400 si algún campo tiene un tipo o valor inválido.
 */
app.post('/api/courses', (req, res) => {
  // Se usa Partial porque el cuerpo recibido puede estar incompleto y debe validarse.
  const { title, duration, lessons, isAvailable } = req.body as Partial<Course>;

  // Comprueba que cada propiedad coincide con el modelo Course.
  if (
    typeof title !== 'string' ||
    !title.trim() ||
    typeof duration !== 'number' ||
    !Number.isFinite(duration) ||
    typeof lessons !== 'number' ||
    !Number.isInteger(lessons) ||
    typeof isAvailable !== 'boolean'
  ) {
    res.status(400).json({ message: 'Los datos del curso no son válidos.' });
    return;
  }

  // Limpia el título antes de crear el objeto que se guardará.
  const course: Course = {
    title: title.trim(),
    duration,
    lessons,
    isAvailable,
  };

  // Guarda el curso temporalmente y lo devuelve como respuesta.
  apiCourses.push(course);
  res.status(201).json(course);
});

/**
 * Serve static files from /browser
 */
app.use(
  express.static(browserDistFolder, {
    maxAge: '1y',
    index: false,
    redirect: false,
  }),
);

/**
 * Handle all other requests by rendering the Angular application.
 */
app.use((req, res, next) => {
  angularApp
    .handle(req)
    .then((response) =>
      response ? writeResponseToNodeResponse(response, res) : next(),
    )
    .catch(next);
});

/**
 * Start the server if this module is the main entry point, or it is ran via PM2.
 * The server listens on the port defined by the `PORT` environment variable, or defaults to 4000.
 */
if (isMainModule(import.meta.url) || process.env['pm_id']) {
  const port = process.env['PORT'] || 4000;
  app.listen(port, (error) => {
    if (error) {
      throw error;
    }

    console.log(`Node Express server listening on http://localhost:${port}`);
  });
}

/**
 * Request handler used by the Angular CLI (for dev-server and during build) or Firebase Cloud Functions.
 */
export const reqHandler = createNodeRequestHandler(app);
