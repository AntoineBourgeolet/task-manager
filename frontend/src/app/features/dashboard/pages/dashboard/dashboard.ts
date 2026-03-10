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
import { User } from '../../../user/models/user';
import { UserService } from '../../../user/data-access/user.api';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TaskEventsService } from '../../../task/data-access/task-events.service';
import { TaskOpenedComponent } from '../../../task/dialogs/task-opened/task-opened';
import { take } from 'rxjs';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Board } from '../../../../../environments/type';
import { columnsTemplate } from '../../../../../environments/const';
import {
  buildDeleteDto,
} from '../../../task/data-access/builders';
import { buildTaskPathDTO } from '../../../task/data-access/builders/task-patch.builder';

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
    MatDialogModule,
    MatMenuTrigger,
    MatMenuModule,
    MatSnackBarModule,
    MatTooltipModule,
  ],
})
export class Dashboard implements OnInit {
  private readonly taskService = inject(TaskService);
  private readonly userService = inject(UserService);
  private readonly taskEvents = inject(TaskEventsService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  private readonly actor: string = 'AntoineActor';

  public column = columnsTemplate;
  public board: Board = { TODO: [], BLOCKED: [], DOING: [], TESTING: [], DONE: [] };
  public users: User[] = [];
  public loading = true;

  private taskDeleteDto = buildDeleteDto();
  private taskPatchDto = buildTaskPathDTO();

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

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }

  openTask(id: number | string) {
    const dialogRef = this.dialog.open(TaskOpenedComponent, {
      data: { id },
      disableClose: true,
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
    this.taskDeleteDto = buildDeleteDto({ actor: this.actor });

    this.taskService.delete(idTask, this.taskDeleteDto).subscribe(() => {
      this.loadBoard();
      this.showSuccess('Suppression reussie');
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

  affecteUser(idTask: number, newTaskUser: string | null): void {
    this.taskPatchDto = buildTaskPathDTO({
      actor: this.actor,
      userAffectee: newTaskUser
    });
    this.taskService.patch(idTask, this.taskPatchDto).subscribe();
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
      this.taskPatchDto = buildTaskPathDTO({
      actor: this.actor,
      userAffectee: moved.userAffectee,
      status: targetColId as any
    });
    this.taskService.patch(moved.id, this.taskPatchDto).subscribe();      
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

  priorityLabel(p: number | undefined): string {
    if (p === 1) {
      return 'Haute';
    }

    if (p === 2) {
      return 'Moyenne';
    }

    if (p === 3) {
      return 'Basse';
    }

    return 'Non definie';
  }

  assigneeInitials(username: string | null | undefined): string {
    if (!username?.trim()) {
      return '?';
    }

    return username
      .trim()
      .split(/\s+/)
      .slice(0, 2)
      .map((part) => part[0]?.toUpperCase() ?? '')
      .join('');
  }

  totalTasks(): number {
    return Object.values(this.board).reduce((sum, tasks) => sum + tasks.length, 0);
  }

  doneTasks(): number {
    return this.board['DONE'].length;
  }

  completionRate(): number {
    const total = this.totalTasks();
    if (total === 0) {
      return 0;
    }

    return Math.round((this.doneTasks() / total) * 100);
  }
}
