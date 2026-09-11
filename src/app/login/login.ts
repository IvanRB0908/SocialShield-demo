import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Footer } from '../footer/footer';
import { Header } from '../header/header';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, Header, Footer],
  templateUrl: './login.html',
})
export class Login {
  readonly loginForm;
  submitted = false;
  notification = '';
  notificationType: 'error' | 'success' = 'error';

  constructor(
    formBuilder: FormBuilder,
    private readonly router: Router,
    @Inject(PLATFORM_ID) private readonly platformId: object,
  ) {
    this.loginForm = formBuilder.nonNullable.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      remember: [false],
    });
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

    const email = this.loginForm.controls.email.value.trim().toLowerCase();
    const registeredEmail = localStorage.getItem('socialshield-profile-email');
    const registeredPassword = localStorage.getItem('socialshield-account-password');

    if (!registeredEmail || email !== registeredEmail) {
      this.notification = 'No existe una cuenta con este correo electrónico.';
      return;
    }

    if (this.loginForm.controls.password.value !== registeredPassword) {
      this.notification = 'La contraseña es incorrecta. Inténtalo de nuevo.';
      return;
    }

    this.notificationType = 'success';
    this.notification = 'Inicio de sesión correcto. Redirigiendo al inicio...';
    localStorage.setItem('socialshield-session-name', localStorage.getItem('socialshield-profile-name') ?? '');
    void this.router.navigateByUrl('/');
  }
}