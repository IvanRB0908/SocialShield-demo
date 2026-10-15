import { Component, OnInit } from '@angular/core';
import { CourseApiService } from '../services/course-api.service';

// Define la estructura de cada estadística visible en el hero banner.
interface HeroStat {
  value: string;
  label: string;
}

@Component({
  selector: 'app-hero',
  standalone: true,
  templateUrl: './hero.html',
})
export class Hero implements OnInit {
  stats: HeroStat[] = [];
  errorMessage = '';

  constructor(private readonly courseApi: CourseApiService) {}

  ngOnInit(): void {
    this.courseApi.getHomePage().subscribe({
      next: (homePage) => (this.stats = homePage.stats),
      error: () => (this.errorMessage = 'No se pudieron cargar las estadísticas.'),
    });
  }
}
