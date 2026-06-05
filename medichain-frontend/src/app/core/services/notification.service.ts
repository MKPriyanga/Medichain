import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notification } from '../models/models';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/notifications`;

  getForUser(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.base}/user/${userId}`);
  }

  getUnread(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.base}/user/${userId}/unread`);
  }

  getUnreadCount(userId: number): Observable<{ unreadCount: number }> {
    return this.http.get<{ unreadCount: number }>(`${this.base}/user/${userId}/unread-count`);
  }

  markRead(notificationId: number): Observable<string> {
    return this.http.put(`${this.base}/${notificationId}/read`, {}, { responseType: 'text' });
  }

  markAllRead(userId: number): Observable<{ markedRead: number }> {
    return this.http.put<{ markedRead: number }>(`${this.base}/user/${userId}/read-all`, {});
  }
}
