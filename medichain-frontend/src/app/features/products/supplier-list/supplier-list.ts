import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { Supplier } from '../../../core/models/models';

@Component({
  selector: 'app-supplier-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './supplier-list.html'
})
export class SupplierListComponent implements OnInit {
  private svc = inject(ProductService);
  suppliers: Supplier[] = [];
  loading = false; showForm = false; saving = false; message = ''; error = '';
  form: Supplier = { name: '', contactInfo: '', status: 'ACTIVE' };

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.svc.getSuppliers().subscribe({
      next: d => { this.suppliers = d; this.loading = false; },
      error: () => { this.error = 'Failed to load suppliers.'; this.loading = false; }
    });
  }

  create() {
    this.saving = true;
    this.svc.createSupplier(this.form).subscribe({
      next: () => {
        this.message = 'Supplier added.'; this.showForm = false;
        this.form = { name: '', contactInfo: '', status: 'ACTIVE' };
        this.load(); this.saving = false;
      },
      error: () => { this.error = 'Failed to save.'; this.saving = false; }
    });
  }
}
