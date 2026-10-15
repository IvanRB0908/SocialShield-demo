import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CourseApiService } from '../services/course-api.service';
import { ApiCourse, CreateCourseRequest } from '../models/ApiCourse';

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './courses.html',
})
export class Courses implements OnInit {
  currentCourse!: CreateCourseRequest;
  courses: ApiCourse[] = [];
  loading = true;
  errorMessage = '';

  constructor(private readonly courseApi: CourseApiService) {}

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
    const courseTitle = document.getElementById('courseTitle') as HTMLInputElement;
    const courseDuration = document.getElementById('courseDuration') as HTMLInputElement;
    const courseLessons = document.getElementById('courseLessons') as HTMLInputElement;
    const courseAvailable = document.getElementById('courseAvailable') as HTMLInputElement;

    this.currentCourse = {
      title: courseTitle.value,
      duration: `${courseDuration.value} semanas`,
      lessons: Number.parseInt(courseLessons.value, 10),
      isAvailable: courseAvailable.checked,
    };

    this.courseApi.createCourse(this.currentCourse).subscribe({
      next: (course) => {
        this.courses = [
          ...this.courses,
          course,
        ];
      },
      error: () => (this.errorMessage = 'No se pudo guardar el curso.'),
    });
  }
}
