import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WarehouseService } from '../../../core/services/warehouse.service';
import { StockMovement, Warehouse, InventoryItem } from '../../../core/models/models';

@Component({
  selector: 'app-stock-movements',
  imports: [CommonModule, FormsModule],
  templateUrl: './stock-movements.html'
})
export class StockMovementsComponent implements OnInit {
  private svc = inject(WarehouseService);
  movements: StockMovement[] = [];
  warehouses: Warehouse[] = [];
  items: InventoryItem[] = [];
  loading = false; showForm = false; saving = false; message = ''; error = '';
  form = { itemId: 0, fromWarehouseId: 0, toWarehouseId: 0, quantity: 1 };

  ngOnInit() {
    this.load();
    this.svc.getWarehouses().subscribe({ next: d => this.warehouses = d, error: () => {} });
    this.svc.getInventory().subscribe({ next: d => this.items = d, error: () => {} });
  }

  load() {
    this.loading = true;
    this.svc.getStockMovements().subscribe({
      next: d => { this.movements = d; this.loading = false; },
      error: () => { this.error = 'Failed to load movements.'; this.loading = false; }
    });
  }

  create() {
    this.saving = true;
    this.svc.createStockMovement(this.form).subscribe({
      next: () => {
        this.message = 'Stock movement recorded.'; this.showForm = false;
        this.form = { itemId: 0, fromWarehouseId: 0, toWarehouseId: 0, quantity: 1 };
        this.load(); this.saving = false;
      },
      error: () => { this.error = 'Failed to record movement.'; this.saving = false; }
    });
  }

  warehouseName(id: number) { return this.warehouses.find(w => w.warehouseId === id)?.name || `#${id}`; }
}
