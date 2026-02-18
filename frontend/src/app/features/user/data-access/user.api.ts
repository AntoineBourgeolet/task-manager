import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { User } from '../models/user';
import { Observable } from 'rxjs';
import { api } from '../../../../environments/const'
import { UserCreateDto } from '../models/user-create.dto';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient);
    private readonly baseUrl = api.apiBaseUrl?.replace(/\/$/, '') ?? '';
    private readonly apiUrl = `${this.baseUrl}/account`;
  
      getAllUser(): Observable<User[]> {
        return this.http.get<User[]>(this.apiUrl);
      }

      create(userCreateDto: UserCreateDto): Observable<User> {
        return this.http.post<User>(this.apiUrl,userCreateDto);
      }
}
