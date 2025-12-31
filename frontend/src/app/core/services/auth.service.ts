import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export type Role = 'ADMIN' | 'VENDITORE' | 'ACQUIRENTE';

export interface User {
  id: number;
  nome: string;
  cognome: string;
  email: string;
  ruolo: Role;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly API_URL = 'http://localhost:8081/api/users';
  private readonly STORAGE_KEY = 'auth_user';

  private currentUser: User | null = null;

  constructor(private http: HttpClient) {
    // 🔁 ripristino automatico da localStorage
    const storedUser = localStorage.getItem(this.STORAGE_KEY);
    if (storedUser) {
      this.currentUser = JSON.parse(storedUser);
    }
  }

  /** LOGIN */
  login(email: string, password: string): Observable<User> {
    // password ignorata come deciso
    return this.http.get<User[]>(this.API_URL).pipe(
      map(users => {
        const user = users.find(
          u => u.email.toLowerCase() === email.toLowerCase()
        );

        if (!user) {
          throw new Error('Utente non trovato');
        }

        this.currentUser = user;
        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(user));

        return user;
      })
    );
  }

  /** LOGOUT */
  logout(): void {
    this.currentUser = null;
    localStorage.removeItem(this.STORAGE_KEY);
  }

  /** STATO */
  isLogged(): boolean {
    return this.currentUser !== null;
  }

  /** RUOLO */
  getRole(): Role | null {
    return this.currentUser?.ruolo ?? null;
  }

  /** UTENTE */
  getUser(): User | null {
    return this.currentUser;
  }

  updateStoredUser(user: User): void {
  this.currentUser = user;
  localStorage.setItem(this.STORAGE_KEY, JSON.stringify(user));
}

getUserId(): number | null {
    return this.currentUser?.id ?? null;
  }

}
