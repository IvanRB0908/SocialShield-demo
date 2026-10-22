import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { GoogleAuthApiService } from './google-auth-api.service';
import { Login } from './login';

describe('Login', () => {
  let fixture: ComponentFixture<Login>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideRouter([]),
        { provide: PLATFORM_ID, useValue: 'browser' },
        {
          provide: GoogleAuthApiService,
          useValue: {
            getConfig: () => of({ clientId: '' }),
            login: () => of({ id: '1', name: 'Test', email: 'test@example.com' }),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('shows Google sign-in and indicates when it needs configuration', () => {
    const fallbackButton = fixture.nativeElement.querySelector('.google-signin-fallback');

    expect(fallbackButton).toBeTruthy();
    expect(fallbackButton.disabled).toBe(true);
  });
});
