import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const roleGuard = (...allowedRoles: string[]): CanActivateFn => () => {
  const router = inject(Router);
  const role = localStorage.getItem('role') || '';
  if (allowedRoles.includes(role)) return true;
  router.navigate(['/dashboard']);
  return false;
};
