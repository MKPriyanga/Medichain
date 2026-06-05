import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DepartmentRequestService } from '../../../core/services/department-request.service';
import { DepartmentService } from '../../../core/services/department.service';
import { ProductService } from '../../../core/services/product.service';
import { DepartmentRequest, Department, Product } from '../../../core/models/models';

@Component({
  selector: 'app-approval-queue',
  imports: [CommonModule],
  templateUrl: './approval-queue.html'
})
export class ApprovalQueueComponent implements OnInit {
  private svc = inject(DepartmentRequestService);
  private deptSvc = inject(DepartmentService);
  private prodSvc = inject(ProductService);

  requests: DepartmentRequest[] = [];
  departments: Department[] = [];
  products: Product[] = [];
  loading = false; message = ''; error = '';
  acting: number | null = null;

  ngOnInit() {
    this.load();
    this.deptSvc.getAll().subscribe({ next: d => this.departments = d, error: () => {} });
    this.prodSvc.getProducts().subscribe({ next: d => this.products = d, error: () => {} });
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

  load() {
    this.loading = true;
    this.svc.getAll().subscribe({
      next: all => {
        this.requests = all.filter(r => r.status === 'PENDING');
        this.loading = false;
      },
      error: () => { this.error = 'Failed to load.'; this.loading = false; }
    });
  }

  approve(id: number) {
    this.acting = id;
    this.svc.approve(id).subscribe({
      next: () => { this.message = `Request #${id} approved.`; this.load(); this.acting = null; },
      error: err => { this.error = err.error || 'Failed.'; this.acting = null; }
    });
  }

  reject(id: number) {
    this.acting = id;
    this.svc.reject(id).subscribe({
      next: () => { this.message = `Request #${id} rejected.`; this.load(); this.acting = null; },
      error: err => { this.error = err.error || 'Failed.'; this.acting = null; }
    });
  }
}
