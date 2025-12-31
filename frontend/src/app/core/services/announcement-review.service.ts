import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Review, ReviewStats, CreateReviewRequest } from '../models/review.model';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementReviewService {

  private readonly API_URL = 'http://localhost:8081/api/announcements';

  constructor(private http: HttpClient) {}

  getReviews(announcementId: number): Observable<Review[]> {
    return this.http.get<Review[]>(
      `${this.API_URL}/${announcementId}/reviews`
    );
  }

  getStats(announcementId: number): Observable<ReviewStats> {
    return this.http.get<ReviewStats>(
      `${this.API_URL}/${announcementId}/reviews/stats`
    );
  }

  addOrUpdateReview(
  announcementId: number,
  userId: number,
  payload: CreateReviewRequest
): Observable<void> {
  return this.http.post<void>(
    `${this.API_URL}/${announcementId}/reviews/${userId}`,
    payload
  );
}

deleteReview(
  announcementId: number,
  userId: number
): Observable<void> {
  return this.http.delete<void>(
    `${this.API_URL}/${announcementId}/reviews/${userId}`
  );
}

}
