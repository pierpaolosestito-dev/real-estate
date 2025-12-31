import { Injectable } from '@angular/core';
import { Announcement } from '../models/announcement.model';

export interface PriceStats {
  min: number;
  max: number;
  avg: number;
  avgPerMq: number;
}

@Injectable({
  providedIn: 'root'
})
export class PriceComparisonService {

  getSimilarAnnouncements(
    base: Announcement,
    all: Announcement[],
    tolerance = 0.2 // ±20%
  ): Announcement[] {

    const baseMq = base.immobile.superficieMq;

    return all.filter(a =>
      a.id !== base.id &&
      a.tipo === base.tipo &&
      a.immobile.location.city === base.immobile.location.city &&
      a.immobile.superficieMq >= baseMq * (1 - tolerance) &&
      a.immobile.superficieMq <= baseMq * (1 + tolerance)
    );
  }

  computeStats(announcements: Announcement[]): PriceStats {
    const prices = announcements.map(a => a.prezzo);
    const perMq = announcements.map(a => a.prezzo / a.immobile.superficieMq);

    const avg = prices.reduce((a, b) => a + b, 0) / prices.length;
    const avgMq = perMq.reduce((a, b) => a + b, 0) / perMq.length;

    return {
      min: Math.min(...prices),
      max: Math.max(...prices),
      avg: Math.round(avg),
      avgPerMq: Math.round(avgMq)
    };
  }

  positionLabel(price: number, avg: number): 'LOW' | 'MID' | 'HIGH' {
    if (price < avg * 0.9) return 'LOW';
    if (price > avg * 1.1) return 'HIGH';
    return 'MID';
  }
}
