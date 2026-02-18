import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'dashboard',
    loadChildren: () =>
      import('./features/dashboard/dashboard.routes').then(m => m.DASHBOARD_ROUTES),
  },
  {
    path: 'tasks',
    loadChildren: () =>
      import('./features/task/task.routes').then(m => m.TASK_ROUTES),
  },
  {
    path: 'users',
    loadChildren: () =>
      import('./features/user/user.routes').then(m => m.USER_ROUTES),
  },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' },
];