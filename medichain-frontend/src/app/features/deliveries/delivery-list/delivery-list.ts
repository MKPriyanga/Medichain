import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DeliveryService } from '../../../core/services/delivery.service';
import { AuthService } from '../../../core/services/auth.service';
import { DepartmentRequestService } from '../../../core/services/department-request.service';
import { WarehouseService } from '../../../core/services/warehouse.service';
import { Delivery, DepartmentRequest, Warehouse } from '../../../core/models/models';

@Component({
  selector: 'app-delivery-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './delivery-list.html'
})
export class DeliveryListComponent implements OnInit {
  private svc     = inject(DeliveryService);
  private auth    = inject(AuthService);
  private reqSvc  = inject(DepartmentRequestService);
  private whSvc   = inject(WarehouseService);

  deliveries: Delivery[] = [];
  requests: DepartmentRequest[] = [];
  warehouses: Warehouse[] = [];
  loading = false; showForm = false; saving = false; message = ''; error = '';
  role = '';
  form = { requestId: 0, quantity: 1, warehouseId: 0 };

  ngOnInit() {
    this.role = this.auth.getRole();
    this.load();

    if (this.role === 'WAREHOUSE' || this.role === 'ADMIN') {
      this.reqSvc.getAll().subscribe({
        next: d => this.requests = d.filter(r => r.status === 'APPROVED' || r.status === 'PROCESSING'),
        error: () => {}
      });
      this.whSvc.getWarehouses().subscribe({
        next: d => this.warehouses = d,
        error: () => {}
      });
    }
  }

  load() {
    this.loading = true;
    this.svc.getDeliveries().subscribe({
      next: d => { this.deliveries = d; this.loading = false; },
      error: () => { this.error = 'Failed to load deliveries.'; this.loading = false; }
    });
  }

  create() {
    if (!this.form.warehouseId) { this.error = 'Please select a warehouse.'; return; }
    this.saving = true;
    this.error = '';
    this.svc.create(this.form).subscribe({
      next: () => {
        this.message = 'Delivery created and inventory updated.';
        this.showForm = false;
        this.form = { requestId: 0, quantity: 1, warehouseId: 0 };
        this.load();
        this.saving = false;
      },
      error: err => { this.error = this.extractError(err, 'Failed to create delivery.'); this.saving = false; }
    });
  }

  close(id: number) {
    this.error = '';
    this.svc.close(id).subscribe({
      next: () => { this.message = `Delivery #${id} closed.`; this.load(); },
      error: err => { this.error = this.extractError(err, 'Failed to close delivery.'); }
    });
  }

  canClose(d: Delivery): boolean {
    return (this.role === 'DEPARTMENT_HEAD' || this.role === 'WAREHOUSE')
        && d.status === 'IN_TRANSIT';
  }

  private extractError(err: any, fallback: string): string {
    if (!err) return fallback;
    if (err.status === 0) return 'Cannot reach server.';
    if (typeof err.error === 'string' && err.error) return err.error;
    if (err.error?.message) return err.error.message;
    if (err.message) return err.message;
    return fallback;
  }
}
