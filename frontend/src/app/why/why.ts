import { Component } from '@angular/core';

interface WhyReason {
  icon: string;
  title: string;
  description: string;
}

@Component({
  selector: 'app-why',
  standalone: true,
  templateUrl: './why.html',
})
export class Why {
  readonly reasons: WhyReason[] = [
    { icon: '🎯', title: 'Enfoque práctico', description: 'Simulaciones reales, no solo teoría. Aprenderás a reaccionar ante situaciones auténticas.' },
    { icon: '📱', title: 'Acceso desde cualquier dispositivo', description: 'Aprende a tu ritmo desde el móvil, tablet o computadora sin restricciones.' },
    { icon: '🏅', title: 'Certificado de finalización', description: 'Al completar cada curso recibirás un certificado digital que acredita tu formación.' },
    { icon: '🌍', title: 'En español', description: 'Contenido íntegramente en español, con ejemplos adaptados a nuestra región.' },
    { icon: '🔄', title: 'Actualización constante', description: 'Los ataques evolucionan. Nuestros cursos se actualizan con las últimas amenazas.' },
    { icon: '💬', title: 'Comunidad activa', description: 'Únete a miles de personas aprendiendo juntas a protegerse en el mundo digital.' },
  ];
}
