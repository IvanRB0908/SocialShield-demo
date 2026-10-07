import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, signal, PLATFORM_ID } from '@angular/core';
import { RouterLink } from '@angular/router';

interface HeaderNavItem {
  label: string;
  href: string;
  isPrimary?: boolean;
}

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './header.html',
})
export class Header {
  readonly menuOpen = signal(false);
  readonly profileName = signal<string | null>(null);
  readonly navItems: HeaderNavItem[] = [
    { label: 'Cursos', href: '#courses' },
    { label: '¿Por qué nosotros?', href: '#why' },
    { label: 'Recursos', href: '#courses' },
  ];

  constructor(@Inject(PLATFORM_ID) platformId: object) {
    if (isPlatformBrowser(platformId)) {
      this.profileName.set(localStorage.getItem('socialshield-session-name'));
    }
  }

  toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }

  closeMenu(): void {
    this.menuOpen.set(false);
  }
}
