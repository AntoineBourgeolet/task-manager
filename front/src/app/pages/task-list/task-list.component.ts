
import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CdkDragDrop, DragDropModule, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';

import { TaskService } from '../../services/task/task.service';
import { Task, Board, columnsTemplate } from '../../models/task/task';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { User } from '../../models/user/user';
import { UserService } from '../../services/user/user.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TaskEventsService } from '../../services/events/task-events/task-events.service';
import { TaskOpenedComponent } from '../task-opened.component/task-opened.component';
import { take } from 'rxjs';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { TaskDeleteDto } from '../../models/task/task-delete-dto';
import { TaskChangeStatusDTO } from '../../models/task/task-change-status-dto';
import { TaskChangeUserAffecteeDTO } from '../../models/task/task-change-user-affectee-dto';
import { move } from '@angular-devkit/schematics';

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

  tasks: Board = { TODO: [], BLOCKED: [], DOING: [], TESTING: [], DONE: [] };
  users: User[] = [];
  loading = true;

  actor: string = "AntoineActor";

  taskChangeUserAffecteeDTO: TaskChangeUserAffecteeDTO = { actor: 'AntoineActor', id: 0, newUser: ''};
  taskChangeStatusDTO: TaskChangeStatusDTO = { actor: 'AntoineActor', id: 0, newStatus: ''};
  taskDeleteDto: TaskDeleteDto = { actor: 'AntoineActor', id: 0};



  constructor(
    private taskService: TaskService,
    private userService: UserService,
    private taskEvents: TaskEventsService,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog,
  ) { }


  ngOnInit(): void {

    this.taskEvents.refreshList$.subscribe(() => {
      this.loadUsers();
      this.loadBoard();
    });

    this.loadUsers();
    this.loadBoard();

    
  }

  loadUsers(): void {
    this.userService.getAllUser().subscribe({
      next: (usersResponse) => {
        this.users = usersResponse;
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

  deleteTask(idTask: number) {
   this.taskDeleteDto = { actor: this.actor, id: idTask };

    this.taskService.delete(this.taskDeleteDto).subscribe(() => {
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

  affecteUser(idTask: number, newTaskUser: string): void {
    this.taskChangeUserAffecteeDTO = {actor: this.actor, id: idTask, newUser: newTaskUser};
    console.log(this.taskChangeUserAffecteeDTO);
    this.taskService.modifyUser(this.taskChangeUserAffecteeDTO).subscribe();
  }

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
    this.taskChangeStatusDTO = {actor: this.actor, id: moved.id, newStatus: targetColId}
    this.taskService.modifyStatus?.(this.taskChangeStatusDTO).subscribe();
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
