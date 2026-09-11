import { Component } from '@angular/core';

@Component({
  selector: 'app-why',
  standalone: true,
  templateUrl: './why.html',
})
export class Why {
  readonly reasons = [['🎯', 'Enfoque práctico', 'Simulaciones reales, no solo teoría. Aprenderás a reaccionar ante situaciones auténticas.'], ['📱', 'Acceso desde cualquier dispositivo', 'Aprende a tu ritmo desde el móvil, tablet o computadora sin restricciones.'], ['🏅', 'Certificado de finalización', 'Al completar cada curso recibirás un certificado digital que acredita tu formación.'], ['🌍', 'En español', 'Contenido íntegramente en español, con ejemplos adaptados a nuestra región.'], ['🔄', 'Actualización constante', 'Los ataques evolucionan. Nuestros cursos se actualizan con las últimas amenazas.'], ['💬', 'Comunidad activa', 'Únete a miles de personas aprendiendo juntas a protegerse en el mundo digital.']];
}
