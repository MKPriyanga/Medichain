import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportService } from '../../../core/services/report.service';
import { AuditPackage } from '../../../core/models/models';

@Component({
  selector: 'app-audit-packages',
  imports: [CommonModule, FormsModule],
  templateUrl: './audit-packages.html'
})
export class AuditPackagesComponent implements OnInit {
  private svc = inject(ReportService);

  packages: AuditPackage[] = [];
  loading    = false;
  generating = false;
  searching  = false;
  message    = '';
  error      = '';

  scopes = ['ALL', 'REQUESTS', 'DELIVERIES', 'BILLING', 'INVENTORY'];

  /** Generate form */
  form = { start: '', end: '', scope: 'ALL' };

  /** Search form */
  search = { start: '', end: '' };
  searchResults: AuditPackage[] | null = null;

  /** Detail / content preview */
  selectedPackage: AuditPackage | null = null;

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.error = '';
    this.svc.getAuditPackages().subscribe({
      next: res => { this.packages = res.data || []; this.loading = false; },
      error: () => { this.error = 'Failed to load audit packages.'; this.loading = false; }
    });
  }

  generate() {
    if (!this.form.start || !this.form.end) {
      this.error = 'Please select both start and end dates.';
      return;
    }
    this.generating = true;
    this.message = '';
    this.error = '';
    const start = new Date(this.form.start).toISOString();
    const end   = new Date(this.form.end).toISOString();
    this.svc.generateAuditPackage(start, end, this.form.scope).subscribe({
      next: res => {
        this.message = res.message || 'Audit package generated.';
        this.load();
        this.generating = false;
      },
      error: err => {
        this.error = err?.error?.message || 'Failed to generate audit package.';
        this.generating = false;
      }
    });
  }

  searchByPeriod() {
    if (!this.search.start || !this.search.end) {
      this.error = 'Please select both start and end dates for search.';
      return;
    }
    this.searching = true;
    this.error = '';
    const start = new Date(this.search.start).toISOString();
    const end   = new Date(this.search.end).toISOString();
    this.svc.searchAuditPackages(start, end).subscribe({
      next: res => { this.searchResults = res.data || []; this.searching = false; },
      error: () => { this.error = 'Search failed.'; this.searching = false; }
    });
  }

  clearSearch() {
    this.searchResults = null;
    this.search = { start: '', end: '' };
  }

  /** Quick inline preview of contentsJSON */
  preview(pkg: AuditPackage) {
    this.selectedPackage = pkg;
  }

  closePreview() { this.selectedPackage = null; }

  delete(id: number) {
    if (!confirm(`Delete audit package #${id}? This cannot be undone.`)) return;
    this.svc.deleteAuditPackage(id).subscribe({
      next: () => { this.message = `Audit package #${id} deleted.`; this.load(); },
      error: () => { this.error = `Failed to delete audit package #${id}.`; }
    });
  }

  download(pkg: AuditPackage) {
    if (!pkg.packageId) return;
    this.svc.downloadAuditPackage(pkg.packageId).subscribe({
      next: blob => {
        const url  = URL.createObjectURL(blob);
        const a    = document.createElement('a');
        a.href     = url;
        a.download = `audit_package_${pkg.packageId}.json`;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: () => { this.error = `Failed to download audit package #${pkg.packageId}.`; }
    });
  }

  /** Display list — search results when active, otherwise full list */
  get displayPackages(): AuditPackage[] {
    return this.searchResults ?? this.packages;
  }
}