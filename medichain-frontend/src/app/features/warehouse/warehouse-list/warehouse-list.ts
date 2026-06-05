import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { WarehouseService } from '../../../core/services/warehouse.service';
import { Warehouse } from '../../../core/models/models';

@Component({
  selector: 'app-warehouse-list',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './warehouse-list.html'
})
export class WarehouseListComponent implements OnInit {
  private svc = inject(WarehouseService);
  warehouses: Warehouse[] = [];
  loading = false; showForm = false; saving = false; message = ''; error = '';
  form = { name: '', location: '', capacity: 0 };

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.svc.getWarehouses().subscribe({
      next: d => { this.warehouses = d; this.loading = false; },
      error: () => { this.error = 'Failed to load warehouses.'; this.loading = false; }
    });
  }

  create() {
    this.saving = true;
    this.svc.createWarehouse(this.form).subscribe({
      next: () => {
        this.message = 'Warehouse created.'; this.showForm = false;
        this.form = { name: '', location: '', capacity: 0 };
        this.load(); this.saving = false;
      },
      error: () => { this.error = 'Failed to create.'; this.saving = false; }
    });
  }
}
