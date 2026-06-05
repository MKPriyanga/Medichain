import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { KPI, ApiResponse } from '../models/models';

@Injectable({ providedIn: 'root' })
export class KpiService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/kpi`;

  /** GET /api/kpi */
  getAll(): Observable<ApiResponse<KPI[]>> {
    return this.http.get<ApiResponse<KPI[]>>(this.base);
  }

  /** GET /api/kpi/filter?category= */
  filterByCategory(category: string): Observable<ApiResponse<KPI[]>> {
    return this.http.get<ApiResponse<KPI[]>>(`${this.base}/filter`, {
      params: { category }
    });
  }

  /** POST /api/kpi/sync */
  sync(): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.base}/sync`, {});
  }
}