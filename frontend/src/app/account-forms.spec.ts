import { PLATFORM_ID } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { Login } from './login/login';
import { GoogleAuthApiService } from './login/google-auth-api.service';
import { Profile } from './profile/profile';
import { Register } from './register/register';

describe('Account forms', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: GoogleAuthApiService, useValue: { getConfig: () => of({ clientId: '' }) } },
      ],
    });
    vi.spyOn(TestBed.inject(Router), 'navigateByUrl').mockResolvedValue(true);
  });

  afterEach(() => {
    vi.useRealTimers();
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('preserves all four profile fields and the session name', () => {
    const profile = TestBed.createComponent(Profile).componentInstance;
    profile.profileForm.setValue({ name: 'Ana García', email: 'ana@example.test', phone: '5551234', bio: 'Mi perfil' });
    profile.saveProfile();
    expect(profile.saved).toBe(true);
    expect(localStorage.getItem('socialshield-profile-name')).toBe('Ana García');
    expect(localStorage.getItem('socialshield-profile-email')).toBe('ana@example.test');
    expect(localStorage.getItem('socialshield-profile-phone')).toBe('5551234');
    expect(localStorage.getItem('socialshield-profile-bio')).toBe('Mi perfil');
    expect(localStorage.getItem('socialshield-session-name')).toBe('Ana García');
  });

  it('keeps registration normalization and the five-second completion', () => {
    vi.useFakeTimers();
    const fixture = TestBed.createComponent(Register);
    const register = fixture.componentInstance;
    register.registerForm.setValue({ name: 'Ana García', email: 'ANA@EXAMPLE.TEST', password: 'password-123', confirmPassword: 'password-123', terms: true });
    register.submit();
    expect(register.saving).toBe(true);
    expect(localStorage.getItem('socialshield-profile-name')).toBeNull();
    vi.advanceTimersByTime(5000);
    expect(register.saved).toBe(true);
    expect(register.saving).toBe(false);
    expect(localStorage.getItem('socialshield-profile-email')).toBe('ana@example.test');
    expect(localStorage.getItem('socialshield-account-password')).toBe('password-123');
    expect(TestBed.inject(Router).navigateByUrl).toHaveBeenCalledWith('/');
    fixture.destroy();
  });

  it('preserves local login and rejects an incorrect password', () => {
    localStorage.setItem('socialshield-profile-name', 'Ana García');
    localStorage.setItem('socialshield-profile-email', 'ana@example.test');
    localStorage.setItem('socialshield-account-password', 'password-123');
    const login = TestBed.createComponent(Login).componentInstance;
    login.loginForm.setValue({ email: 'ANA@EXAMPLE.TEST', password: 'incorrect-password', remember: false });
    login.submit();
    expect(login.notification).toContain('contraseña es incorrecta');
    expect(TestBed.inject(Router).navigateByUrl).not.toHaveBeenCalled();
    login.loginForm.controls.password.setValue('password-123');
    login.submit();
    expect(localStorage.getItem('socialshield-session-name')).toBe('Ana García');
    expect(TestBed.inject(Router).navigateByUrl).toHaveBeenCalledWith('/');
  });
});
