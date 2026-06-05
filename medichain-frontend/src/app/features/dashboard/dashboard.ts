import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { DepartmentRequestService } from '../../core/services/department-request.service';
import { DepartmentService } from '../../core/services/department.service';
import { DeliveryService } from '../../core/services/delivery.service';
import { WarehouseService } from '../../core/services/warehouse.service';
import { ProductService } from '../../core/services/product.service';
import { DepartmentRequest, Delivery, Department, Product } from '../../core/models/models';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html'
})
export class DashboardComponent implements OnInit {
  auth = inject(AuthService);
  private reqSvc  = inject(DepartmentRequestService);
  private deptSvc = inject(DepartmentService);
  private delSvc  = inject(DeliveryService);
  private whSvc   = inject(WarehouseService);
  private prodSvc = inject(ProductService);

  role = '';
  name = '';
  stats = { requests: 0, pending: 0, deliveries: 0, products: 0, warehouses: 0, approvedRequests: 0, inTransit: 0 };
  recentRequests: any[] = [];
  approvedRequests: DepartmentRequest[] = [];   // APPROVED + PROCESSING (active warehouse work)
  inTransitDeliveries: Delivery[] = [];
  departments: Department[] = [];
  products: Product[] = [];
  actingRequest: number | null = null;
  requestMessage = '';
  requestError = '';

  ngOnInit() {
    this.role = this.auth.getRole();
    this.name = this.auth.getName();
    this.loadStats();
  }

  loadStats() {
    if (['ADMIN', 'DEPARTMENT_HEAD'].includes(this.role)) {
      this.reqSvc.getAll().subscribe({
        next: data => {
          this.stats.requests = data.length;
          this.stats.pending = data.filter(r => r.status === 'PENDING').length;
          this.recentRequests = data.slice(0, 5);
        },
        error: () => {}
      });
    }
    if (['ADMIN', 'WAREHOUSE'].includes(this.role)) {
      this.whSvc.getWarehouses().subscribe({ next: d => this.stats.warehouses = d.length, error: () => {} });
    }
    if (['ADMIN', 'PROCUREMENT'].includes(this.role)) {
      this.prodSvc.getProducts().subscribe({ next: d => this.stats.products = d.length, error: () => {} });
    }
    if (['ADMIN', 'WAREHOUSE', 'AUDITOR'].includes(this.role)) {
      this.delSvc.getDeliveries().subscribe({ next: d => this.stats.deliveries = d.length, error: () => {} });
    }

    // WAREHOUSE: load APPROVED + PROCESSING requests (active warehouse work)
    if (this.role === 'WAREHOUSE' || this.role === 'ADMIN') {
      this.reqSvc.getAll().subscribe({
        next: data => {
          this.approvedRequests = data.filter(
            r => r.status === 'APPROVED' || r.status === 'PROCESSING'
          );
          this.stats.approvedRequests = this.approvedRequests.length;
        },
        error: () => {}
      });
      this.deptSvc.getAll().subscribe({ next: d => this.departments = d, error: () => {} });
      this.prodSvc.getProducts().subscribe({ next: d => this.products = d, error: () => {} });
    }

    // DEPARTMENT_HEAD: load IN_TRANSIT deliveries awaiting confirmation
    if (this.role === 'DEPARTMENT_HEAD' || this.role === 'ADMIN') {
      this.delSvc.getDeliveries().subscribe({
        next: data => {
          this.inTransitDeliveries = data.filter(d => d.status === 'IN_TRANSIT');
          this.stats.inTransit = this.inTransitDeliveries.length;
        },
        error: () => {}
      });
    }
  }

  // ── Warehouse request actions ───────────────────────────────────────────────

  markProcessing(id: number) {
    this.actingRequest = id;
    this.requestMessage = ''; this.requestError = '';
    this.reqSvc.markProcessing(id).subscribe({
      next: updated => {
        this.requestMessage = `Request #${id} marked as PROCESSING.`;
        this.updateLocal(updated);
        this.actingRequest = null;
      },
      error: err => {
        this.requestError = this.extractError(err, 'Failed to update request.');
        this.actingRequest = null;
      }
    });
  }

  markCompleted(id: number) {
    this.actingRequest = id;
    this.requestMessage = ''; this.requestError = '';
    this.reqSvc.markCompleted(id).subscribe({
      next: () => {
        this.requestMessage = `Request #${id} marked as COMPLETED.`;
        // Remove from active list once completed
        this.approvedRequests = this.approvedRequests.filter(r => r.requestId !== id);
        this.stats.approvedRequests = this.approvedRequests.length;
        this.actingRequest = null;
      },
      error: err => {
        this.requestError = this.extractError(err, 'Failed to update request.');
        this.actingRequest = null;
      }
    });
  }

  private updateLocal(updated: DepartmentRequest) {
    const idx = this.approvedRequests.findIndex(r => r.requestId === updated.requestId);
    if (idx !== -1) this.approvedRequests[idx] = updated;
  }

  private extractError(err: any, fallback: string): string {
    if (!err) return fallback;
    if (err.status === 0) return 'Cannot reach server.';
    if (typeof err.error === 'string' && err.error) return err.error;
    if (err.error?.message) return err.error.message;
    if (err.message) return err.message;
    return fallback;
  }

  deptName(id: number): string {
    const dept = this.departments.find(d => d.departmentId === id);
    return dept ? dept.name : 'Dept #' + id;
  }

  productNames(productIdsJson: string): string[] {
    try {
      const ids: number[] = JSON.parse(productIdsJson);
      return ids.map(id => {
        const p = this.products.find(p => p.productId === id);
        return p ? p.name : 'Product #' + id;
      });
    } catch {
      return [productIdsJson];
    }
  }
}
