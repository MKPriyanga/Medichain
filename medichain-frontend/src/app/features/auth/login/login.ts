import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.html'
})
export class LoginComponent {
  auth = inject(AuthService);
  router = inject(Router);

  email = '';
  password = '';
  loading = false;
  error = '';

  submit() {
    this.loading = true;
    this.error = '';
    this.auth.login({ email: this.email, password: this.password }).subscribe({
      next: res => {
        const role = res.role;
        const redirects: Record<string, string> = {
          ADMIN: '/dashboard',
          DOCTOR: '/requests/my',
          NURSE: '/requests/my',
          WAREHOUSE: '/inventory',
          PROCUREMENT: '/products',
          AUDITOR: '/kpis',
          DEPARTMENT_HEAD: '/requests/approve'
        };
        this.router.navigate([redirects[role] || '/dashboard']);
      },
      error: err => {
        if (err.status === 0) {
          this.error = 'Cannot connect to server. Make sure the backend is running on port 8001.';
        } else if (typeof err.error === 'string' && err.error) {
          this.error = err.error;
        } else if (err.error?.message) {
          this.error = err.error.message;
        } else {
          this.error = 'Invalid credentials. Please try again.';
        }
        this.loading = false;
      }
    });
  }
}
