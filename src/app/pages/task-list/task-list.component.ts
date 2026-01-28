
import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CdkDragDrop, DragDropModule, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';

import { TaskService } from '../../services/task.service';
import { Task, Board, ColumnId } from '../../models/task';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { User } from '../../models/user';
import { UserService } from '../../services/user/user.service';

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
    MatSelectModule
  ]
})
export class TaskListComponent {
  columns = [
    { id: 'todo', title: 'À faire' },
    { id: 'blocked', title: 'En attente' },
    { id: 'doing', title: 'En cours' },
    { id: 'testing', title: 'En test' },
    { id: 'done', title: 'Terminé' }
  ] as const;

  tasks: Board = { todo: [], blocked: [], doing: [], testing: [], done: [] };
  users: User[] = [];
  loading = true;

  private readonly taskService = inject(TaskService);
  private readonly userService = inject(UserService)


  constructor(private cdr: ChangeDetectorRef) { }


  ngOnInit(): void {
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

  affecteUser(id: number | string, event: string): void{
    this.taskService.modifyUser(id,event).subscribe();
  }

  drop(event: CdkDragDrop<Task[]>, columnId: ColumnId) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    }
    this.taskService.modifyStatus(event.container.data[0]['id'], event.container.id).subscribe({
      next: (task) => {this.cdr.detectChanges(),
      console.log(task)}
    });
    //todo : PB ici... quand on clique sur bouton ça subscribe plus
  }

  getConnectedLists(): string[] {
    return this.columns.map(c => c.id);
  }

  priorityColor(p?: Task['priority']) {
    switch (p) {
      case 1: return 'warn';
      case 2: return 'accent';
      default: return 'primary';
    }
  }
}
