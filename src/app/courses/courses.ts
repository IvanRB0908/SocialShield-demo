import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface Course { icon: string; tag: string; title: string; description: string; duration: string; level: string; lessons: number; }

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './courses.html',
})
export class Courses {
  readonly courses: Course[] = [
    { icon: '✉', tag: 'Más popular', title: 'Detección de Phishing', description: 'Aprende a identificar correos, mensajes y sitios web fraudulentos antes de que sea tarde. Casos reales y simulaciones interactivas.', duration: '4 semanas', level: 'Principiante', lessons: 12 },
    { icon: '◎', tag: 'Nuevo', title: 'Seguridad en Redes Sociales', description: 'Protege tu identidad digital, configura la privacidad correctamente y reconoce perfiles falsos y estafas en plataformas sociales.', duration: '3 semanas', level: 'Intermedio', lessons: 9 },
    { icon: '⌕', tag: 'Esencial', title: 'Llamadas de Fraude', description: 'Identifica vishing, llamadas de suplantación de identidad y técnicas de presión psicológica usadas por estafadores telefónicos.', duration: '2 semanas', level: 'Principiante', lessons: 7 },
    { icon: '▤', tag: 'Avanzado', title: 'Manipulación Psicológica', description: 'Comprende las tácticas de persuasión, urgencia y miedo que usan los atacantes para lograr que compartas información confidencial.', duration: '3 semanas', level: 'Intermedio', lessons: 10 },
    { icon: '▧', tag: 'Nuevo', title: 'Seguridad en Mensajería', description: 'Detecta cadenas de desinformación, enlaces maliciosos y estafas enviadas por WhatsApp, Telegram y otras apps.', duration: '2 semanas', level: 'Principiante', lessons: 8 },
  ];
}
