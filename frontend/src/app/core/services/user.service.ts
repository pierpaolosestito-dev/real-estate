import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { Announcement } from '../models/announcement.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly API_URL = 'http://localhost:8081/api/users';

  constructor(private http: HttpClient) {}

  /** GET /api/users */
  getAll(): Observable<User[]> {
    return this.http.get<User[]>(this.API_URL);
  }

  /** PUT /api/users/{id} */
  update(user: User): Observable<User> {
    return this.http.put<User>(
      `${this.API_URL}/${user.id}`,
      user
    );
  }

  /** DELETE /api/users/{id} */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  /**
   * ✅ GET /api/users/{id}/liked-announcements
   */
  getLikedAnnouncements(userId: number): Observable<Announcement[]> {
    return this.http.get<Announcement[]>(
      `${this.API_URL}/${userId}/liked-announcements`
    );
  }
}
