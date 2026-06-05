import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User, CreateUserRequest, AuditLog } from '../models/models';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/admin`;

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.base}/user-management`);
  }

  getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.base}/user-management/${id}`);
  }

  createUser(data: CreateUserRequest): Observable<string> {
    return this.http.post(`${this.base}/user-management`, data, { responseType: 'text' });
  }

  activateUser(id: number): Observable<string> {
    return this.http.put(`${this.base}/users/${id}/activate`, {}, { responseType: 'text' });
  }

  deactivateUser(id: number): Observable<string> {
    return this.http.put(`${this.base}/users/${id}/deactivate`, {}, { responseType: 'text' });
  }

  assignRole(id: number, role: string): Observable<string> {
    return this.http.put(`${this.base}/users/${id}/role`, { role }, { responseType: 'text' });
  }

  getAuditLogs(): Observable<AuditLog[]> {
  return this.http.get<AuditLog[]>(`${this.base}/audit-logs`);
}
 
getAuditLogsByUser(userId: number): Observable<AuditLog[]> {
  return this.http.get<AuditLog[]>(`${this.base}/audit-logs?userId=${userId}`);
}
}
