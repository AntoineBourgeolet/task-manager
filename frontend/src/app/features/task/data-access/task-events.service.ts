import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TaskEventsService {
  private readonly refreshListSource = new Subject<void>();
  refreshList$ = this.refreshListSource.asObservable();

  notifyRefresh() {
    this.refreshListSource.next();
  }
}
