import { Routes } from '@angular/router';
import { AdminGuard } from './core/guards/admin.guard';
import { AuthGuard } from './core/guards/auth.guard';
import { VendorGuard } from './core/guards/vendor.guard';

export const routes: Routes = [

  {
    path: '',
    loadComponent: () =>
      import('./pages/home/home')
        .then(m => m.HomeComponent)
  },

  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login')
        .then(m => m.LoginComponent)
  },

  // ANNUNCI
  {
    path: 'annunci',
    loadComponent: () =>
      import('./pages/announcements/announcements')
        .then(m => m.AnnouncementsComponent)
  },
  {
    path: 'annunci/:id',
    loadComponent: () =>
      import('./pages/announcement-detail/announcement-detail')
        .then(m => m.AnnouncementDetailComponent)
  },

  // AUTH
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/register/register')
        .then(m => m.RegisterComponent)
  },

  // ADMIN
  {
    path: 'admin',
    canActivate: [AuthGuard, AdminGuard],
    loadComponent: () =>
      import('./pages/admin-dashboard/admin-dashboard')
        .then(m => m.AdminDashboardComponent)
  },

  // VENDOR
  {
    path: 'vendor/new',
    canActivate: [AuthGuard, VendorGuard],
    loadComponent: () =>
      import('./pages/new-announcement/new-announcement')
        .then(m => m.NewAnnouncementComponent)
  },
  {
    path: 'vendor',
    canActivate: [AuthGuard, VendorGuard],
    loadComponent: () =>
      import('./pages/vendor-dashboard/vendor-dashboard')
        .then(m => m.VendorDashboardComponent)
  },

  {
  path: 'me',
  canActivate: [AuthGuard],
  loadComponent: () =>
    import('./pages/profile/profile')
      .then(m => m.ProfileComponent)
},

  // SEMPRE ULTIMA
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full'
  }
];
