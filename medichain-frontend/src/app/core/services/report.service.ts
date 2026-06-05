import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Report, AuditPackage, ApiResponse } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  // ── Reports ─────────────────────────────────────────────────────────────────

  /** GET /api/reports — ADMIN, AUDITOR */
  getReports(): Observable<ApiResponse<Report[]>> {
    return this.http.get<ApiResponse<Report[]>>(`${this.base}/api/reports`);
  }

  /** GET /api/reports/{id} — ADMIN, AUDITOR */
  getReportById(id: number): Observable<ApiResponse<Report>> {
    return this.http.get<ApiResponse<Report>>(`${this.base}/api/reports/${id}`);
  }

  /**
   * POST /api/reports/generate
   * Matches ReportController.generate() signature exactly:
   *   scope  — REQUESTS | DELIVERIES | BILLING | INVENTORY  (required)
   *   type   — ALL | STATUS | ID                             (default ALL)
   *   status — filter value when type=STATUS                 (optional)
   *   id     — integer record id when type=ID                (optional)
   */
  generateReport(
    scope: string,
    type: string = 'ALL',
    status?: string,
    id?: number
  ): Observable<ApiResponse<Report>> {
    let url = `${this.base}/api/reports/generate?scope=${scope}&type=${type}`;
    if (status) url += `&status=${status}`;
    if (id != null) url += `&id=${id}`;
    return this.http.post<ApiResponse<Report>>(url, {});
  }

  /** DELETE /api/reports/{id} — ADMIN only */
  deleteReport(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.base}/api/reports/${id}`);
  }

  /** GET /api/reports/{id}/download — returns file as Blob with auth header */
  downloadReport(id: number): Observable<Blob> {
    return this.http.get(`${this.base}/api/reports/${id}/download`, {
      responseType: 'blob'
    });
  }

  /** GET /api/audit-packages/{id}/download — returns file as Blob with auth header */
  downloadAuditPackage(id: number): Observable<Blob> {
    return this.http.get(`${this.base}/api/audit-packages/${id}/download`, {
      responseType: 'blob'
    });
  }

  // ── Audit Packages ───────────────────────────────────────────────────────────

  /** GET /api/audit-packages — ADMIN, AUDITOR */
  getAuditPackages(): Observable<ApiResponse<AuditPackage[]>> {
    return this.http.get<ApiResponse<AuditPackage[]>>(`${this.base}/api/audit-packages`);
  }

  /** GET /api/audit-packages/{id} — ADMIN, AUDITOR */
  getAuditPackageById(id: number): Observable<ApiResponse<AuditPackage>> {
    return this.http.get<ApiResponse<AuditPackage>>(`${this.base}/api/audit-packages/${id}`);
  }

  /**
   * GET /api/audit-packages/search?start=&end=
   * Search packages by period — ADMIN, AUDITOR.
   * Dates must be ISO-8601 LocalDateTime strings (e.g. 2025-01-01T00:00:00).
   */
  searchAuditPackages(start: string, end: string): Observable<ApiResponse<AuditPackage[]>> {
    return this.http.get<ApiResponse<AuditPackage[]>>(
      `${this.base}/api/audit-packages/search`, {
        params: { start, end }
      }
    );
  }

  /**
   * POST /api/audit-packages/generate
   * Generates a full compliance bundle for the given period + scope.
   * scope: ALL | REQUESTS | DELIVERIES | BILLING | INVENTORY
   */
  generateAuditPackage(
    start: string,
    end: string,
    scope: string = 'ALL'
  ): Observable<ApiResponse<AuditPackage>> {
    return this.http.post<ApiResponse<AuditPackage>>(
      `${this.base}/api/audit-packages/generate?start=${start}&end=${end}&scope=${scope}`, {}
    );
  }

  /** PUT /api/audit-packages/{id} — ADMIN only */
  updateAuditPackage(id: number, data: AuditPackage): Observable<ApiResponse<AuditPackage>> {
    return this.http.put<ApiResponse<AuditPackage>>(
      `${this.base}/api/audit-packages/${id}`, data
    );
  }

  /** DELETE /api/audit-packages/{id} — ADMIN only */
  deleteAuditPackage(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(
      `${this.base}/api/audit-packages/${id}`
    );
  }
}