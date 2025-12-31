import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Announcement } from '../models/announcement.model';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {

  private readonly API_URL = 'http://localhost:8081/api/announcements';

  constructor(private http: HttpClient) {}

  /** CREA annuncio */
  create(announcement: Announcement): Observable<Announcement> {
    return this.http.post<Announcement>(this.API_URL, announcement);
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

  /** MODIFICA annuncio */
  update(announcement: Announcement): Observable<Announcement> {
    return this.http.put<Announcement>(
      `${this.API_URL}/${announcement.id}`,
      announcement
    );
  }

  /** ELIMINA annuncio */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
