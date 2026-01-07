import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  Announcement,
  CreateAnnouncementPayload
} from '../models/announcement.model';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {

  private readonly API_URL = 'http://localhost:8081/api/announcements';

  constructor(private http: HttpClient) {}

  /**
   * ✅ CREA annuncio
   * Usa payload SENZA id, venditore, data
   */
create(payload: CreateAnnouncementPayload): Observable<Announcement> {
  return this.http.post<Announcement>(this.API_URL, payload);
}


  /** Tutti gli annunci */
  getAll(): Observable<Announcement[]> {
    return this.http.get<Announcement[]>(this.API_URL);
  }

  /** Dettaglio annuncio */
  getById(id: number): Observable<Announcement> {
    return this.http.get<Announcement>(`${this.API_URL}/${id}`);
  }

  /** Annunci di un venditore */
  getByVendor(vendorId: number): Observable<Announcement[]> {
    return this.http.get<Announcement[]>(
      `${this.API_URL}/vendor/${vendorId}`
    );
  }

  /**
   * ✏️ MODIFICA annuncio
   * Qui l'id ESISTE ed è obbligatorio
   */
  update(announcement: Announcement): Observable<Announcement> {
    return this.http.put<Announcement>(
      `${this.API_URL}/${announcement.id}`,
      announcement
    );
  }

  /** 🗑️ ELIMINA annuncio */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
