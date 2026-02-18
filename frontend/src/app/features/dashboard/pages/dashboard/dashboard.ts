import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  CdkDragDrop,
  DragDropModule,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';

import { TaskService } from '../../../task/data-access/task.api';
import { Task } from '../../../task/models/task';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { User } from '../../../user/models/user';
import { UserService } from '../../../user/data-access/user.api';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TaskEventsService } from '../../../task/data-access/task-events.service';
import { TaskOpenedComponent } from '../../../task/dialogs/task-opened/task-opened';
import { take } from 'rxjs';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { Board } from '../../../../../environments/type';
import { columnsTemplate } from '../../../../../environments/const';
import {
  buildDeleteDto,
  buildTaskChangeStatusDTO,
  buildTaskChangeUserAffecteeDTO,
} from '../../../task/data-access/builders';

@Component({
  selector: 'dashboard',
  standalone: true,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
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
    MatMenuModule,
  ],
})
export class Dashboard implements OnInit {
  private readonly taskService = inject(TaskService);
  private readonly userService = inject(UserService);
  private readonly taskEvents = inject(TaskEventsService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly dialog = inject(MatDialog);

  private readonly actor: string = 'AntoineActor';

  public column = columnsTemplate;
  public board: Board = { TODO: [], BLOCKED: [], DOING: [], TESTING: [], DONE: [] };
  public users: User[] = [];
  public loading = true;

  private taskDeleteDto = buildDeleteDto();
  private taskChangeUserAffecteeDTO = buildTaskChangeUserAffecteeDTO();
  private taskChangeStatusDTO = buildTaskChangeStatusDTO();

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

  openTask(id: number | string) {
    const dialogRef = this.dialog.open(TaskOpenedComponent, {
      data: { id },
    });

    dialogRef
      .afterClosed()
      .pipe(take(1))
      .subscribe((result) => {
        if (result) {
          this.loadBoard();
          this.cdr.markForCheck();
        }
      });
  }

  deleteTask(idTask: number) {
    this.taskDeleteDto = buildDeleteDto({ actor: this.actor, id: idTask });

    this.taskService.delete(this.taskDeleteDto).subscribe(() => {
      this.loadBoard();
      this.cdr.detectChanges();
    });
  }

  loadBoard(): void {
    this.taskService.getAllTaskByStatus().subscribe({
      next: (board) => {
        this.board = board;
        console.log(this.board);
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err),
    });
  }

  affecteUser(idTask: number, newTaskUser: string): void {
    this.taskChangeUserAffecteeDTO = buildTaskChangeUserAffecteeDTO({
      actor: this.actor,
      id: idTask,
      newUser: newTaskUser,
    });
    console.log(this.taskChangeUserAffecteeDTO);
    this.taskService.modifyUser(this.taskChangeUserAffecteeDTO).subscribe();
  }

  getConnectedLists(): string[] {
    return columnsTemplate.map((c) => c.id);
  }

  drop(event: CdkDragDrop<Task[]>, targetColId: string) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
      const moved = event.container.data[event.currentIndex];
      console.log(moved.id, targetColId.toUpperCase());
      this.taskChangeStatusDTO = { actor: this.actor, id: moved.id, newStatus: targetColId };
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
  priorityChipClass(p: number | undefined) {
    return { p1: p === 1, p2: p === 2, p3: p === 3 };
  }
}
