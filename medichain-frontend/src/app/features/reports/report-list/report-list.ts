import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportService } from '../../../core/services/report.service';
import { Report } from '../../../core/models/models';

@Component({
  selector: 'app-report-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './report-list.html'
})
export class ReportListComponent implements OnInit {
  private svc = inject(ReportService);

  reports: Report[] = [];
  loading = false;
  generating = false;
  message = '';
  error = '';

  scopes = ['INVENTORY', 'REQUESTS', 'DELIVERIES', 'BILLING'];

  /**
   * Allowed types per scope — matches ReportService backend logic exactly:
   *   INVENTORY  → ALL, WAREHOUSE
   *   REQUESTS   → ALL, STATUS, ID
   *   DELIVERIES → ALL, STATUS, ID
   *   BILLING    → ALL, STATUS, ID
   */
  scopeTypes: Record<string, string[]> = {
    INVENTORY:  ['ALL', 'WAREHOUSE'],
    REQUESTS:   ['ALL', 'STATUS', 'ID'],
    DELIVERIES: ['ALL', 'STATUS', 'ID'],
    BILLING:    ['ALL', 'STATUS', 'ID']
  };

  get types(): string[] {
    return this.scopeTypes[this.genScope] ?? ['ALL'];
  }

  genScope  = 'INVENTORY';
  genType   = 'ALL';
  genStatus = '';
  genId: number | null = null;

  /** Parsed metrics from a selected report for inline preview */
  selectedReport: Report | null = null;
  parsedMetrics: Record<string, unknown> | null = null;

  ngOnInit() { this.load(); }

  onScopeChange() {
    // Reset type to ALL when scope changes — avoids sending invalid type combos
    this.genType   = 'ALL';
    this.genStatus = '';
    this.genId     = null;
    this.error     = '';
  }

  load() {
    this.loading = true;
    this.error = '';
    this.svc.getReports().subscribe({
      next: res => { this.reports = res.data || []; this.loading = false; },
      error: () => { this.error = 'Failed to load reports.'; this.loading = false; }
    });
  }

  generate() {
    if (this.genType === 'STATUS' && !this.genStatus.trim()) {
      this.error = 'Status value is required when type is STATUS.';
      return;
    }
    if ((this.genType === 'ID' || this.genType === 'WAREHOUSE') && this.genId == null) {
      this.error = this.genType === 'WAREHOUSE' ? 'Warehouse ID is required.' : 'Record ID is required when type is ID.';
      return;
    }
    this.generating = true;
    this.message = '';
    this.error = '';

    this.svc.generateReport(
      this.genScope,
      this.genType,
      this.genType === 'STATUS' ? this.genStatus : undefined,
      (this.genType === 'ID' || this.genType === 'WAREHOUSE') && this.genId != null ? this.genId : undefined
    ).subscribe({
      next: res => {
        this.message = res.message || `${this.genScope} report generated.`;
        this.load();
        this.generating = false;
      },
      error: err => {
        this.error = err?.error?.message || 'Failed to generate report.';
        this.generating = false;
      }
    });
  }

  /** Open a quick inline metrics preview */
  preview(r: Report) {
    this.selectedReport = r;
    try {
      this.parsedMetrics = r.metricsJSON ? JSON.parse(r.metricsJSON) : null;
    } catch {
      this.parsedMetrics = null;
    }
  }

  closePreview() {
    this.selectedReport = null;
    this.parsedMetrics = null;
  }

  delete(id: number) {
    if (!confirm(`Delete report #${id}?`)) return;
    this.svc.deleteReport(id).subscribe({
      next: () => { this.message = `Report #${id} deleted.`; this.load(); },
      error: () => { this.error = `Failed to delete report #${id}.`; }
    });
  }

  download(r: Report) {
    if (!r.reportId) return;
    this.svc.downloadReport(r.reportId).subscribe({
      next: blob => {
        const url  = URL.createObjectURL(blob);
        const a    = document.createElement('a');
        a.href     = url;
        a.download = r.fileName || `report_${r.reportId}.json`;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: () => { this.error = `Failed to download report #${r.reportId}.`; }
    });
  }

  /** Returns an array of [key, value] pairs from parsedMetrics for template iteration */
  metricEntries(): [string, unknown][] {
    return this.parsedMetrics ? Object.entries(this.parsedMetrics) : [];
  }
}