export type UserRole = 'ADMIN' | 'VENDITORE' | 'ACQUIRENTE';

export interface User {
  id: number;
  nome: string;
  cognome: string;
  email: string;
  ruolo: UserRole;
}
