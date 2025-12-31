import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {

  nome = '';
  cognome = '';
  email = '';
  password = ''; // solo UI
  ruolo: 'ACQUIRENTE' | 'VENDITORE' = 'ACQUIRENTE';

  loading = false;
  error = false;
  success = false;

  private readonly API_URL = 'http://localhost:8081/api/users';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  submit(): void {
    this.error = false;
    this.success = false;

    if (!this.nome || !this.cognome || !this.email) {
      this.error = true;
      return;
    }

    this.loading = true;

    const payload = {
      nome: this.nome,
      cognome: this.cognome,
      email: this.email,
      ruolo: this.ruolo
    };

    this.http.post(this.API_URL, payload).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;

        // redirect soft dopo 1s
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1000);
      },
      error: err => {
        console.error(err);
        this.error = true;
        this.loading = false;
      }
    });
  }
}
