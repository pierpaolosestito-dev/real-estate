import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';

import { Announcement } from '../../core/models/announcement.model';
import { AnnouncementService } from '../../core/services/announcement.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-vendor-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './vendor-dashboard.html',
  styleUrls: ['./vendor-dashboard.css']
})
export class VendorDashboardComponent implements OnInit {

  private announcementsSubject = new BehaviorSubject<Announcement[]>([]);
  announcements$ = this.announcementsSubject.asObservable();

  selectedAnnouncement?: Announcement;
  showModal = false;

  private vendorId!: number;

  saving = false;
  deletingId: number | null = null;

  constructor(
    private announcementService: AnnouncementService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.auth.getUser();
    if (!user) return;

    this.vendorId = user.id;
    this.loadAnnouncements();
  }

  /** RICARICA LISTA */
  private loadAnnouncements(): void {
    this.announcementService
      .getByVendor(this.vendorId)
      .subscribe({
        next: data => this.announcementsSubject.next(data),
        error: err => console.error('Errore caricamento annunci venditore', err)
      });
  }

  /** MODALE */
  openEditModal(a: Announcement): void {
    this.selectedAnnouncement = structuredClone(a);
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedAnnouncement = undefined;
    this.saving = false;
  }

  /** SUBMIT MODIFICA -> PUT */
  submitEdit(): void {
    if (!this.selectedAnnouncement) return;

    this.saving = true;

    const payload: Announcement = this.selectedAnnouncement;

    // ✅ CHIAMA PUT/UPDATE (deve esistere nel service)
    this.announcementService.update(payload).subscribe({
      next: () => {
        this.closeModal();
        this.loadAnnouncements();
      },
      error: err => {
        console.error('Errore update annuncio', err);
        this.saving = false;
        alert('Errore durante la modifica dell’annuncio');
      }
    });
  }

  /** ELIMINA -> DELETE */
  deleteAnnouncement(id: number): void {
    if (!confirm('Eliminare questo annuncio?')) return;

    this.deletingId = id;

    this.announcementService.delete(id).subscribe({
      next: () => {
        this.deletingId = null;
        this.loadAnnouncements();
      },
      error: err => {
        console.error('Errore delete annuncio', err);
        this.deletingId = null;
        alert('Errore durante l’eliminazione dell’annuncio');
      }
    });
  }
}
