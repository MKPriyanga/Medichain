import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, RegisterRequest } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private base = environment.apiUrl;

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/api/auth/login`, data).pipe(
      tap(res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('userId', String(res.userId));
        localStorage.setItem('role', res.role);
        localStorage.setItem('name', res.name);
      })
    );
  }

  register(data: RegisterRequest): Observable<string> {
    return this.http.post(`${this.base}/api/auth/register`, data, { responseType: 'text' });
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  getToken(): string | null { return localStorage.getItem('token'); }
  getRole(): string { return localStorage.getItem('role') || ''; }
  getUserId(): string { return localStorage.getItem('userId') || ''; }
  getName(): string { return localStorage.getItem('name') || ''; }
  isLoggedIn(): boolean { return !!this.getToken(); }
}
