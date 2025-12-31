import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService, Role, User } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent {

  constructor(
    public auth: AuthService,
    private router: Router
  ) {}

  get role(): Role | null {
    return this.auth.getRole();
  }

  get user(): User | null {
    return this.auth.getUser();
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
