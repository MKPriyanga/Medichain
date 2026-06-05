import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { AuthService } from '../../../core/services/auth.service';
import { PurchaseRequest, PurchaseRequestCreate, Supplier, Product } from '../../../core/models/models';

@Component({
  selector: 'app-purchase-request-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './purchase-request-list.html'
})
export class PurchaseRequestListComponent implements OnInit {
  private svc = inject(ProductService);
  auth = inject(AuthService);

  role = '';
  userId = 0;

  requests: PurchaseRequest[] = [];
  suppliers: Supplier[] = [];
  products: Product[] = [];
  loading = false;

  // Create form (WAREHOUSE)
  showCreateForm = false;
  saving = false;
  createForm: PurchaseRequestCreate = { productId: 0, quantity: 1 };

  // Approve panel (PROCUREMENT / ADMIN)
  approvingId: number | null = null;
  selectedSupplierId = 0;
  approving = false;

  message = '';
  error = '';

  ngOnInit() {
    this.role = this.auth.getRole();
    this.userId = Number(this.auth.getUserId());
    this.load();
    this.svc.getProducts().subscribe({ next: d => this.products = d, error: () => {} });
    if (this.isProcurement()) {
      this.svc.getSuppliers().subscribe({ next: d => this.suppliers = d, error: () => {} });
    }
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
    this.svc.getPurchaseRequests().subscribe({
      next: d => { this.requests = d; this.loading = false; },
      error: () => { this.error = 'Failed to load purchase requests.'; this.loading = false; }
    });
  }

  submitCreate() {
    if (!this.createForm.productId || this.createForm.quantity < 1) {
      this.error = 'Please enter a valid product ID and quantity.';
      return;
    }
    this.saving = true;
    this.svc.createPurchaseRequest(this.createForm).subscribe({
      next: () => {
        this.message = 'Purchase request submitted successfully.';
        this.showCreateForm = false;
        this.createForm = { productId: 0, quantity: 1 };
        this.load();
        this.saving = false;
      },
      error: (err) => {
        this.error = this.extractError(err, 'Failed to submit purchase request.');
        this.saving = false;
      }
    });
  }

  openApprove(id: number) {
    this.approvingId = id;
    this.selectedSupplierId = 0;
    this.error = '';
  }

  cancelApprove() {
    this.approvingId = null;
    this.selectedSupplierId = 0;
  }

  confirmApprove() {
    if (!this.selectedSupplierId || this.selectedSupplierId === 0) {
      this.error = 'Please select a supplier before approving.';
      return;
    }
    this.approving = true;
    this.svc.approvePurchaseRequest(this.approvingId!, { supplierId: this.selectedSupplierId }).subscribe({
      next: () => {
        this.message = `Purchase request #${this.approvingId} approved. Supplier order created.`;
        this.approvingId = null;
        this.selectedSupplierId = 0;
        this.load();
        this.approving = false;
      },
      error: (err) => {
        this.error = this.extractError(err, 'Failed to approve request.');
        this.approving = false;
      }
    });
  }

  reject(id: number) {
    if (!confirm(`Reject purchase request #${id}?`)) return;
    this.svc.rejectPurchaseRequest(id).subscribe({
      next: () => { this.message = `Purchase request #${id} rejected.`; this.load(); },
      error: (err) => { this.error = this.extractError(err, 'Failed to reject request.'); }
    });
  }

  // Safely extract a readable string from any Angular HttpErrorResponse.
  // Spring Boot wraps errors as JSON objects; err.error is that parsed object.
  private extractError(err: any, fallback: string): string {
    if (!err) return fallback;
    // Gateway / Spring Boot JSON body: { message: "..." }
    if (typeof err.error === 'string') return err.error;
    if (err.error?.message) return err.error.message;
    // HttpErrorResponse-level message
    if (err.message) return err.message;
    return fallback;
  }

  productName(id: number): string {
    return this.products.find(p => p.productId === id)?.name || `Product #${id}`;
  }

  productCategory(id: number): string {
    return this.products.find(p => p.productId === id)?.category || '';
  }

  supplierName(id: number): string {
    return this.suppliers.find(s => s.supplierId === id)?.name || `Supplier #${id}`;
  }

  statusClass(status: string): string {
    switch (status) {
      case 'PENDING':  return 'status-PENDING';
      case 'ORDERED':  return 'status-APPROVED';
      case 'REJECTED': return 'status-REJECTED';
      default:         return '';
    }
  }
}
