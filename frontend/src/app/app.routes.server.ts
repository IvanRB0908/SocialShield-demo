import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // Las páginas del perfil se cargan en el navegador, fuera del HTML estático.
  { path: 'perfil', renderMode: RenderMode.Client },
  { path: 'perfil/**', renderMode: RenderMode.Client },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
