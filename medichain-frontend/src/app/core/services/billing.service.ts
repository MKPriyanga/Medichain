import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Invoice, Payment } from '../models/models';

@Injectable({ providedIn: 'root' })
export class BillingService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  getInvoices(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.base}/api/invoices`);
  }

  getInvoice(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.base}/api/invoices/${id}`);
  }

  createInvoice(data: Invoice): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.base}/api/invoices`, data);
  }

  getPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.base}/api/payments`);
  }

  recordPayment(data: Payment): Observable<Payment> {
    return this.http.post<Payment>(`${this.base}/api/payments`, data);
  }
}
