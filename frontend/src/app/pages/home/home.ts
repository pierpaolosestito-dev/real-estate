import { Component, AfterViewInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';

// FIX marker Leaflet con CDN
delete (L.Icon.Default.prototype as any)._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements AfterViewInit {

  agencyName = 'Aurora Immobiliare';

  highlights = [
    {
      icon: 'bi-building',
      title: 'Immobili selezionati',
      text: 'Ogni immobile viene scelto secondo criteri di qualità, posizione e reale valore di mercato.'
    },
    {
      icon: 'bi-geo-alt',
      title: 'Esperienza locale',
      text: 'Conosciamo quartieri, zone emergenti e opportunità spesso invisibili online.'
    },
    {
      icon: 'bi-shield-check',
      title: 'Trasparenza totale',
      text: 'Dalla prima visita al rogito, nessuna sorpresa.'
    }
  ];

  stats = [
    { value: '15+', label: 'anni di esperienza' },
    { value: '1.200+', label: 'clienti soddisfatti' },
    { value: '900+', label: 'immobili venduti' },
    { value: '98%', label: 'recensioni positive' }
  ];

  steps = [
    {
      number: '01',
      title: 'Ascolto',
      text: 'Partiamo dalle tue esigenze reali, non da un catalogo.'
    },
    {
      number: '02',
      title: 'Selezione',
      text: 'Ti proponiamo solo immobili realmente in linea.'
    },
    {
      number: '03',
      title: 'Assistenza',
      text: 'Ti seguiamo in ogni aspetto tecnico e burocratico.'
    },
    {
      number: '04',
      title: 'Consegna',
      text: 'Fino al momento in cui ricevi le chiavi.'
    }
  ];

  testimonials = [
    {
      name: 'Marco R.',
      text: 'Professionalità e umanità. Ho trovato casa senza stress.'
    },
    {
      name: 'Laura B.',
      text: 'Seguiti passo dopo passo, sempre disponibili.'
    },
    {
      name: 'Giuseppe T.',
      text: 'Un’agenzia che mette davvero le persone al centro.'
    }
  ];

  // --- MAPPA ---
  private lat = 41.9028;   // Roma (modifica liberamente)
  private lng = 12.4964;

  ngAfterViewInit(): void {
    this.initMap();
  }

  private initMap(): void {
    const map = L.map('map', {
      center: [this.lat, this.lng],
      zoom: 15,
      scrollWheelZoom: false
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    L.marker([this.lat, this.lng])
      .addTo(map)
      .bindPopup(`<b>${this.agencyName}</b><br>Sede operativa`);
  }
}
