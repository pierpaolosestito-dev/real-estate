import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { UserService } from '../../core/services/user.service';
import { AnnouncementService } from '../../core/services/announcement.service';
import { User } from '../../core/models/user.model';
import { Announcement } from '../../core/models/announcement.model';
import { Role } from '../../core/services/auth.service';
@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboardComponent {

  /** TAB */
  activeTab: 'users' | 'announcements' = 'users';

  /** FILTRI UI */
  userEmailFilter = '';
  userRoleFilter = '';

  vendorEmailFilter = '';
  announcementTypeFilter = '';

  /** SUBJECT */
  private userEmailFilter$ = new BehaviorSubject<string>('');
  private userRoleFilter$ = new BehaviorSubject<string>('');
  private vendorEmailFilter$ = new BehaviorSubject<string>('');
  private announcementTypeFilter$ = new BehaviorSubject<string>('');

  /** STREAM DATI */
  users$: Observable<User[]>;
  announcements$: Observable<Announcement[]>;

  constructor(
    private userService: UserService,
    private announcementService: AnnouncementService
  ) {

    this.users$ = combineLatest([
      this.userService.getAll(),
      this.userEmailFilter$,
      this.userRoleFilter$
    ]).pipe(
      map(([users, email, role]) =>
        users.filter(u =>
          (!email || u.email.toLowerCase().includes(email.toLowerCase())) &&
          (!role || u.ruolo === role)
        )
      )
    );

    this.announcements$ = combineLatest([
      this.announcementService.getAll(),
      this.vendorEmailFilter$,
      this.announcementTypeFilter$
    ]).pipe(
      map(([announcements, vendorEmail, type]) =>
        announcements.filter(a =>
          (!vendorEmail ||
            a.venditore?.email.toLowerCase().includes(vendorEmail.toLowerCase())) &&
          (!type || a.tipo === type)
        )
      )
    );
  }

  /** UPDATE FILTRI */
  onUserFilterChange(): void {
    this.userEmailFilter$.next(this.userEmailFilter);
    this.userRoleFilter$.next(this.userRoleFilter);
  }

  onAnnouncementFilterChange(): void {
    this.vendorEmailFilter$.next(this.vendorEmailFilter);
    this.announcementTypeFilter$.next(this.announcementTypeFilter);
  }

  /** AZIONI */
  disableUser(user: User): void {
    if (confirm(`Disabilitare ${user.email}?`)) {
      this.userService.delete(user.id).subscribe();
    }
  }

  promoteToAdmin(user: User): void {
  if (confirm(`Rendere ${user.email} amministratore?`)) {

    const updated: User = {
      ...user,
      ruolo: 'ADMIN'
    };

    this.userService.update(updated).subscribe();
  }
}


  removeAnnouncement(a: Announcement): void {
    if (confirm(`Rimuovere annuncio "${a.titolo}"?`)) {
      this.announcementService.delete(a.id).subscribe();
    }
  }
}
