import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.html'
})
export class RegisterComponent {
  auth = inject(AuthService);
  router = inject(Router);

  name = '';
  email = '';
  password = '';
  phone = '';
  loading = false;
  error = '';
  success = '';

  submit() {
    this.loading = true;
    this.error = '';
    this.auth.register({ name: this.name, email: this.email, password: this.password, phone: this.phone }).subscribe({
      next: () => {
        this.success = 'Registration successful! Await admin approval before logging in.';
        this.loading = false;
      },
      error: err => {
        if (err.status === 0) {
          this.error = 'Cannot connect to server. Make sure the backend is running on port 8001.';
        } else if (typeof err.error === 'string' && err.error) {
          this.error = err.error;
        } else if (err.error?.message) {
          this.error = err.error.message;
        } else {
          this.error = 'Registration failed. Please try again.';
        }
        this.loading = false;
      }
    });
  }
}
