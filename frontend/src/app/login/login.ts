import { isPlatformBrowser } from '@angular/common';
import { AfterViewInit, ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { GoogleAuthApiService } from './google-auth-api.service';
import { Footer } from '../footer/footer';
import { Header } from '../header/header';

interface GoogleCredentialResponse {
  credential: string;
}

interface GoogleIdentityServices {
  accounts: {
    id: {
      initialize(options: {
        client_id: string;
        callback: (response: GoogleCredentialResponse) => void;
      }): void;
      renderButton(
        parent: HTMLElement,
        options: {
          theme: 'outline';
          size: 'large';
          shape: 'rectangular';
          text: 'continue_with';
          width: number;
        },
      ): void;
    };
  };
}

declare global {
  interface Window {
    google?: GoogleIdentityServices;
  }
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, Header, Footer],
  templateUrl: './login.html',
})
export class Login implements AfterViewInit {
  readonly loginForm;
  submitted = false;
  notification = '';
  notificationType: 'error' | 'success' = 'error';
  googleStatus = '';
  googleReady = false;
  private googleClientId = '';

  constructor(
    formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly googleAuthApi: GoogleAuthApiService,
    private readonly changeDetector: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private readonly platformId: object,
  ) {
    this.loginForm = formBuilder.nonNullable.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      remember: [false],
    });
  }

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.googleAuthApi.getConfig().subscribe({
      next: ({ clientId }) => {
        if (!clientId) {
          this.setGoogleStatus('El inicio de sesión con Google aún no está configurado.');
          return;
        }
        this.googleClientId = clientId;
        this.loadGoogleIdentityServices();
      },
      error: () => {
        this.setGoogleStatus('No se pudo conectar con el servicio de inicio de sesión con Google.');
      },
    });
  }

  private loadGoogleIdentityServices(): void {
    if (window.google) {
      this.renderGoogleButton();
      return;
    }

    const existingScript = document.getElementById('google-identity-services');
    if (existingScript) {
      existingScript.addEventListener('load', () => this.renderGoogleButton(), { once: true });
      existingScript.addEventListener(
        'error',
        () => this.setGoogleStatus('No se pudo cargar el botón de Google.'),
        { once: true },
      );
      return;
    }

    const script = document.createElement('script');
    script.id = 'google-identity-services';
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    script.onload = () => {
      this.renderGoogleButton();
    };
    script.onerror = () => {
      this.setGoogleStatus('No se pudo cargar el botón de Google.');
    };
    document.head.appendChild(script);
  }

  private renderGoogleButton(): void {
    const google = window.google;
    const buttonContainer = document.getElementById('google-signin-button');
    if (!google || !buttonContainer) {
      this.setGoogleStatus('No se pudo mostrar el botón de Google.');
      return;
    }

    google.accounts.id.initialize({
      client_id: this.googleClientId,
      callback: (response) => this.loginWithGoogle(response.credential),
    });
    google.accounts.id.renderButton(buttonContainer, {
      theme: 'outline',
      size: 'large',
      shape: 'rectangular',
      text: 'continue_with',
      width: Math.min(buttonContainer.clientWidth, 400),
    });
    this.googleReady = true;
    this.setGoogleStatus('');
  }

  private loginWithGoogle(idToken: string): void {
    this.setGoogleStatus('Iniciando sesión...');
    this.googleAuthApi.login(idToken).subscribe({
      next: (user) => {
        localStorage.setItem('socialshield-profile-name', user.name);
        localStorage.setItem('socialshield-profile-email', user.email);
        localStorage.setItem('socialshield-session-name', user.name);
        void this.router.navigateByUrl('/');
      },
      error: () => {
        this.setGoogleStatus('No se pudo iniciar sesión con Google. Inténtalo de nuevo.');
      },
    });
  }

  private setGoogleStatus(status: string): void {
    this.googleStatus = status;
    this.changeDetector.markForCheck();
  }

  submit(): void {
    this.submitted = true;
    this.notification = '';
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      this.notification = 'Revisa los datos indicados antes de continuar.';
      return;
    }

    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    const values = this.loginForm.getRawValue();
    values.email = values.email.trim().toLowerCase();

    const registeredEmail = localStorage.getItem('socialshield-profile-email');
    const registeredPassword = localStorage.getItem('socialshield-account-password');

    if (!registeredEmail || values.email !== registeredEmail) {
      this.notification = 'No existe una cuenta con este correo electrónico.';
      return;
    }

    if (values.password !== registeredPassword) {
      this.notification = 'La contraseña es incorrecta. Inténtalo de nuevo.';
      return;
    }

    this.notificationType = 'success';
    this.notification = 'Inicio de sesión correcto. Redirigiendo al inicio...';
    localStorage.setItem('socialshield-session-name', localStorage.getItem('socialshield-profile-name') ?? '');
    void this.router.navigateByUrl('/');
  }
}