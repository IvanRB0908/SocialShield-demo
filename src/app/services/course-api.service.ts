import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import Course from '../models/Course';

@Injectable({ providedIn: 'root' })
export class CourseApiService {
  // Ruta base de los endpoints de cursos del servidor Express.
  private readonly apiUrl = '/api/courses';

  constructor(private readonly http: HttpClient) {}

  /**
   * Solicita al servidor todos los cursos disponibles.
   * @returns Observable con la lista de cursos recibida desde la API.
   */
  getCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(this.apiUrl);
  }

  /**
   * Envía un curso al servidor para registrarlo.
   * @param course Curso que se enviará en el cuerpo JSON de la petición.
   * @returns Observable con el curso creado por la API.
   */
  createCourse(course: Course): Observable<Course> {
    return this.http.post<Course>(this.apiUrl, course);
  }
}