import { Component } from '@angular/core';

@Component({
  selector: 'app-hero',
  standalone: true,
  templateUrl: './hero.html',
})
export class Hero {
  readonly stats = [['12,000+', 'Estudiantes formados'], ['94%', 'Tasa de satisfacción'], ['3 cursos', 'Especializados'], ['Certificado', 'Reconocido']];
}
