import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { AuthService } from '../../../core/services/auth.service';
import { WarehouseService } from '../../../core/services/warehouse.service';
import { SupplierOrder, Supplier, ReceiveOrder, Warehouse } from '../../../core/models/models';

@Component({
  selector: 'app-supplier-order-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './supplier-order-list.html'
})
export class SupplierOrderListComponent implements OnInit {
  private svc   = inject(ProductService);
  private whSvc = inject(WarehouseService);
  auth = inject(AuthService);

  role = '';
  orders: SupplierOrder[] = [];
  suppliers: Supplier[] = [];
  warehouses: Warehouse[] = [];
  loading = false;

  // Create form (PROCUREMENT / ADMIN)
  showForm = false;
  saving = false;
  form: SupplierOrder = { supplierId: 0, productIdsJson: '', quantity: 1, status: 'PLACED' };

  // Receive panel (WAREHOUSE / ADMIN)
  receivingOrderId: number | null = null;
  receiveForm: ReceiveOrder = { warehouseId: 0, quantityReceived: 1 };
  receiving = false;

  message = '';
  error = '';

  ngOnInit() {
    this.role = this.auth.getRole();
    this.load();
  }

  isWarehouse(): boolean {
    return this.role === 'WAREHOUSE' || this.role === 'ADMIN';
  }

  isProcurement(): boolean {
    return this.role === 'PROCUREMENT' || this.role === 'ADMIN';
  }

  load() {
    this.loading = true;
    this.message = '';
    this.error = '';
    this.svc.getOrders().subscribe({
      next: d => { this.orders = d; this.loading = false; },
      error: () => { this.error = 'Failed to load orders.'; this.loading = false; }
    });
    this.svc.getSuppliers().subscribe({ next: d => this.suppliers = d, error: () => {} });
    if (this.isWarehouse()) {
      this.whSvc.getWarehouses().subscribe({ next: d => this.warehouses = d, error: () => {} });
    }
  }

  create() {
    this.saving = true;
    this.svc.createOrder(this.form).subscribe({
      next: () => {
        this.message = 'Order placed successfully.';
        this.showForm = false;
        this.form = { supplierId: 0, productIdsJson: '', quantity: 1, status: 'PLACED' };
        this.load();
        this.saving = false;
      },
      error: (err) => {
        this.error = this.extractError(err, 'Failed to place order.');
        this.saving = false;
      }
    });
  }

  openReceive(orderId: number, quantity: number) {
    this.receivingOrderId = orderId;
    this.receiveForm = { warehouseId: 0, quantityReceived: quantity };
    this.error = '';
  }

  cancelReceive() {
    this.receivingOrderId = null;
    this.receiveForm = { warehouseId: 0, quantityReceived: 1 };
  }

  confirmReceive() {
    if (!this.receiveForm.warehouseId || this.receiveForm.warehouseId === 0) {
      this.error = 'Please select a warehouse.';
      return;
    }
    if (!this.receiveForm.quantityReceived || this.receiveForm.quantityReceived < 1) {
      this.error = 'Quantity received must be at least 1.';
      return;
    }
    this.receiving = true;
    this.svc.receiveOrder(this.receivingOrderId!, this.receiveForm).subscribe({
      next: () => {
        this.message = `Order #${this.receivingOrderId} marked as RECEIVED. Inventory updated.`;
        this.receivingOrderId = null;
        this.receiveForm = { warehouseId: 0, quantityReceived: 1 };
        this.load();
        this.receiving = false;
      },
      error: (err) => {
        this.error = this.extractError(err, 'Failed to receive order.');
        this.receiving = false;
      }
    });
  }

  supplierName(id: number) {
    return this.suppliers.find(s => s.supplierId === id)?.name || `Supplier #${id}`;
  }

  statusClass(status: string): string {
    switch (status) {
      case 'PLACED':    return 'status-PENDING';
      case 'RECEIVED':  return 'status-APPROVED';
      case 'CANCELLED': return 'status-REJECTED';
      default:          return '';
    }
  }

  private extractError(err: any, fallback: string): string {
    if (!err) return fallback;
    if (typeof err.error === 'string') return err.error;
    if (err.error?.message) return err.error.message;
    if (err.message) return err.message;
    return fallback;
  }
}
