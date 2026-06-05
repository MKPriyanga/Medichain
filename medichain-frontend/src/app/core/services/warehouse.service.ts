import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Warehouse, InventoryItem, InventoryCreate, RestockCreate, StockMovement } from '../models/models';

@Injectable({ providedIn: 'root' })
export class WarehouseService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  getWarehouses(): Observable<Warehouse[]> {
    return this.http.get<Warehouse[]>(`${this.base}/api/warehouse/list`);
  }

  createWarehouse(data: { name: string; location: string; capacity: number }): Observable<Warehouse> {
    return this.http.post<Warehouse>(`${this.base}/api/warehouse/create`, data);
  }

  getInventory(warehouseId?: number): Observable<InventoryItem[]> {
    if (warehouseId) {
      return this.http.get<InventoryItem[]>(`${this.base}/api/inventory/list?warehouseId=${warehouseId}`);
    }
    return this.http.get<InventoryItem[]>(`${this.base}/api/inventory/all`);
  }

  createInventory(data: InventoryCreate): Observable<InventoryItem> {
    return this.http.post<InventoryItem>(`${this.base}/api/inventory/create`, data);
  }

  restockInventory(data: RestockCreate): Observable<InventoryItem> {
    return this.http.post<InventoryItem>(`${this.base}/api/inventory/restock`, data);
  }

  getStockMovements(): Observable<StockMovement[]> {
    return this.http.get<StockMovement[]>(`${this.base}/api/stock/movements`);
  }

  createStockMovement(data: { itemId: number; fromWarehouseId: number; toWarehouseId: number; quantity: number }): Observable<StockMovement> {
    return this.http.post<StockMovement>(`${this.base}/api/stock/move`, data);
  }
}
