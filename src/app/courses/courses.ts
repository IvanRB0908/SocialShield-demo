import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CourseApiService } from '../services/course-api.service';
import Course from '../models/Course';

interface CourseCard extends Course {
  icon: string;
  tag: string;
  description: string;
  level: string;
}

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './courses.html',
})
export class Courses {
  currentCourse!: Course;

  courses: CourseCard[] = [
    { icon: '✉', tag: 'Más popular', title: 'Detección de Phishing', description: 'Aprende a identificar correos, mensajes y sitios web fraudulentos antes de que sea tarde. Casos reales y simulaciones interactivas.', duration: 4, level: 'Principiante', lessons: 12, isAvailable: true },
    { icon: '◎', tag: 'Nuevo', title: 'Seguridad en Redes Sociales', description: 'Protege tu identidad digital, configura la privacidad correctamente y reconoce perfiles falsos y estafas en plataformas sociales.', duration: 3, level: 'Intermedio', lessons: 9, isAvailable: true },
    { icon: '⌕', tag: 'Esencial', title: 'Llamadas de Fraude', description: 'Identifica vishing, llamadas de suplantación de identidad y técnicas de presión psicológica usadas por estafadores telefónicos.', duration: 2, level: 'Principiante', lessons: 7, isAvailable: true },
    { icon: '▤', tag: 'Avanzado', title: 'Manipulación Psicológica', description: 'Comprende las tácticas de persuasión, urgencia y miedo que usan los atacantes para lograr que compartas información confidencial.', duration: 3, level: 'Intermedio', lessons: 10, isAvailable: true },
    { icon: '▧', tag: 'Nuevo', title: 'Seguridad en Mensajería', description: 'Detecta cadenas de desinformación, enlaces maliciosos y estafas enviadas por WhatsApp, Telegram y otras apps.', duration: 2, level: 'Principiante', lessons: 8, isAvailable: true },
  ];

  constructor(private readonly courseApi: CourseApiService) {}

  saveCourse(): void {
    const courseTitle = document.getElementById('courseTitle') as HTMLInputElement;
    const courseDuration = document.getElementById('courseDuration') as HTMLInputElement;
    const courseLessons = document.getElementById('courseLessons') as HTMLInputElement;
    const courseAvailable = document.getElementById('courseAvailable') as HTMLInputElement;

    this.currentCourse = {
      title: courseTitle.value,
      duration: Number(courseDuration.value),
      lessons: Number.parseInt(courseLessons.value, 10),
      isAvailable: courseAvailable.checked,
    };

    this.courseApi.createCourse(this.currentCourse).subscribe({
      next: (course) => {
        this.courses = [
          ...this.courses,
          {
            ...course,
            icon: '＋',
            tag: course.isAvailable ? 'Nuevo' : 'Próximamente',
            description: 'Curso añadido desde la API de SocialShield.',
            level: 'Por definir',
          },
        ];
        console.log(course);
      },
      error: (error: unknown) => console.error('No se pudo guardar el curso.', error),
    });
  }
}
