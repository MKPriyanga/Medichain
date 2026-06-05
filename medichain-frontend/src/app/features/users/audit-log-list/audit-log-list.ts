import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { AuditLog } from '../../../core/models/models';
 
@Component({
  selector: 'app-audit-log-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './audit-log-list.html',
  styleUrl: './audit-log-list.css'
})
export class AuditLogListComponent implements OnInit {
  private svc = inject(UserService);
  private auth = inject(AuthService);
 
  logs: AuditLog[] = [];
  filteredLogs: AuditLog[] = [];
  loading = false;
  error = '';
 
  filterUserId = '';
  filterAction = '';
 
  canAccess = ['ADMIN', 'AUDITOR'].includes(this.auth.getRole());
 
  ngOnInit() {
    if (this.canAccess) {
      this.load();
    }
  }
 
  load() {
    this.loading = true;
    this.error = '';
    this.svc.getAuditLogs().subscribe({
      next: data => {
        this.logs = data;
        this.filteredLogs = [...data];  // spread copy
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load audit logs.';
        this.loading = false;
      }
    });
  }
 
  applyFilter() {
    this.filteredLogs = this.logs.filter(log => {
      const matchUser = this.filterUserId
        ? String(log.userId).includes(this.filterUserId)
        : true;
      const matchAction = this.filterAction
        ? log.action.toLowerCase().includes(this.filterAction.toLowerCase())
        : true;
      return matchUser && matchAction;
    });
  }
 
  clearFilter() {
    this.filterUserId = '';
    this.filterAction = '';
    this.filteredLogs = [...this.logs];  // ✅ reset to full list
  }
}
 