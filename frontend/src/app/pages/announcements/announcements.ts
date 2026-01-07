import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { Observable, forkJoin, of, BehaviorSubject, combineLatest } from 'rxjs';
import { map, startWith, switchMap, tap, catchError } from 'rxjs/operators';

import { AnnouncementService } from '../../core/services/announcement.service';
import { AnnouncementLikeService } from '../../core/services/announcement-like.service';
import { AuthService } from '../../core/services/auth.service';
import { Announcement } from '../../core/models/announcement.model';

type TipoAnnuncio = '' | 'VENDITA' | 'AFFITTO';

interface Filters {
  tipo: TipoAnnuncio;
  prezzoMax?: number;
  bagni?: number;

  stanzeMin?: number;
  superficieMin?: number;
  superficieMax?: number;
  city: string;
}

@Component({
  selector: 'app-announcements',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './announcements.html',
})
export class AnnouncementsComponent {

  // ===== FILTRI (ngModel) =====
  filterTipo: TipoAnnuncio = '';
  filterPrezzo?: number;
  filterBagni?: number;

  filterStanzeMin?: number;
  filterSuperficieMin?: number;
  filterSuperficieMax?: number;
  filterCity: string = '';

  // ===== STREAM =====
  filteredAnnouncements$!: Observable<Announcement[]>;

  // ===== LIKE =====
  likeLoading: Record<number, boolean> = {};
  likeCounts: Record<number, number> = {};
  likedByMe: Record<number, boolean> = {};

  private userId: number | null = null;

  // stream filtri
  private filtersSubject = new BehaviorSubject<Filters>(this.buildFilters());
  private filters$ = this.filtersSubject.asObservable();

  constructor(
    private announcementService: AnnouncementService,
    private likeService: AnnouncementLikeService,
    private auth: AuthService
  ) {
    this.userId = this.auth.getUser()?.id ?? null;

    // 1) Dati dal backend + enrichment likes (IDENTICO concetto al tuo)
    const announcements$ = this.announcementService.getAll().pipe(
      switchMap((announcements) => {
        if (!announcements.length) {
          this.likeCounts = {};
          this.likedByMe = {};
          return of(announcements);
        }

        const countRequests: Record<number, Observable<number>> = {};
        const likedRequests: Record<number, Observable<boolean>> = {};

        for (const a of announcements) {
          countRequests[a.id] = this.likeService
            .countLikes(a.id)
            .pipe(catchError(() => of(0)));

          if (this.userId) {
            likedRequests[a.id] = this.likeService
              .likedByMe(a.id, this.userId)
              .pipe(catchError(() => of(false)));
          }
        }

        return forkJoin({
          counts: forkJoin(countRequests),
          liked: Object.keys(likedRequests).length ? forkJoin(likedRequests) : of({})
        }).pipe(
          tap(({ counts, liked }) => {
            this.likeCounts = counts;
            this.likedByMe = liked as any;
          }),
          map(() => announcements)
        );
      }),
      catchError(() => of([] as Announcement[]))
    );

    // 2) Filtri reattivi: ogni cambio filtro -> nuova lista
    this.filteredAnnouncements$ = combineLatest([announcements$, this.filters$]).pipe(
      map(([announcements, f]) => this.applyFilters(announcements, f)),
      startWith([] as Announcement[])
    );
  }

  // chiamata dal template su ogni change
  onFiltersChanged(): void {
    this.filtersSubject.next(this.buildFilters());
  }

  resetFilters(): void {
    this.filterTipo = '';
    this.filterPrezzo = undefined;
    this.filterBagni = undefined;

    this.filterStanzeMin = undefined;
    this.filterSuperficieMin = undefined;
    this.filterSuperficieMax = undefined;
    this.filterCity = '';

    this.onFiltersChanged();
  }

  private buildFilters(): Filters {
    return {
      tipo: this.filterTipo,
      prezzoMax: this.filterPrezzo,
      bagni: this.filterBagni,

      stanzeMin: this.filterStanzeMin,
      superficieMin: this.filterSuperficieMin,
      superficieMax: this.filterSuperficieMax,
      city: (this.filterCity || '').trim(),
    };
  }

  private applyFilters(announcements: Announcement[], f: Filters): Announcement[] {
    const cityQuery = f.city.toLowerCase();

    return announcements.filter(a => {
      const tipoOk = !f.tipo || a.tipo === f.tipo;
      const prezzoOk = !f.prezzoMax || a.prezzo <= f.prezzoMax;
      const bagniOk = !f.bagni || a.immobile?.bagni === f.bagni;

      const stanzeOk = !f.stanzeMin || (a.immobile?.stanze ?? 0) >= f.stanzeMin;

      const mq = a.immobile?.superficieMq ?? 0;
      const mqMinOk = !f.superficieMin || mq >= f.superficieMin;
      const mqMaxOk = !f.superficieMax || mq <= f.superficieMax;

      const cityVal = (a.immobile?.location?.city ?? '').toLowerCase();
      const cityOk = !cityQuery || cityVal.includes(cityQuery);

      return tipoOk && prezzoOk && bagniOk && stanzeOk && mqMinOk && mqMaxOk && cityOk;
    });
  }

  // ===== LIKE (COME LA TUA VERSIONE: reload totale) =====
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
      next: () => window.location.reload(),
      error: () => alert('Errore durante il like')
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
