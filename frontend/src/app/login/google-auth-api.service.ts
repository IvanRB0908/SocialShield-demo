import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface GoogleAuthConfig {
  clientId: string;
}

export interface AuthenticatedUser {
  id: string;
  name: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class GoogleAuthApiService {
  private readonly apiUrl = 'http://localhost:8080/api/auth/google';

  constructor(private readonly http: HttpClient) {}

  getConfig(): Observable<GoogleAuthConfig> {
    return this.http.get<GoogleAuthConfig>(`${this.apiUrl}/config`);
  }

  login(idToken: string): Observable<AuthenticatedUser> {
    return this.http.post<AuthenticatedUser>(this.apiUrl, { idToken });
  }
}
