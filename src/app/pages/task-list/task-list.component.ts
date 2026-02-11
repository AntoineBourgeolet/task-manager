
import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CdkDragDrop, DragDropModule, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';

import { TaskService } from '../../services/task/task.service';
import { Task, Board, ColumnId, columnsTemplate } from '../../models/task';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { User } from '../../models/user';
import { UserService } from '../../services/user/user.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TaskEventsService } from '../../services/events/task-events/task-events.service';
import { TaskOpenedComponent } from '../task-opened.component/task-opened.component';
import { take } from 'rxjs';
import { MatBadge } from '@angular/material/badge';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';

@Component({
  selector: 'task-list',
  standalone: true,
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css'],
  imports: [
    CommonModule,
    DragDropModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDividerModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDialogModule, 
    MatMenuTrigger,
    MatMenuModule
  ]
})
export class TaskListComponent {
  columns = columnsTemplate;

  tasks: Board = { todo: [], blocked: [], doing: [], testing: [], done: [] };
  users: User[] = [];
  loading = true;






  constructor(
    private taskService: TaskService,
    private userService: UserService,
    private taskEvents: TaskEventsService,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog,
  ) { }


  ngOnInit(): void {

    this.taskEvents.refreshList$.subscribe(() => {
      this.loadBoard();
    });

    this.loadUsers();
    this.loadBoard();

    
  }

  loadUsers(): void {
    this.userService.getAllUser().subscribe({
      next: (usersResponse) => {
        this.users = usersResponse;
        console.log(this.users)
      },
    });
  }

  openTask(id: number | string){
    const dialogRef = this.dialog.open(TaskOpenedComponent
          , {
            data: { id}
          });
    
    
        dialogRef.afterClosed().pipe(take(1)).subscribe((result) => {
          if (result) {
            this.loadBoard();
    
            this.cdr.markForCheck();
          }
        });
  }

  deleteTask(idTask: number | string) {
    this.taskService.delete(idTask).subscribe(() => {
      this.loadBoard();
      this.cdr.detectChanges();
    });
  }

  loadBoard(): void {
    this.taskService.getAllTaskByStatus().subscribe({
      next: (board) => {
        this.tasks = board;
        console.log(this.tasks);
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  affecteUser(id: number | string, event: string): void {
    this.taskService.modifyUser(id, event).subscribe();
  }

// Pour CDK
getConnectedLists(): string[] {
  return this.columns.map(c => c.id);
}

drop(event: CdkDragDrop<Task[]>, targetColId: string) {
  if (event.previousContainer === event.container) {
    moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
  } else {
    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex
    );
    const moved = event.container.data[event.currentIndex];
    console.log(moved.id,targetColId.toUpperCase());
    this.taskService.modifyStatus?.( moved.id,targetColId).subscribe();
  }
}

priorityClass(p: number | undefined) {
  return {
    'card-red': p === 1,
    'card-orange': p === 2,
    'card-green': p === 3,
  };
}

priorityDotClass(p: number | undefined) {
  return { p1: p === 1, p2: p === 2, p3: p === 3 };
}

priorityChipClass(p: number | undefined) {
  return { p1: p === 1, p2: p === 2, p3: p === 3 };
}

isOverdue(d?: string | Date | null): boolean {
  if (!d) return false;
  const date = new Date(d);
  const today = new Date();
  // Comparaison sur la date seule
  date.setHours(0,0,0,0);
  today.setHours(0,0,0,0);
  return date < today;
}


  priorityColor(p?: Task['priority']) {
    switch (p) {
      case 1: return 'warn';
      case 2: return 'accent';
      default: return 'primary';
    }
  }
}
