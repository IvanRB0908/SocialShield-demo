import { Component } from '@angular/core';

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
export class Hero {
  readonly stats: HeroStat[] = [
    { value: '12,000+', label: 'Estudiantes formados' },
    { value: '94%', label: 'Tasa de satisfacción' },
    { value: '3 cursos', label: 'Especializados' },
    { value: 'Certificado', label: 'Reconocido' },
  ];
}
