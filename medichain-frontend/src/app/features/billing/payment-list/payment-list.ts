import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BillingService } from '../../../core/services/billing.service';
import { Payment, Invoice } from '../../../core/models/models';

@Component({
  selector: 'app-payment-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-list.html'
})
export class PaymentListComponent implements OnInit {
  private svc = inject(BillingService);
  payments: Payment[] = [];
  invoices: Invoice[] = [];
  loading = false; showForm = false; saving = false; message = ''; error = '';
  methods = ['CREDIT_CARD', 'UPI', 'CASH', 'BANK_TRANSFER'];
  form: Payment = { invoiceId: 0, amount: 0, method: 'CASH' };

  ngOnInit() {
    this.load();
    this.svc.getInvoices().subscribe({ next: d => this.invoices = d.filter(i => i.status === 'UNPAID'), error: () => {} });
  }

  load() {
    this.loading = true;
    this.svc.getPayments().subscribe({
      next: d => { this.payments = d; this.loading = false; },
      error: () => { this.error = 'Failed to load.'; this.loading = false; }
    });
  }

  record() {
    this.saving = true;
    this.svc.recordPayment(this.form).subscribe({
      next: () => {
        this.message = 'Payment recorded.'; this.showForm = false;
        this.form = { invoiceId: 0, amount: 0, method: 'CASH' };
        this.load(); this.saving = false;
      },
      error: err => { this.error = err.error?.message || 'Failed.'; this.saving = false; }
    });
  }

  onInvoiceSelect() {
    const inv = this.invoices.find(i => i.invoiceId === Number(this.form.invoiceId));
    if (inv) this.form.amount = inv.amount;
  }
}
