import {
  Component,
  ChangeDetectorRef,
  ViewChild,
  ElementRef,
  AfterViewChecked
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, EMPTY, of, forkJoin } from 'rxjs';
import { switchMap, catchError, tap, map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import * as L from 'leaflet';

import Swal from 'sweetalert2';

import { AnnouncementService } from '../../core/services/announcement.service';
import { AnnouncementLikeService } from '../../core/services/announcement-like.service';
import { AnnouncementReviewService } from '../../core/services/announcement-review.service';
import { AuthService } from '../../core/services/auth.service';

import { Announcement } from '../../core/models/announcement.model';
import { Review, CreateReviewRequest } from '../../core/models/review.model';

/* ==================== FIX LEAFLET MARKER ==================== */

delete (L.Icon.Default.prototype as any)._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

/* ==================== METEO ==================== */

interface WeatherData {
  temperature: number;
  windspeed: number;
  weathercode: number;
}

@Component({
  selector: 'app-announcement-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './announcement-detail.html',
})
export class AnnouncementDetailComponent implements AfterViewChecked {

  announcement$!: Observable<Announcement>;
  error = false;

  likeCount = 0;
  likedByMe = false;

  reviews: Review[] = [];
  myRating = 5;
  myComment = '';
  hasReviewed = false;

  weather: WeatherData | null = null;
  weatherLoading = false;
  weatherError = false;

  @ViewChild('mapAnnouncement')
  mapElementRef!: ElementRef<HTMLDivElement>;

  private mapInitialized = false;
  private pendingMapCoords: { lat: number; lng: number } | null = null;

  userId: number | null = null;
  private announcementId!: number;

  constructor(
    private route: ActivatedRoute,
    private announcementService: AnnouncementService,
    private likeService: AnnouncementLikeService,
    private reviewService: AnnouncementReviewService,
    private auth: AuthService,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
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
          tap(({ announcement }) => {
            const loc = announcement.immobile?.location;
            if (loc?.latitude && loc?.longitude) {
              this.loadWeather(loc.latitude, loc.longitude);
              this.pendingMapCoords = { lat: loc.latitude, lng: loc.longitude };
            }
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

  ngAfterViewChecked(): void {
    if (!this.mapInitialized && this.mapElementRef && this.pendingMapCoords) {
      const { lat, lng } = this.pendingMapCoords;
      this.initMap(lat, lng);
      this.mapInitialized = true;
    }
  }

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
      Swal.fire({
        icon: 'warning',
        title: 'Accesso richiesto',
        text: 'Devi essere loggato per mettere like'
      });
      return;
    }

    const action$ = this.likedByMe
      ? this.likeService.unlike(this.announcementId, this.userId)
      : this.likeService.like(this.announcementId, this.userId);

    action$.subscribe({
      next: () => {
        window.location.reload(); // progetto didattico
      },
      error: () => {
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: 'Errore durante il like'
        });
      }
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

  submitReview(): void {
    if (!this.userId) {
      Swal.fire({
        icon: 'warning',
        title: 'Accesso richiesto',
        text: 'Devi essere loggato per recensire'
      });
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
          this.myComment = '';
          this.myRating = 5;
          this.loadReviews();
          setTimeout(() => window.location.reload(), 2000);
        },
        error: err => console.error('Errore invio recensione', err)
      });
  }

  deleteReview(): void {
    if (!this.userId) return;

    this.reviewService
      .deleteReview(this.announcementId, this.userId)
      .subscribe({
        next: () => {
          this.loadReviews();
          setTimeout(() => window.location.reload(), 2000);
        },
        error: err => console.error('Errore cancellazione recensione', err)
      });
  }

  /* ==================== METEO ==================== */

  loadWeather(lat: number, lon: number): void {
    this.weatherLoading = true;
    this.weatherError = false;

    this.http
      .get<any>(
        `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current_weather=true`
      )
      .subscribe({
        next: data => {
          this.weather = data.current_weather ?? null;
          this.weatherLoading = false;
          this.cdr.detectChanges();
        },
        error: err => {
          console.error('Errore meteo', err);
          this.weatherError = true;
          this.weatherLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  getWeatherDescription(code: number): string {
    const map: Record<number, string> = {
      0: 'Sereno',
      1: 'Prevalentemente sereno',
      2: 'Parzialmente nuvoloso',
      3: 'Nuvoloso',
      45: 'Nebbia',
      48: 'Nebbia',
      51: 'Pioggerella',
      61: 'Pioggia',
      71: 'Neve',
      80: 'Rovesci'
    };

    return map[code] ?? 'Condizioni variabili';
  }

  /* ==================== MAPPA ==================== */

  private initMap(lat: number, lng: number): void {
    const map = L.map(this.mapElementRef.nativeElement, {
      center: [lat, lng],
      zoom: 15,
      scrollWheelZoom: false,
      zoomControl: true
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(map);

    L.marker([lat, lng])
      .addTo(map)
      .bindPopup('Posizione immobile');

    setTimeout(() => map.invalidateSize(), 0);
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
