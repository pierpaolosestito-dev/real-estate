import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {

  email = '';
  password = '';

  loading = false;
  error: string | null = null;

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  submit(): void {
    this.loading = true;
    this.error = null;

    this.auth.login(this.email, this.password).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: err => {
        this.error = err.message || 'Errore di login';
        this.loading = false;
      }
    });
  }
}
