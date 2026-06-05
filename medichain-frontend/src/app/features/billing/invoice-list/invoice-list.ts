import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BillingService } from '../../../core/services/billing.service';
import { Invoice } from '../../../core/models/models';

@Component({
  selector: 'app-invoice-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './invoice-list.html'
})
export class InvoiceListComponent implements OnInit {
  private svc = inject(BillingService);
  invoices: Invoice[] = [];
  filtered: Invoice[] = [];
  loading = false; showForm = false; saving = false; message = ''; error = '';
  filterStatus = '';
  form: Invoice = { departmentId: 0, amount: 0, status: 'UNPAID' };

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.svc.getInvoices().subscribe({
      next: d => { this.invoices = d; this.applyFilter(); this.loading = false; },
      error: () => { this.error = 'Failed to load invoices.'; this.loading = false; }
    });
  }

  applyFilter() {
    this.filtered = this.filterStatus
      ? this.invoices.filter(i => i.status === this.filterStatus)
      : [...this.invoices];
  }

  create() {
    this.saving = true;
    this.svc.createInvoice(this.form).subscribe({
      next: () => {
        this.message = 'Invoice created.'; this.showForm = false;
        this.form = { departmentId: 0, amount: 0, status: 'UNPAID' };
        this.load(); this.saving = false;
      },
      error: err => { this.error = err.error?.message || 'Failed.'; this.saving = false; }
    });
  }

  totalUnpaid() { return this.invoices.filter(i => i.status === 'UNPAID').reduce((s, i) => s + i.amount, 0); }
  totalPaid() { return this.invoices.filter(i => i.status === 'PAID').reduce((s, i) => s + i.amount, 0); }
}
