import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { SidebarService } from '../../../core/services/sidebar.service';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.html'
})
export class NavbarComponent implements OnInit, OnDestroy {
  auth = inject(AuthService);
  private notifService = inject(NotificationService);
  sidebarSvc = inject(SidebarService);

  name = '';
  role = '';
  userId = 0;
  unreadCount = 0;
  private pollInterval: ReturnType<typeof setInterval> | null = null;

  ngOnInit() {
    this.name = this.auth.getName();
    this.role = this.auth.getRole();
    this.userId = Number(this.auth.getUserId());
    this.loadUnreadCount();
    this.pollInterval = setInterval(() => this.loadUnreadCount(), 30000);
  }

  ngOnDestroy() {
    if (this.pollInterval !== null) {
      clearInterval(this.pollInterval);
      this.pollInterval = null;
    }
  }

  loadUnreadCount() {
    if (this.userId) {
      this.notifService.getUnreadCount(this.userId).subscribe({
        next: res => this.unreadCount = res.unreadCount,
        error: () => {}
      });
    }
  }

  logout() { this.auth.logout(); }
}
