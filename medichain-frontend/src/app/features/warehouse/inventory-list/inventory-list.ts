import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WarehouseService } from '../../../core/services/warehouse.service';
import { ProductService } from '../../../core/services/product.service';
import { InventoryItem, Warehouse, Product } from '../../../core/models/models';

@Component({
  selector: 'app-inventory-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './inventory-list.html'
})
export class InventoryListComponent implements OnInit {
  private svc = inject(WarehouseService);
  private prodSvc = inject(ProductService);

  items: InventoryItem[] = [];
  warehouses: Warehouse[] = [];
  products: Product[] = [];
  loading = false;

  // Create new inventory item form
  showForm = false;
  saving = false;
  form = { warehouseId: 0, productId: 0, quantity: 1 };

  // Restock (top-up existing item) panel
  restockItem: InventoryItem | null = null;
  restockQty = 1;
  restocking = false;

  filterWarehouse = 0;
  message = '';
  error = '';

  ngOnInit() {
    this.svc.getWarehouses().subscribe({ next: d => this.warehouses = d, error: () => {} });
    this.prodSvc.getProducts().subscribe({ next: d => this.products = d, error: () => {} });
    this.load();
  }

  load() {
    this.loading = true;
    this.message = '';
    this.error = '';
    this.svc.getInventory(this.filterWarehouse || undefined).subscribe({
      next: d => { this.items = d; this.loading = false; },
      error: () => { this.error = 'Failed to load inventory.'; this.loading = false; }
    });
  }

  create() {
    this.saving = true;
    this.svc.createInventory(this.form).subscribe({
      next: () => {
        this.message = 'Inventory item recorded.';
        this.showForm = false;
        this.form = { warehouseId: 0, productId: 0, quantity: 1 };
        this.load();
        this.saving = false;
      },
      error: err => {
        this.error = this.extractError(err, 'Failed to save inventory item.');
        this.saving = false;
      }
    });
  }

  // ── Restock ────────────────────────────────────────────────────────────────
  openRestock(item: InventoryItem) {
    this.restockItem = item;
    this.restockQty = 1;
    this.error = '';
    this.showForm = false; // close create form if open
  }

  cancelRestock() {
    this.restockItem = null;
    this.restockQty = 1;
  }

  confirmRestock() {
    if (!this.restockQty || this.restockQty < 1) {
      this.error = 'Quantity to add must be at least 1.';
      return;
    }
    this.restocking = true;
    const item = this.restockItem!;
    this.svc.restockInventory({
      warehouseId: item.warehouse!.warehouseId!,
      productId: item.productId,
      quantity: this.restockQty
    }).subscribe({
      next: () => {
        this.message = `Restocked ${this.restockQty} unit(s) of "${this.productName(item.productId)}" in ${item.warehouse?.name || 'warehouse'}.`;
        this.restockItem = null;
        this.restockQty = 1;
        this.load();
        this.restocking = false;
      },
      error: err => {
        this.error = this.extractError(err, 'Failed to restock inventory.');
        this.restocking = false;
      }
    });
  }

  productName(id: number) { return this.products.find(p => p.productId === id)?.name || `Product #${id}`; }
  warehouseName(id: number) { return this.warehouses.find(w => w.warehouseId === id)?.name || `Warehouse #${id}`; }
  isLowStock(item: InventoryItem) { return item.quantity < 10; }

  private extractError(err: any, fallback: string): string {
    if (!err) return fallback;
    if (err.status === 0) return 'Cannot reach server.';
    if (typeof err.error === 'string' && err.error) return err.error;
    if (err.error?.message) return err.error.message;
    if (err.message) return err.message;
    return fallback;
  }
}
