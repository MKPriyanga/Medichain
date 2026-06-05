import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DepartmentRequest, DepartmentRequestCreate } from '../models/models';

@Injectable({ providedIn: 'root' })
export class DepartmentRequestService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/department-requests`;

  create(data: DepartmentRequestCreate): Observable<DepartmentRequest> {
    return this.http.post<DepartmentRequest>(this.base, data);
  }

  getAll(): Observable<DepartmentRequest[]> {
    return this.http.get<DepartmentRequest[]>(this.base);
  }

  getById(id: number): Observable<DepartmentRequest> {
    return this.http.get<DepartmentRequest>(`${this.base}/${id}`);
  }

  approve(id: number): Observable<DepartmentRequest> {
    return this.http.put<DepartmentRequest>(`${this.base}/${id}/approve`, {});
  }

  reject(id: number): Observable<DepartmentRequest> {
    return this.http.put<DepartmentRequest>(`${this.base}/${id}/reject`, {});
  }

  markProcessing(id: number): Observable<DepartmentRequest> {
    return this.http.put<DepartmentRequest>(`${this.base}/${id}/process`, {});
  }

  markCompleted(id: number): Observable<DepartmentRequest> {
    return this.http.put<DepartmentRequest>(`${this.base}/${id}/complete`, {});
  }
}
