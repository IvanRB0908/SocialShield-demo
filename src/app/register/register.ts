import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnDestroy, PLATFORM_ID } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Footer } from '../footer/footer';
import { Header } from '../header/header';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, Header, Footer],
  templateUrl: './register.html',
})
export class Register implements OnDestroy {
  readonly registerForm;
  submitted = false;
  saving = false;
  saved = false;
  secondsRemaining = 5;
  private saveTimer?: ReturnType<typeof setInterval>;

  constructor(
    formBuilder: FormBuilder,
    private readonly router: Router,
    @Inject(PLATFORM_ID) private readonly platformId: object,
  ) {
    this.registerForm = formBuilder.nonNullable.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
      terms: [false, Validators.requiredTrue],
    });
  }

  submit(): void {
    this.submitted = true;
    if (this.registerForm.invalid || this.passwordsDoNotMatch()) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.saved = false;
    this.secondsRemaining = 5;
    this.saveTimer = setInterval(() => {
      this.secondsRemaining -= 1;
      if (this.secondsRemaining === 0) {
        this.saving = false;
        this.saved = true;
        this.clearSaveTimer();
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('socialshield-profile-name', this.registerForm.controls.name.value);
          localStorage.setItem('socialshield-profile-email', this.registerForm.controls.email.value.trim().toLowerCase());
          localStorage.setItem('socialshield-account-password', this.registerForm.controls.password.value);
          localStorage.setItem('socialshield-session-name', this.registerForm.controls.name.value);
        }
        void this.router.navigateByUrl('/');
      }
    }, 1000);
  }

  passwordsDoNotMatch(): boolean {
    return this.registerForm.controls.confirmPassword.value !== this.registerForm.controls.password.value;
  }

  ngOnDestroy(): void {
    this.clearSaveTimer();
  }

  private clearSaveTimer(): void {
    if (this.saveTimer) {
      clearInterval(this.saveTimer);
      this.saveTimer = undefined;
    }
  }
}