import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environnements/environnement';
import { User, UserCreateDto } from '../../models/user/user';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient);
    private readonly baseUrl = environment.apiBaseUrl?.replace(/\/$/, '') ?? '';
    private readonly apiUrl = `${this.baseUrl}/users`;
  
      getAllUser(): Observable<User[]> {
        return this.http.get<User[]>(this.apiUrl);
      }

      create(userCreateDto: UserCreateDto): Observable<User> {
        return this.http.post<User>(this.apiUrl,userCreateDto);
      }
}
