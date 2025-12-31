/* User ridotto per le recensioni (coerente col backend) */
export interface ReviewUser {
  id: number;
  nome: string;
  cognome: string;
}

export interface Review {
  id: number;
  rating: number;
  commento?: string;
  createdAt: string;
  user: ReviewUser;
}

export interface ReviewStats {
  count: number;
  average: number;
}

/* DTO per POST */
export interface CreateReviewRequest {
  rating: number;
  commento: string;
}
