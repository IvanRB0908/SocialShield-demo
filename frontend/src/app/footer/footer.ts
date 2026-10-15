import { Component } from '@angular/core';

interface FooterLink {
  label: string;
  href: string;
}

@Component({
  selector: 'app-footer',
  standalone: true,
  templateUrl: './footer.html',
})
export class Footer {
  readonly links: FooterLink[] = [
    { label: 'Cursos', href: '#courses' },
    { label: 'Sobre nosotros', href: '#why' },
  ];
}
