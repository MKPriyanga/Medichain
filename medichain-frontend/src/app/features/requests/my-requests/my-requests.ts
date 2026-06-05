import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DepartmentRequestService } from '../../../core/services/department-request.service';
import { DepartmentService } from '../../../core/services/department.service';
import { ProductService } from '../../../core/services/product.service';
import { AuthService } from '../../../core/services/auth.service';
import { DepartmentRequest, Department, Product } from '../../../core/models/models';

@Component({
  selector: 'app-my-requests',
  imports: [CommonModule, FormsModule],
  templateUrl: './my-requests.html'
})
export class MyRequestsComponent implements OnInit {
  private reqSvc = inject(DepartmentRequestService);
  private deptSvc = inject(DepartmentService);
  private prodSvc = inject(ProductService);
  private auth = inject(AuthService);

  requests: DepartmentRequest[] = [];
  departments: Department[] = [];
  products: Product[] = [];
  loading = false; showForm = false; saving = false; message = ''; error = '';

  form = { departmentId: 0, productIds: [] as number[], quantity: 1 };
  selectedProductId = 0;

  ngOnInit() {
    this.load();
    this.deptSvc.getAll().subscribe({ next: d => this.departments = d, error: () => {} });
    this.prodSvc.getProducts().subscribe({ next: d => this.products = d, error: () => {} });
  }

  load() {
    this.loading = true;
    this.error = '';
    this.reqSvc.getAll().subscribe({
      next: all => {
        const myId = Number(this.auth.getUserId());
        this.requests = all.filter(r => Number(r.createdByUserId) === myId);
        this.loading = false;
      },
      error: (err) => {
        this.error = err.status === 403
          ? 'Access denied (403) — backend role check failed. Please restart departmentrequest-service.'
          : 'Failed to load requests: ' + (err.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  addProduct() {
    if (this.selectedProductId && !this.form.productIds.includes(this.selectedProductId)) {
      this.form.productIds = [...this.form.productIds, this.selectedProductId];
      this.selectedProductId = 0;
    }
  }

  removeProduct(id: number) {
    this.form.productIds = this.form.productIds.filter(p => p !== id);
  }

  deptName(id: number): string {
    const dept = this.departments.find(d => d.departmentId === id);
    return dept ? dept.name : 'Dept #' + id;
  }

  productName(id: number): string {
    return this.products.find(p => p.productId === id)?.name || `Product #${id}`;
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

  submit() {
    this.saving = true;
    this.reqSvc.create(this.form).subscribe({
      next: () => {
        this.message = 'Request submitted successfully!';
        this.showForm = false;
        this.form = { departmentId: 0, productIds: [], quantity: 1 };
        this.load(); this.saving = false;
      },
      error: err => { this.error = err.error || 'Failed to submit.'; this.saving = false; }
    });
  }
}