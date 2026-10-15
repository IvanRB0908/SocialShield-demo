import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiCourse, CreateCourseRequest } from '../models/ApiCourse';

export interface HomePageResponse {
  platformName: string;
  stats: { value: string; label: string }[];
  sections: string[];
}

@Injectable({ providedIn: 'root' })
export class CourseApiService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private readonly http: HttpClient) {}

  /**
   * Solicita al servidor todos los cursos disponibles.
   * @returns Observable con la lista de cursos recibida desde la API.
   */
  getCourses(): Observable<ApiCourse[]> {
    return this.http.get<ApiCourse[]>(`${this.apiUrl}/courses`);
  }

  getHomePage(): Observable<HomePageResponse> {
    return this.http.get<HomePageResponse>(`${this.apiUrl}/home`);
  }

  /**
   * Envía un curso al servidor para registrarlo.
   * @param course Curso que se enviará en el cuerpo JSON de la petición.
   * @returns Observable con el curso creado por la API.
   */
  createCourse(course: CreateCourseRequest): Observable<ApiCourse> {
    return this.http.post<ApiCourse>(`${this.apiUrl}/courses`, course);
  }
}