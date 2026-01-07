import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import Swal from 'sweetalert2';

import { AnnouncementService } from '../../core/services/announcement.service';
import { AuthService } from '../../core/services/auth.service';
import {
  AnnouncementType,
  CreateAnnouncementPayload
} from '../../core/models/announcement.model';
import { PropertyType } from '../../core/models/property.model';

@Component({
  selector: 'app-new-announcement',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './new-announcement.html',
  styleUrls: ['./new-announcement.css']
})
export class NewAnnouncementComponent {

  // =====================
  // DATI ANNUNCIO
  // =====================
  titolo = '';
  descrizione = '';
  prezzo: number | null = null;
  tipo: AnnouncementType = 'VENDITA';
  imageUrl = '';

  // =====================
  // DATI IMMOBILE
  // =====================
  immobileTipo: PropertyType = 'APPARTAMENTO';
  superficieMq: number | null = null;
  stanze: number | null = null;
  bagni: number | null = null;

  // =====================
  // LOCALIZZAZIONE
  // =====================
  indirizzo = '';
  citta = '';
  cap = '';

  latitudine: string | null = null;
  longitudine: string | null = null;

  geocoding = false;
  publishing = false;

  constructor(
    private announcements: AnnouncementService,
    private auth: AuthService,
    private router: Router
  ) {}

  // =====================
  // GEOCODING
  // =====================
  pubblicaAnnuncio(): void {
    if (!this.indirizzo || !this.citta || !this.cap) {
      Swal.fire({
        icon: 'warning',
        title: 'Dati mancanti',
        text: 'Inserisci indirizzo, città e CAP'
      });
      return;
    }

    const address = `${this.indirizzo}, ${this.cap}, ${this.citta}, Italia`;
    this.geocoding = true;

    fetch(
      `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}`,
      { headers: { Accept: 'application/json' } }
    )
      .then(res => res.json())
      .then(data => {
        if (data.length > 0) {
          this.latitudine = data[0].lat;
          this.longitudine = data[0].lon;

          Swal.fire({
            icon: 'success',
            title: 'Coordinate trovate',
            text: 'Localizzazione completata con successo'
          });
        } else {
          Swal.fire({
            icon: 'error',
            title: 'Indirizzo non trovato',
            text: 'Verifica i dati inseriti'
          });
        }
      })
      .catch(() => {
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Errore durante il geocoding'
        });
      })
      .finally(() => (this.geocoding = false));
  }

  // =====================
  // SUBMIT
  // =====================
  submit(): void {

    const venditoreId = this.auth.getUserId();
    if (!venditoreId) {
      Swal.fire({
        icon: 'warning',
        title: 'Accesso richiesto',
        text: 'Utente non autenticato'
      });
      return;
    }

    // ---- Validazione completa ----
    if (
      !this.titolo ||
      !this.descrizione ||
      this.prezzo === null ||
      this.superficieMq === null ||
      this.stanze === null ||
      this.bagni === null ||
      !this.indirizzo ||
      !this.citta ||
      !this.cap ||
      !this.latitudine ||
      !this.longitudine
    ) {
      Swal.fire({
        icon: 'warning',
        title: 'Campi incompleti',
        text: 'Compila tutti i campi e trova le coordinate'
      });
      return;
    }

    // ---- Payload POST (type-safe) ----
    const payload: CreateAnnouncementPayload = {
      titolo: this.titolo.trim(),
      descrizione: this.descrizione.trim(),
      prezzo: this.prezzo,
      tipo: this.tipo,
      imageUrl:
        this.imageUrl.trim() ||
        'https://placehold.co/1200x800?text=RealEstate',
      venditoreId,
      immobile: {
        tipo: this.immobileTipo,
        superficieMq: this.superficieMq,
        stanze: this.stanze,
        bagni: this.bagni,
        location: {
          address: this.indirizzo.trim(),
          city: this.citta.trim(),
          latitude: Number(this.latitudine),
          longitude: Number(this.longitudine)
        }
      }
    };

    this.publishing = true;

    this.announcements.create(payload).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Annuncio pubblicato',
          text: 'Il tuo annuncio è stato pubblicato con successo'
        }).then(() => {
          this.router.navigate(['/vendor']);
        });
      },
      error: err => {
        console.error(err);
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Errore durante la pubblicazione'
        });
        this.publishing = false;
      }
    });
  }
}
