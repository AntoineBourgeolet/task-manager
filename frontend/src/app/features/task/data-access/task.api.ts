import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from '../models/task';
import { TaskCreateDto } from '../models/task-create-dto';
import { TaskDeleteDto } from '../models/task-delete-dto';
import { TaskPatchDto } from '../models/task-patch-dto';
import { Board } from '../../../../environments/type';
import { api } from '../../../../environments/const';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = api.apiBaseUrl?.replace(/\/$/, '') ?? '';
  private readonly apiUrl = `${this.baseUrl}/task`;

  getAllTaskByStatus(): Observable<Board> {
    return this.http.get<Board>(this.apiUrl + '/allByStatus');
  }

  getTaskById(id: number | string): Observable<Task> {
    return this.http.get<Task>(this.apiUrl + '/getTaskById/' + id);
  }

  create(taskCreateDto: TaskCreateDto): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, taskCreateDto);
  }

  delete(id: number, taskDeleteDto: TaskDeleteDto): Observable<void> {
    return this.http.delete<void>(this.apiUrl + '/' + id, {
      body: taskDeleteDto,
      headers: { 'Content-Type': 'application/json' },
    });
  }

  patch(id: number | string, taskPatchDTO: TaskPatchDto): Observable<Task> {
    return this.http.patch<Task>(this.apiUrl + '/' + id, taskPatchDTO);
  }

}
