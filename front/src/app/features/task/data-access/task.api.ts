import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environnements/environnement';
import { Observable } from 'rxjs';
import { Board, Task } from '../models/task';
import { TaskCreateDto } from '../models/task-create-dto';
import { TaskDeleteDto } from '../models/task-delete-dto';
import { TaskChangeStatusDTO } from '../models/task-change-status-dto';
import { TaskChangeUserAffecteeDTO } from '../models/task-change-user-affectee-dto';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl?.replace(/\/$/, '') ?? '';
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

  delete(taskDeleteDto: TaskDeleteDto): Observable<void> {
    return this.http.delete<void>(this.apiUrl, {
      body: taskDeleteDto,
      headers: { 'Content-Type': 'application/json' },
    });
  }

  modifyStatus(taskChangeStatusDTO: TaskChangeStatusDTO): Observable<Task> {
    return this.http.patch<Task>(this.apiUrl + '/modifyStatus', taskChangeStatusDTO);
  }

  modifyUser(taskChangeUserAffecteeDTO: TaskChangeUserAffecteeDTO): Observable<Task> {
    return this.http.patch<Task>(this.apiUrl + '/modifyUser', taskChangeUserAffecteeDTO);
  }
}
