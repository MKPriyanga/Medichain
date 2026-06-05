import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KpiService } from '../../../core/services/kpi.service';
import { KPI } from '../../../core/models/models';

@Component({
  selector: 'app-kpi-dashboard',
  imports: [CommonModule],
  templateUrl: './kpi-dashboard.html'
})
export class KpiDashboardComponent implements OnInit {
  private svc = inject(KpiService);

  kpis: KPI[] = [];
  loading  = false;
  syncing  = false;
  message  = '';
  error    = '';

  categories = ['ALL', 'REQUESTS', 'DELIVERY', 'INVENTORY'];
  selectedCategory = 'ALL';

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.error   = '';

    const obs$ = this.selectedCategory === 'ALL'
      ? this.svc.getAll()
      : this.svc.filterByCategory(this.selectedCategory);

    obs$.subscribe({
      next: res => { this.kpis = res.data || []; this.loading = false; },
      error: () => { this.error = 'Failed to load KPIs.'; this.loading = false; }
    });
  }

  onCategoryChange() { this.load(); }

  sync() {
    this.syncing = true;
    this.message = '';
    this.error   = '';
    this.svc.sync().subscribe({
      next: res => {
        this.message = res.message ||
          'KPIs synced — Request Fulfillment Rate, Delivery Completion Rate, and Stock Utilization Rate updated.';
        this.load();
        this.syncing = false;
      },
      error: () => { this.error = 'Sync failed.'; this.syncing = false; }
    });
  }

  progressPct(kpi: KPI): number {
    const cur = parseFloat(kpi.currentValue) || 0;
    const tgt = parseFloat(kpi.target) || 100;
    return Math.min(Math.round((cur / tgt) * 100), 100);
  }

  progressColor(pct: number): string {
    if (pct >= 80) return '#198754';
    if (pct >= 50) return '#ffc107';
    return '#dc3545';
  }

  categoryLabel(cat: string): string {
    const map: Record<string, string> = {
      REQUESTS: 'Requests',
      DELIVERY: 'Delivery',
      INVENTORY: 'Inventory'
    };
    return map[cat] ?? cat;
  }
}