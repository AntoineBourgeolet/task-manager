import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environnements/environnement';
import { User } from '../../models/user';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient);
    private readonly baseUrl = environment.apiBaseUrl?.replace(/\/$/, '') ?? ''; // ex: '/api'
    private readonly apiUrl = `${this.baseUrl}/users`;
  
      getAllUser(): Observable<User[]> {
        return this.http.get<User[]>(this.apiUrl);
      }
}
