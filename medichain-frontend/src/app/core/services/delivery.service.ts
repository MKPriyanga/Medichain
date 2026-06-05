import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Delivery, DeliveryCreate, ProofOfReceipt, ProofOfReceiptCreate } from '../models/models';

@Injectable({ providedIn: 'root' })
export class DeliveryService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  getDeliveries(requestId?: number): Observable<Delivery[]> {
    const url = requestId
      ? `${this.base}/api/deliveries?requestId=${requestId}`
      : `${this.base}/api/deliveries`;
    return this.http.get<Delivery[]>(url);
  }

  getById(id: number): Observable<Delivery> {
    return this.http.get<Delivery>(`${this.base}/api/deliveries/${id}`);
  }

  create(data: DeliveryCreate): Observable<Delivery> {
    return this.http.post<Delivery>(`${this.base}/api/deliveries`, data);
  }

  close(id: number): Observable<Delivery> {
    return this.http.put<Delivery>(`${this.base}/api/deliveries/${id}/close`, {});
  }

  createProof(data: ProofOfReceiptCreate): Observable<ProofOfReceipt> {
    return this.http.post<ProofOfReceipt>(`${this.base}/api/proof-of-receipt`, data);
  }

  getProofByDelivery(deliveryId: number): Observable<ProofOfReceipt[]> {
    return this.http.get<ProofOfReceipt[]>(`${this.base}/api/proof-of-receipt/delivery/${deliveryId}`);
  }
}
