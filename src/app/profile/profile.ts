import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Footer } from '../footer/footer';
import { Header } from '../header/header';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, Header, Footer],
  templateUrl: './profile.html',
})
export class Profile {
  readonly profileForm;
  saved = false;

  constructor(
    formBuilder: FormBuilder,
    private readonly router: Router,
    @Inject(PLATFORM_ID) private readonly platformId: object,
  ) {
    this.profileForm = formBuilder.nonNullable.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      bio: [''],
    });

    if (isPlatformBrowser(platformId)) {
      this.profileForm.patchValue({
        name: localStorage.getItem('socialshield-profile-name') ?? '',
        email: localStorage.getItem('socialshield-profile-email') ?? '',
        phone: localStorage.getItem('socialshield-profile-phone') ?? '',
        bio: localStorage.getItem('socialshield-profile-bio') ?? '',
      });
    }
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('socialshield-profile-name', this.profileForm.controls.name.value);
      localStorage.setItem('socialshield-profile-email', this.profileForm.controls.email.value);
      localStorage.setItem('socialshield-profile-phone', this.profileForm.controls.phone.value);
      localStorage.setItem('socialshield-profile-bio', this.profileForm.controls.bio.value);
      localStorage.setItem('socialshield-session-name', this.profileForm.controls.name.value);
    }
    this.saved = true;
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('socialshield-session-name');
    }
    void this.router.navigateByUrl('/login');
  }

  deleteAccount(): void {
    if (typeof window !== 'undefined' && window.confirm('¿Seguro que quieres borrar tu cuenta? Esta acción no se puede deshacer.')) {
      this.clearAccountData();
      void this.router.navigateByUrl('/');
    }
  }

  private clearAccountData(): void {
    if (isPlatformBrowser(this.platformId)) {
      ['name', 'email', 'phone', 'bio'].forEach((field) => localStorage.removeItem(`socialshield-profile-${field}`));
      localStorage.removeItem('socialshield-account-password');
      localStorage.removeItem('socialshield-session-name');
    }
  }
}