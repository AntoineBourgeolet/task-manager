import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { UserService } from '../../../user/data-access/user.api';
import { TaskService } from '../../data-access/task.api';
import { User } from '../../../user/models/user';
import { Task } from '../../models/task';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { ClipboardModule } from '@angular/cdk/clipboard';
import { MatTooltipModule } from '@angular/material/tooltip';
import { columnsTemplate } from '../../../../../environments/const';

@Component({
  selector: 'task-opened',
  imports: [
    FormsModule,
    ClipboardModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    MatTooltipModule,
  ],
  templateUrl: './task-opened.html',
  styleUrl: './task-opened.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskOpenedComponent implements OnInit {
  private readonly userService: UserService = inject(UserService);
  private readonly taskService: TaskService = inject(TaskService);
  private readonly cdr = inject(ChangeDetectorRef);

  public readonly announcer = inject(LiveAnnouncer);
  public readonly dialogRef = inject(MatDialogRef<TaskOpenedComponent>);

  private readonly idTask = this.dialogRef._containerInstance._config.data.id;

  public readonly addOnBlur = true;
  public readonly separatorKeysCodes = [ENTER, COMMA] as const;

  public task: Task = {
    id: 0,
    title: '',
    description: '',
    priority: 1,
    userAffectee: '',
    status: 'TODO',
    tags: [],
  };
  public users: User[] = [];
  public statusBinded = '';

  ngOnInit(): void {
    this.loadUsers();
    this.loadTask();
  }

  loadUsers(): void {
    this.userService.getAllUser().subscribe({
      next: (usersResponse) => {
        this.users = usersResponse;
      },
    });
  }

  loadTask(): void {
    console.log(this.dialogRef);
    this.taskService.getTaskById(this.idTask).subscribe({
      next: (taskResponse) => {
        this.task = taskResponse;
        this.statusBinded = this.getStatusById(this.task.status);
        this.cdr.detectChanges();
      },
    });
  }

  private normalizeId(id?: string | null): string {
    return (id ?? '').toLowerCase();
  }

  private getStatusById(id?: string | null, fallback = ''): string {
    const nid = this.normalizeId(id);
    const col = columnsTemplate.find((c) => c.id === nid);
    return col?.title ?? fallback;
  }

  cancelCreate(): void {
    this.dialogRef.close();
  }
}
