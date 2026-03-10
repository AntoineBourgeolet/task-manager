import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { api } from '../../../../environments/const';
import { TagCreateDto } from '../models/tag-create-dto';
import { Tag } from '../models/tag';
import { TagDeleteDto } from '../models/tag-delete-dto';
import { TagPatchDto } from '../models/tag-patch-dto';

@Injectable({ providedIn: 'root' })
export class TagService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = api.apiBaseUrl?.replace(/\/$/, '') ?? '';
  private readonly apiUrl = `${this.baseUrl}/tag`;

  getAll(): Observable<Tag[]> {
    return this.http.get<Tag[]>(this.apiUrl);
  }

  create(tagCreateDto: TagCreateDto): Observable<Tag> {
    return this.http.post<Tag>(this.apiUrl, tagCreateDto);
  }

  patch(tagPatchDto: TagPatchDto): Observable<Tag> {
    return this.http.patch<Tag>(this.apiUrl, tagPatchDto);
  }

  delete(tagDeleteDto: TagDeleteDto): Observable<void> {
    return this.http.request<void>('delete', this.apiUrl, {
      body: tagDeleteDto,
      headers: { 'Content-Type': 'application/json' },
    });
  }
}
