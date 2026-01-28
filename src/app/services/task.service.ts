
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../environnements/environnement'; // vérifie le chemin
import { Task, Board, ColumnId } from '../models/task';
import { Observable, throwError } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl?.replace(/\/$/, '') ?? ''; // ex: '/api'
  private readonly apiUrl = `${this.baseUrl}/tasks`;

  getAllTaskByStatus(): Observable<Board> {
    return this.http.get<Board>(this.apiUrl+ "/allByStatus");
  }


  create(task: Task): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  modifyStatus(task_id: string | number, new_status: String): Observable<Task>{
    return this.http.patch<Task>(this.apiUrl+"/modifyStatus/"+task_id+"?new_status="+new_status.toUpperCase(),"");
  }

   modifyUser(task_id: string | number, new_username: String): Observable<Task>{
    return this.http.patch<Task>(this.apiUrl+"/modifyUser/"+task_id+"?new_user="+new_username,"");
  }

}
