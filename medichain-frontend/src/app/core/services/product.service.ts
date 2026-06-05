import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product, Supplier, SupplierOrder, ReceiveOrder, PurchaseRequest, PurchaseRequestCreate, ApprovePurchaseRequest } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.base}/api/products`);
  }

  createProduct(data: Product): Observable<Product> {
    return this.http.post<Product>(`${this.base}/api/products`, data);
  }

  getSuppliers(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(`${this.base}/api/suppliers`);
  }

  createSupplier(data: Supplier): Observable<Supplier> {
    return this.http.post<Supplier>(`${this.base}/api/suppliers`, data);
  }

  getOrders(): Observable<SupplierOrder[]> {
    return this.http.get<SupplierOrder[]>(`${this.base}/api/orders`);
  }

  createOrder(data: SupplierOrder): Observable<SupplierOrder> {
    return this.http.post<SupplierOrder>(`${this.base}/api/orders`, data);
  }

  receiveOrder(orderId: number, body: ReceiveOrder): Observable<SupplierOrder> {
    return this.http.put<SupplierOrder>(`${this.base}/api/orders/${orderId}/receive`, body);
  }

  // ── Purchase Requests ─────────────────────────────────────────────────────

  getPurchaseRequests(): Observable<PurchaseRequest[]> {
    return this.http.get<PurchaseRequest[]>(`${this.base}/api/purchase-requests`);
  }

  createPurchaseRequest(data: PurchaseRequestCreate): Observable<PurchaseRequest> {
    return this.http.post<PurchaseRequest>(`${this.base}/api/purchase-requests`, data);
  }

  approvePurchaseRequest(id: number, body: ApprovePurchaseRequest): Observable<SupplierOrder> {
    return this.http.put<SupplierOrder>(`${this.base}/api/purchase-requests/${id}/approve`, body);
  }

  rejectPurchaseRequest(id: number): Observable<PurchaseRequest> {
    return this.http.put<PurchaseRequest>(`${this.base}/api/purchase-requests/${id}/reject`, {});
  }
}
