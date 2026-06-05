import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/models';

@Component({
  selector: 'app-product-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './product-list.html'
})
export class ProductListComponent implements OnInit {
  private svc = inject(ProductService);

  products: Product[] = [];
  loading = false;
  showForm = false;
  saving = false;
  message = '';
  error = '';

  form: Product = { name:'', category:'', unit:'', price:0, status:'ACTIVE' };

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.svc.getProducts().subscribe({
      next: d => { this.products = d; this.loading = false; },
      error: () => { this.error = 'Failed to load products.'; this.loading = false; }
    });
  }

  create() {
    this.saving = true;
    this.svc.createProduct(this.form).subscribe({
      next: () => {
        this.message = 'Product created.';
        this.showForm = false;
        this.form = { name:'', category:'', unit:'', price:0, status:'ACTIVE' };
        this.load();
        this.saving = false;
      },
      error: err => { this.error = err.error?.message || 'Failed.'; this.saving = false; }
    });
  }
}
