import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable, of } from 'rxjs';

import { AuthService, User } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import {
  PriceComparisonService,
  PriceStats
} from '../../core/services/price-comparison.service';

import { Announcement } from '../../core/models/announcement.model';
import Chart from 'chart.js/auto';

@Component({
  standalone: true,
  selector: 'app-profile',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './profile.html'
})
export class ProfileComponent implements OnInit {

  user!: User;

  saving = false;
  success = false;
  error = false;
  noComparisonData = false;
  /** ❤️ liked */
  likedAnnouncements$!: Observable<Announcement[]>;

  /** 📊 comparazione */
  selectedAnnouncement?: Announcement;
  similarAnnouncements: Announcement[] = [];
  stats?: PriceStats;
  position?: 'LOW' | 'MID' | 'HIGH';

  private chart?: Chart;

  constructor(
    private auth: AuthService,
    private users: UserService,
    private comparison: PriceComparisonService
  ) {}

  ngOnInit(): void {
    const stored = this.auth.getUser();
    if (!stored) {
      this.likedAnnouncements$ = of([]);
      return;
    }

    this.user = { ...stored };
    this.likedAnnouncements$ =
      this.users.getLikedAnnouncements(this.user.id);
  }

  save(): void {
    this.saving = true;
    this.success = false;
    this.error = false;

    this.users.update(this.user).subscribe({
      next: updated => {
        this.auth.updateStoredUser(updated);
        this.success = true;
        this.saving = false;
      },
      error: () => {
        this.error = true;
        this.saving = false;
      }
    });
  }

  // =====================================================
  // 📊 COMPARA PREZZI
  // =====================================================

  compare(
    base: Announcement,
    all: Announcement[]
  ): void {

    this.selectedAnnouncement = base;

    this.similarAnnouncements =
      this.comparison.getSimilarAnnouncements(base, all);

    if (this.similarAnnouncements.length === 0) {
      this.stats = undefined;
      this.position = undefined;
      this.chart?.destroy();
      return;
    }

    this.stats =
      this.comparison.computeStats(this.similarAnnouncements);

    this.position =
      this.comparison.positionLabel(
        base.prezzo,
        this.stats.avg
      );

    this.renderChart();
  }

  private renderChart(): void {
    const canvas =
      document.getElementById('priceChart') as HTMLCanvasElement;

    if (!canvas || !this.stats) return;

    this.chart?.destroy();

    this.chart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: this.similarAnnouncements.map(a =>
          `${a.immobile.superficieMq} mq`
        ),
        datasets: [{
          label: 'Prezzo (€)',
          data: this.similarAnnouncements.map(a => a.prezzo),
          backgroundColor: '#0d6efd'
        }]
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Confronto prezzi annunci simili'
          }
        }
      }
    });
  }
}
