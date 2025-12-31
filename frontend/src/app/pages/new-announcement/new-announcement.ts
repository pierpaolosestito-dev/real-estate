import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { AnnouncementService } from '../../core/services/announcement.service';
import { AuthService } from '../../core/services/auth.service';
import { Announcement } from '../../core/models/announcement.model';
import { AnnouncementType } from '../../core/models/announcement.model';
import { PropertyType } from '../../core/models/property.model';
@Component({
  selector: 'app-new-announcement',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './new-announcement.html',
  styleUrls: ['./new-announcement.css']
})
export class NewAnnouncementComponent {

  // --- Campi UI: ANNUNCIO ---
  titolo = '';
  descrizione = '';
  prezzo: number | null = null;
  tipo: AnnouncementType = 'VENDITA';
  imageUrl = '';

  // --- Campi UI: IMMOBILE ---
  immobileTipo: PropertyType = 'APPARTAMENTO';
  superficieMq: number | null = null;
  stanze: number | null = null;
  bagni: number | null = null;

  // --- Campi UI: LOCATION ---
  indirizzo = '';
  citta = '';
  cap = '';

  // Coordinate geocoding (string perché arrivano da Nominatim come stringhe)
  latitudine: string | null = null;
  longitudine: string | null = null;

  // Stato UI
  geocoding = false;
  publishing = false;

  constructor(
    private announcements: AnnouncementService,
    public auth: AuthService,
    private router: Router
  ) {}

  // 1) Geocoding
  pubblicaAnnuncio() {
    if (!this.indirizzo || !this.citta || !this.cap) {
      alert('Inserisci indirizzo, città e CAP');
      return;
    }

    const address = `${this.indirizzo}, ${this.cap}, ${this.citta}, Italia`;

    this.geocoding = true;

    fetch(
      `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}`,
      {
        headers: {
          'Accept': 'application/json'
        }
      }
    )
      .then(res => res.json())
      .then(data => {
        if (data.length > 0) {
          this.latitudine = data[0].lat;
          this.longitudine = data[0].lon;
        } else {
          alert('Indirizzo non trovato');
        }
      })
      .catch(() => alert('Errore durante il geocoding'))
      .finally(() => this.geocoding = false);
  }

  // 2) Submit reale: costruisce Announcement completo e fa POST
  submit(): void {
    // Validazioni minime
    if (!this.titolo || !this.descrizione) {
      alert('Inserisci titolo e descrizione');
      return;
    }
    if (this.prezzo === null || this.prezzo <= 0) {
      alert('Inserisci un prezzo valido');
      return;
    }
    if (!this.immobileTipo) {
      alert('Seleziona il tipo immobile');
      return;
    }
    if (this.superficieMq === null || this.superficieMq <= 0) {
      alert('Inserisci una superficie valida');
      return;
    }
    if (this.stanze === null || this.stanze <= 0) {
      alert('Inserisci un numero stanze valido');
      return;
    }
    if (this.bagni === null || this.bagni <= 0) {
      alert('Inserisci un numero bagni valido');
      return;
    }

    if (!this.indirizzo || !this.citta || !this.cap) {
      alert('Inserisci indirizzo, città e CAP');
      return;
    }

    if (!this.latitudine || !this.longitudine) {
      alert('Prima trova le coordinate (clicca "Trova coordinate")');
      return;
    }

    const venditore = this.auth.getUser();
    if (!venditore) {
      alert('Utente non autenticato');
      return;
    }

    // Costruzione oggetto Announcement completo (rispetta il tuo model.ts)
    const announcement: Announcement = {
      id: 0, // il backend assegna l'id
      titolo: this.titolo.trim(),
      descrizione: this.descrizione.trim(),
      prezzo: this.prezzo,
      tipo: this.tipo,
      imageUrl: this.imageUrl?.trim() || 'https://placehold.co/1200x800?text=RealEstate',
      venditore,
      dataPubblicazione: new Date(), // hardcoded lato JS (come volevi)
      immobile: {
        id: 0,
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

    this.announcements.create(announcement).subscribe({
      next: () => {
        alert('Annuncio pubblicato!');
        this.router.navigate(['/vendor']);
      },
      error: (err) => {
        console.error(err);
        alert('Errore durante la pubblicazione');
        this.publishing = false;
      }
    });
  }
}
