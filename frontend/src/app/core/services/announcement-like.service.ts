import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementLikeService {

  private readonly API_URL = 'http://localhost:8081/api/announcements';

  constructor(private http: HttpClient) {}

  countLikes(announcementId: number): Observable<number> {
    return this.http.get<number>(
      `${this.API_URL}/${announcementId}/likes/count`
    );
  }

  likedByMe(announcementId: number, userId: number): Observable<boolean> {
    return this.http.get<boolean>(
      `${this.API_URL}/${announcementId}/likes/me`,
      { params: { userId } }
    );
  }

  like(announcementId: number, userId: number): Observable<void> {
    return this.http.post<void>(
      `${this.API_URL}/${announcementId}/like`,
      null,
      { params: { userId } }
    );
  }

  unlike(announcementId: number, userId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.API_URL}/${announcementId}/like`,
      { params: { userId } }
    );
  }
}
