import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DepartmentRequestService } from '../../../core/services/department-request.service';
import { DepartmentService } from '../../../core/services/department.service';
import { ProductService } from '../../../core/services/product.service';
import { DepartmentRequest, Department, Product } from '../../../core/models/models';

@Component({
  selector: 'app-all-requests',
  imports: [CommonModule, FormsModule],
  templateUrl: './all-requests.html'
})
export class AllRequestsComponent implements OnInit {
  private svc = inject(DepartmentRequestService);
  private deptSvc = inject(DepartmentService);
  private prodSvc = inject(ProductService);

  all: DepartmentRequest[] = [];
  filtered: DepartmentRequest[] = [];
  departments: Department[] = [];
  products: Product[] = [];
  loading = false; error = '';
  filterStatus = '';
  statuses = ['', 'PENDING', 'APPROVED', 'REJECTED', 'IN_DELIVERY', 'DELIVERED', 'CLOSED'];

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
      next: d => { this.all = d; this.applyFilter(); this.loading = false; },
      error: () => { this.error = 'Failed to load.'; this.loading = false; }
    });
  }

  applyFilter() {
    this.filtered = this.filterStatus ? this.all.filter(r => r.status === this.filterStatus) : [...this.all];
  }

  countByStatus(status: string): number {
    return this.all.filter(r => r.status === status).length;
  }
}
