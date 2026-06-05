import { Component, OnInit, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { SidebarService } from '../../../core/services/sidebar.service';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html'
})
export class SidebarComponent implements OnInit {
  auth = inject(AuthService);
  sidebarSvc = inject(SidebarService);
  role = '';

  ngOnInit() {
    this.role = this.auth.getRole();
  }

  is(...roles: string[]): boolean {
    return roles.includes(this.role);
  }

  closeOnMobile() {
    this.sidebarSvc.close();
  }
}
