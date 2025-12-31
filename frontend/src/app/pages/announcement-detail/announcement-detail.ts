import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, EMPTY, of, forkJoin } from 'rxjs';
import { switchMap, catchError, tap, map } from 'rxjs/operators';

import { AnnouncementService } from '../../core/services/announcement.service';
import { AnnouncementLikeService } from '../../core/services/announcement-like.service';
import { AnnouncementReviewService } from '../../core/services/announcement-review.service';
import { AuthService } from '../../core/services/auth.service';

import { Announcement } from '../../core/models/announcement.model';
import { Review, CreateReviewRequest } from '../../core/models/review.model';

@Component({
  selector: 'app-announcement-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './announcement-detail.html',
})
export class AnnouncementDetailComponent {

  /* ==================== ANNUNCIO ==================== */

  announcement$!: Observable<Announcement>;
  error = false;

  /* ==================== LIKE ==================== */

  likeCount = 0;
  likedByMe = false;

  /* ==================== RECENSIONI ==================== */

  reviews: Review[] = [];

  myRating = 5;
  myComment = '';
  hasReviewed = false;

  /* ==================== CONTESTO ==================== */

  userId: number | null = null;
  private announcementId!: number;

  constructor(
    private route: ActivatedRoute,
    private announcementService: AnnouncementService,
    private likeService: AnnouncementLikeService,
    private reviewService: AnnouncementReviewService,
    private auth: AuthService
  ) {
    this.userId = this.auth.getUser()?.id ?? null;

    this.announcement$ = this.route.paramMap.pipe(
      switchMap(params => {
        const id = Number(params.get('id'));

        if (!id) {
          this.error = true;
          return EMPTY;
        }

        this.announcementId = id;
        this.error = false;

        // carica recensioni
        this.loadReviews();

        return this.announcementService.getById(id).pipe(
          switchMap(announcement => {
            const count$ = this.likeService.countLikes(id);
            const liked$ = this.userId
              ? this.likeService.likedByMe(id, this.userId)
              : of(false);

            return forkJoin({
              announcement: of(announcement),
              count: count$,
              liked: liked$
            });
          }),
          tap(({ count, liked }) => {
            this.likeCount = count;
            this.likedByMe = liked;
          }),
          map(({ announcement }) => announcement),
          catchError(err => {
            console.error(err);
            this.error = true;
            return EMPTY;
          })
        );
      })
    );
  }

  /* ==================== STATS DERIVATE ==================== */

  get reviewStats() {
    const count = this.reviews.length;
    const average =
      count === 0
        ? 0
        : Math.round(
            (this.reviews.reduce((s, r) => s + r.rating, 0) / count) * 10
          ) / 10;

    return { count, average };
  }

  /* ==================== LIKE ==================== */

  toggleLike(): void {
    if (!this.userId) {
      alert('Devi essere loggato per mettere like');
      return;
    }

    const action$ = this.likedByMe
      ? this.likeService.unlike(this.announcementId, this.userId)
      : this.likeService.like(this.announcementId, this.userId);

    action$.subscribe({
      next: () => {
        this.likedByMe = !this.likedByMe;
        this.likeCount += this.likedByMe ? 1 : -1;
      },
      error: () => alert('Errore durante il like')
    });
  }

  /* ==================== RECENSIONI ==================== */

  loadReviews(): void {
    this.reviewService.getReviews(this.announcementId).subscribe({
      next: r => {
        this.reviews = r ?? [];

        this.hasReviewed =
          !!this.userId &&
          this.reviews.some(rv => rv.user?.id === this.userId);
      },
      error: err => console.error('Errore caricamento recensioni', err)
    });
  }

  /* ===== INVIO / AGGIORNAMENTO RECENSIONE ===== */

  submitReview(): void {
    if (!this.userId) {
      alert('Devi essere loggato per recensire');
      return;
    }

    const payload: CreateReviewRequest = {
      rating: this.myRating,
      commento: this.myComment || ''
    };

    this.reviewService
      .addOrUpdateReview(this.announcementId, this.userId, payload)
      .subscribe({
        next: () => {
          // reset form
          this.myComment = '';
          this.myRating = 5;

          // ricarica recensioni subito
          this.loadReviews();

          // REFRESH HARD DOPO 2 SECONDI (PROGETTO DIDATTICO)
          setTimeout(() => {
            window.location.reload();
          }, 2000);
        },
        error: err => console.error('Errore invio recensione', err)
      });
  }

  /* ===== CANCELLAZIONE RECENSIONE ===== */

  deleteReview(): void {
    if (!this.userId) return;

    this.reviewService
      .deleteReview(this.announcementId, this.userId)
      .subscribe({
        next: () => {
          // ricarica recensioni subito
          this.loadReviews();

          // REFRESH HARD DOPO 2 SECONDI
          setTimeout(() => {
            window.location.reload();
          }, 2000);
        },
        error: err => console.error('Errore cancellazione recensione', err)
      });
  }

  /* ==================== MAIL ==================== */

  buildMailtoLink(a: Announcement): string {
    if (!a?.venditore?.email) return '';

    const subject = encodeURIComponent(`Richiesta informazioni: ${a.titolo}`);

    const body = encodeURIComponent(
      `Buongiorno ${a.venditore.nome},\n\n` +
      `sono interessato all'annuncio "${a.titolo}".\n` +
      `Potremmo sentirci per maggiori informazioni?\n\nGrazie.`
    );

    return `mailto:${a.venditore.email}?subject=${subject}&body=${body}`;
  }
}

