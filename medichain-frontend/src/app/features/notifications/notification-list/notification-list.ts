import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthService } from '../../../core/services/auth.service';
import { Notification } from '../../../core/models/models';

@Component({
  selector: 'app-notification-list',
  imports: [CommonModule],
  templateUrl: './notification-list.html'
})
export class NotificationListComponent implements OnInit {
  private svc = inject(NotificationService);
  private auth = inject(AuthService);
  notifications: Notification[] = [];
  loading = false; message = ''; error = '';
  userId = 0;

  ngOnInit() {
    this.userId = Number(this.auth.getUserId());
    this.load();
  }

  load() {
    this.loading = true;
    this.svc.getForUser(this.userId).subscribe({
      next: d => { this.notifications = d; this.loading = false; },
      error: () => { this.error = 'Failed to load notifications.'; this.loading = false; }
    });
  }

  markRead(id: number) {
    this.svc.markRead(id).subscribe({
      next: () => {
        const n = this.notifications.find(n => n.id === id);
        if (n) n.status = 'READ';
      },
      error: () => {}
    });
  }

  markAllRead() {
    this.svc.markAllRead(this.userId).subscribe({
      next: res => {
        this.message = `${res.markedRead} notifications marked as read.`;
        this.load();
      },
      error: () => {}
    });
  }

  unreadCount() { return this.notifications.filter(n => n.status === 'UNREAD').length; }
}
