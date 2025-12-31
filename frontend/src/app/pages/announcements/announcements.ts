import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Observable, forkJoin, of } from 'rxjs';
import { map, startWith, switchMap, tap, catchError } from 'rxjs/operators';

import { AnnouncementService } from '../../core/services/announcement.service';
import { AnnouncementLikeService } from '../../core/services/announcement-like.service';
import { AuthService } from '../../core/services/auth.service';
import { Announcement } from '../../core/models/announcement.model';
import { finalize } from 'rxjs/operators';
@Component({
  selector: 'app-announcements',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './announcements.html',
})
export class AnnouncementsComponent {

  filterTipo: '' | 'VENDITA' | 'AFFITTO' = '';
  filterPrezzo?: number;
  filterBagni?: number;

  filteredAnnouncements$!: Observable<Announcement[]>;
  likeLoading: Record<number, boolean> = {};

  likeCounts: Record<number, number> = {};
  likedByMe: Record<number, boolean> = {};

  private userId: number | null = null;

  constructor(
    private announcementService: AnnouncementService,
    private likeService: AnnouncementLikeService,
    private auth: AuthService
  ) {
    this.userId = this.auth.getUser()?.id ?? null;

    this.filteredAnnouncements$ = this.announcementService.getAll().pipe(

      switchMap((announcements) => {
        if (!announcements.length) {
          this.likeCounts = {};
          this.likedByMe = {};
          return of(announcements);
        }

        const countRequests: Record<string, Observable<number>> = {};
        const likedRequests: Record<string, Observable<boolean>> = {};

        for (const a of announcements) {
          countRequests[a.id] = this.likeService.countLikes(a.id)
            .pipe(catchError(() => of(0)));

          if (this.userId) {
            likedRequests[a.id] = this.likeService
              .likedByMe(a.id, this.userId)
              .pipe(catchError(() => of(false)));
          }
        }

        return forkJoin({
          counts: forkJoin(countRequests),
          liked: Object.keys(likedRequests).length
            ? forkJoin(likedRequests)
            : of({})
        }).pipe(
          tap(({ counts, liked }) => {
            this.likeCounts = counts as any;
            this.likedByMe = liked as any;
          }),
          map(() => announcements)
        );
      }),

      map(announcements =>
        announcements.filter(a =>
          (!this.filterTipo || a.tipo === this.filterTipo) &&
          (!this.filterPrezzo || a.prezzo <= this.filterPrezzo) &&
          (!this.filterBagni || a.immobile?.bagni === this.filterBagni)
        )
      ),

      startWith([])
    );
  }

  

toggleLike(a: Announcement): void {
  if (!this.userId) {
    alert('Devi essere loggato per mettere like');
    return;
  }

  const userId = this.userId;

  const liked = this.likedByMe[a.id];

  const action$ = liked
    ? this.likeService.unlike(a.id, userId)
    : this.likeService.like(a.id, userId);

  action$.subscribe({
    next: () => {
      // 💣 refresh totale (F5)
      window.location.reload();
    },
    error: () => {
      alert('Errore durante il like');
    }
  });
}



  getLikeCount(id: number): number {
    return this.likeCounts[id] ?? 0;
  }

  isLiked(id: number): boolean {
    return !!this.likedByMe[id];
  }

  trackById(index: number, item: Announcement): number {
    return item.id;
  }
}
