import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then(m => m.RegisterComponent)
  },
  {
    canActivate: [authGuard],
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

      { path: 'dashboard',      loadComponent: () => import('./features/dashboard/dashboard').then(m => m.DashboardComponent) },
      { path: 'notifications',  loadComponent: () => import('./features/notifications/notification-list/notification-list').then(m => m.NotificationListComponent) },
      { path: 'users',          canActivate: [roleGuard('ADMIN')],                                              loadComponent: () => import('./features/users/user-list/user-list').then(m => m.UserListComponent) },
      { path: 'audit-logs',     canActivate: [roleGuard('ADMIN')],                                              loadComponent: () => import('./features/users/audit-log-list/audit-log-list').then(m => m.AuditLogListComponent) },
      { path: 'departments',    canActivate: [roleGuard('ADMIN')],                                              loadComponent: () => import('./features/departments/department-list/department-list').then(m => m.DepartmentListComponent) },
      { path: 'warehouse',      canActivate: [roleGuard('ADMIN', 'WAREHOUSE')],                                  loadComponent: () => import('./features/warehouse/warehouse-list/warehouse-list').then(m => m.WarehouseListComponent) },
      { path: 'products',       canActivate: [roleGuard('ADMIN', 'PROCUREMENT')],                               loadComponent: () => import('./features/products/product-list/product-list').then(m => m.ProductListComponent) },
      { path: 'suppliers',      canActivate: [roleGuard('ADMIN', 'PROCUREMENT')],                               loadComponent: () => import('./features/products/supplier-list/supplier-list').then(m => m.SupplierListComponent) },
      { path: 'orders',         canActivate: [roleGuard('ADMIN', 'WAREHOUSE', 'PROCUREMENT')],                  loadComponent: () => import('./features/products/supplier-order-list/supplier-order-list').then(m => m.SupplierOrderListComponent) },
      { path: 'purchase-requests', canActivate: [roleGuard('ADMIN', 'WAREHOUSE', 'PROCUREMENT')],              loadComponent: () => import('./features/products/purchase-request-list/purchase-request-list').then(m => m.PurchaseRequestListComponent) },
      { path: 'requests/my',     canActivate: [roleGuard('DOCTOR', 'NURSE', 'ADMIN')],                          loadComponent: () => import('./features/requests/my-requests/my-requests').then(m => m.MyRequestsComponent) },
      { path: 'requests/all',    canActivate: [roleGuard('ADMIN', 'DEPARTMENT_HEAD', 'WAREHOUSE')],             loadComponent: () => import('./features/requests/all-requests/all-requests').then(m => m.AllRequestsComponent) },
      { path: 'requests/approve',canActivate: [roleGuard('ADMIN', 'DEPARTMENT_HEAD')],                          loadComponent: () => import('./features/requests/approval-queue/approval-queue').then(m => m.ApprovalQueueComponent) },
      { path: 'inventory',      canActivate: [roleGuard('ADMIN', 'WAREHOUSE')],                                 loadComponent: () => import('./features/warehouse/inventory-list/inventory-list').then(m => m.InventoryListComponent) },
      { path: 'stock',          canActivate: [roleGuard('ADMIN', 'WAREHOUSE')],                                 loadComponent: () => import('./features/warehouse/stock-movements/stock-movements').then(m => m.StockMovementsComponent) },
      { path: 'deliveries',     canActivate: [roleGuard('ADMIN', 'WAREHOUSE', 'DEPARTMENT_HEAD', 'AUDITOR')],   loadComponent: () => import('./features/deliveries/delivery-list/delivery-list').then(m => m.DeliveryListComponent) },
      { path: 'invoices',       canActivate: [roleGuard('ADMIN', 'PROCUREMENT', 'AUDITOR')],                    loadComponent: () => import('./features/billing/invoice-list/invoice-list').then(m => m.InvoiceListComponent) },
      { path: 'payments',       canActivate: [roleGuard('ADMIN', 'PROCUREMENT', 'AUDITOR')],                    loadComponent: () => import('./features/billing/payment-list/payment-list').then(m => m.PaymentListComponent) },
      { path: 'kpis',           canActivate: [roleGuard('ADMIN', 'AUDITOR')],                                   loadComponent: () => import('./features/reports/kpi-dashboard/kpi-dashboard').then(m => m.KpiDashboardComponent) },
      { path: 'reports',        canActivate: [roleGuard('ADMIN', 'AUDITOR')],                                   loadComponent: () => import('./features/reports/report-list/report-list').then(m => m.ReportListComponent) },
      { path: 'audit-packages', canActivate: [roleGuard('ADMIN', 'AUDITOR')],                                   loadComponent: () => import('./features/reports/audit-packages/audit-packages').then(m => m.AuditPackagesComponent) },
    ]
  },
  { path: '**', redirectTo: 'login' }
];
