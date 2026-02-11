
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environnements/environnement'; // vérifie le chemin
import { Task, Board, ColumnId, TaskCreateDto } from '../../models/task';
import { Observable, throwError } from 'rxjs';
import { TaskCreate } from '../../pages/task-create/task-create';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl?.replace(/\/$/, '') ?? ''; // ex: '/api'
  private readonly apiUrl = `${this.baseUrl}/tasks`;

  getAllTaskByStatus(): Observable<Board> {
    return this.http.get<Board>(this.apiUrl+ "/allByStatus");
  }

  getTaskById(id: number | string): Observable<Task>{
    return this.http.get<Task>(this.apiUrl+ "/getTaskById/"+id);
  }

  create(taskCreateDto: TaskCreateDto): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, taskCreateDto);
  }

  delete(idTask: number | string): Observable<void> {
    return this.http.delete<void>(this.apiUrl+"/"+idTask);
  }

  modifyStatus(task_id: string | number, new_status: String): Observable<Task>{
    return this.http.patch<Task>(this.apiUrl+"/modifyStatus/"+task_id+"?newStatus="+new_status.toUpperCase(),"");
  }

   modifyUser(task_id: string | number, new_username: String): Observable<Task>{
    return this.http.patch<Task>(this.apiUrl+"/modifyUser/"+task_id+"?newUser="+new_username,"");
  }

}
