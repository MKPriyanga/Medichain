import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { User, CreateUserRequest } from '../../../core/models/models';

@Component({
  selector: 'app-user-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './user-list.html'
})
export class UserListComponent implements OnInit {
  private svc = inject(UserService);

  users: User[] = [];
  loading = false;
  showForm = false;
  saving = false;
  message = '';
  error = '';

  roles = ['ADMIN','DOCTOR','NURSE','WAREHOUSE','PROCUREMENT','AUDITOR','DEPARTMENT_HEAD'];

  form: CreateUserRequest = { name:'', email:'', password:'', phone:'', role:'DOCTOR' };

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.svc.getAllUsers().subscribe({
      next: d => { this.users = d; this.loading = false; },
      error: () => { this.error = 'Failed to load users.'; this.loading = false; }
    });
  }

  create() {
    this.saving = true;
    this.svc.createUser(this.form).subscribe({
      next: () => {
        this.message = 'User created successfully.';
        this.showForm = false;
        this.form = { name:'', email:'', password:'', phone:'', role:'DOCTOR' };
        this.load();
        this.saving = false;
      },
      error: err => { this.error = err.error || 'Failed to create user.'; this.saving = false; }
    });
  }

  activate(id: number) {
    this.svc.activateUser(id).subscribe({ next: () => this.load(), error: () => {} });
  }

  deactivate(id: number) {
    this.svc.deactivateUser(id).subscribe({ next: () => this.load(), error: () => {} });
  }

  assignRole(id: number, role: string) {
    this.svc.assignRole(id, role).subscribe({ next: () => this.load(), error: () => {} });
  }
}
