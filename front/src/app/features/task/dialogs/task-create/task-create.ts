import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import {
  MatChipsModule,
  MatChipEditedEvent,
  MatChipGrid,
  MatChipInputEvent,
  MatChipRow,
  MatChipInput,
} from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { UserService } from '../../../user/data-access/user.api';
import { User } from '../../../user/models/user';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../data-access/task.api';
import { TaskEventsService } from '../../data-access/task-events.service';
import { Tag } from '../../../tag/models/tag';
import { TaskCreateDto } from '../../models/task-create-dto';
import { TaskChangeUserAffecteeDTO } from '../../models/task-change-user-affectee-dto';
import { TaskChangeStatusDTO } from '../../models/task-change-status-dto';
import { TaskDeleteDto } from '../../models/task-delete-dto';
import { buildTaskCreateDTO } from '../../data-access/builders/task-create.builder';

@Component({
  selector: 'task-create',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    MatButtonModule,
    MatChipGrid,
    MatChipRow,
    MatIconModule,
    MatChipInput,
    MatChipsModule,
    FormsModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './task-create.html',
  styleUrl: './task-create.css',
})
export class TaskCreateDialog {
  private readonly taskEvents = inject(TaskEventsService);
  private readonly dialogRef = inject(MatDialogRef<TaskCreateDialog>);
  private readonly userService = inject(UserService);
  private readonly taskService = inject(TaskService);

  private readonly actor: string = 'AntoineActor';

  public readonly addOnBlur = true;
  public readonly separatorKeysCodes = [ENTER, COMMA] as const;
  public readonly tags = signal<Tag[]>([]);
  public readonly announcer = inject(LiveAnnouncer);

  private taskCreateDto: TaskCreateDto = buildTaskCreateDTO();

  public users: User[] = [];

  public description: string = '';
  public titre: string = '';
  public priority: number = 3;
  public utilisateurAffecte: string = '';

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUser().subscribe({
      next: (usersResponse) => {
        this.users = usersResponse;
      },
    });
  }

  createTask(): void {
    this.taskCreateDto = {
      actor: this.actor,
      title: this.titre,
      description: this.description,
      userAffectee: this.utilisateurAffecte,
      priority: this.priority,
      tags: this.tags(),
    };
    this.taskService.create(this.taskCreateDto).subscribe(() => {
      this.taskEvents.notifyRefresh();
      this.dialogRef.close();
    });
  }

  cancelCreate(): void {
    this.dialogRef.close();
  }

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (value) {
      this.tags.update((tags) => [...tags, { name: value }]);
    }
    event.chipInput!.clear();
  }

  remove(tag: Tag): void {
    this.tags.update((tags) => {
      const index = tags.indexOf(tag);
      if (index < 0) {
        return tags;
      }

      tags.splice(index, 1);
      this.announcer.announce(`Removed ${tag.name}`);
      return [...tags];
    });
  }

  edit(tag: Tag, event: MatChipEditedEvent) {
    const value = event.value.trim();
    if (!value) {
      this.remove(tag);
      return;
    }
    this.tags.update((tags) => {
      const index = tags.indexOf(tag);
      if (index >= 0) {
        tags[index].name = value;
        return [...tags];
      }
      return tags;
    });
  }
}
