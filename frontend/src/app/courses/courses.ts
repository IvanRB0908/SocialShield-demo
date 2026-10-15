import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CourseApiService } from '../services/course-api.service';
import { ApiCourse, CreateCourseRequest } from '../models/ApiCourse';

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './courses.html',
})
export class Courses implements OnInit {
  readonly courseForm;
  courses: ApiCourse[] = [];
  loading = true;
  saving = false;
  errorMessage = '';

  constructor(
    formBuilder: FormBuilder,
    private readonly courseApi: CourseApiService,
  ) {
    this.courseForm = formBuilder.nonNullable.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      durationHours: [1, [Validators.required, Validators.min(1)]],
      isAvailable: [false],
    });
  }

  ngOnInit(): void {
    this.courseApi.getCourses().subscribe({
      next: (courses) => {
        this.courses = courses;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'No se pudieron cargar los cursos.';
        this.loading = false;
      },
    });
  }

  saveCourse(): void {
    this.errorMessage = '';
    if (this.courseForm.invalid) {
      this.courseForm.markAllAsTouched();
      return;
    }

    const values = this.courseForm.getRawValue();
    const request: CreateCourseRequest = {
      title: values.title.trim(),
      duration: values.durationHours,
      available: values.isAvailable,
      icon: '+',
      tag: values.isAvailable ? 'Nuevo' : 'Próximamente',
      description: 'Curso añadido desde la plataforma SocialShield.',
      level: 'Básico',
    };

    this.saving = true;
    this.courseApi.createCourse(request).subscribe({
      next: (course) => {
        this.courses = [...this.courses, course];
        this.saving = false;
      },
      error: () => {
        this.errorMessage = 'No se pudo guardar el curso.';
        this.saving = false;
      },
    });
  }
}
