import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnInit,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipEditedEvent, MatChipInputEvent, MatChipsModule } from '@angular/material/chips';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UserService } from '../../../user/data-access/user.api';
import { TaskService } from '../../data-access/task.api';
import { User } from '../../../user/models/user';
import { Task } from '../../models/task';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { ENTER } from '@angular/cdk/keycodes';
import { ClipboardModule } from '@angular/cdk/clipboard';
import { MatTooltipModule } from '@angular/material/tooltip';
import { columnsTemplate } from '../../../../../environments/const';
import { buildTaskPathDTO } from '../../data-access/builders';
import { Tag } from '../../../tag/models/tag';
import { TagService } from '../../../tag/data-access/tag.api';
import { buildTagPatchDTO } from '../../../tag/data-access/builders';

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
    MatSnackBarModule,
  ],
  templateUrl: './task-opened.html',
  styleUrl: './task-opened.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskOpenedComponent implements OnInit {
  private readonly userService: UserService = inject(UserService);
  private readonly taskService: TaskService = inject(TaskService);
  private readonly tagService: TagService = inject(TagService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly snackBar = inject(MatSnackBar);

  public readonly announcer = inject(LiveAnnouncer);
  public readonly dialogRef = inject(MatDialogRef<TaskOpenedComponent>);

  private readonly idTask = this.dialogRef._containerInstance._config.data.id;
  private readonly actor: string = 'AntoineActor';

  private taskPatchDto = buildTaskPathDTO();
  private initialTaskState = '';
  private isSaving = false;

  public readonly addOnBlur = true;
  public readonly separatorKeysCodes = [ENTER] as const;

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
    this.registerCloseHandlers();
  }

  loadUsers(): void {
    this.userService.getAllUser().subscribe({
      next: (usersResponse) => {
        this.users = usersResponse;
      },
    });
  }

  loadTask(): void {
    this.taskService.getTaskById(this.idTask).subscribe({
      next: (taskResponse) => {
        this.task = taskResponse;
        this.resolveTaskTagsIds();
        this.statusBinded = this.getStatusById(this.task.status);
        this.initialTaskState = this.createTaskState();
        this.cdr.detectChanges();
      },
    });
  }

  private registerCloseHandlers(): void {
    this.dialogRef.backdropClick().subscribe(() => {
      this.closeAndSave();
    });

    this.dialogRef.keydownEvents().subscribe((event) => {
      if (event.key === 'Escape') {
        event.preventDefault();
        this.closeAndSave();
      }
    });
  }

  private resolveTaskTagsIds(): void {
    const tags = this.task.tags ?? [];
    const hasMissingIds = tags.some((tag) => tag?.id == null);

    if (!hasMissingIds) {
      return;
    }

    this.tagService.getAll().subscribe({
      next: (allTags) => {
        const byName = new Map(allTags.map((tag) => [tag.name.trim().toLowerCase(), tag]));

        this.task.tags = tags.map((tag) => {
          if (tag?.id != null) {
            return tag;
          }

          const key = (tag?.name ?? '').trim().toLowerCase();
          const resolved = byName.get(key);
          return resolved ?? tag;
        });

        this.initialTaskState = this.createTaskState();
        this.cdr.detectChanges();
      },
    });
  }

  private createTaskState(): string {
    return JSON.stringify({
      title: this.task.title,
      description: this.task.description ?? '',
      priority: this.task.priority ?? null,
      userAffectee: this.task.userAffectee ?? '',
      status: this.task.status ?? '',
      tags: (this.task.tags ?? []).map((tag) => ({
        id: tag.id ?? null,
        name: tag.name,
      })),
    });
  }

  private hasUnsavedChanges(): boolean {
    return this.createTaskState() !== this.initialTaskState;
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
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

  save(closeAfterSave = true): void {
    if (this.isSaving) {
      return;
    }

    this.taskPatchDto = buildTaskPathDTO({
      actor: this.actor,
      userAffectee: this.task.userAffectee,
      status: this.task.status,
      title: this.task.title,
      description: this.task.description,
      priority: this.task.priority,
      tags: this.task.tags as Tag[] | null,
    });
    this.isSaving = true;
    this.taskService.patch(this.idTask, this.taskPatchDto).subscribe(() => {
      this.initialTaskState = this.createTaskState();
      this.isSaving = false;
      this.showSuccess('Modification sauvegardee');
      if (closeAfterSave) {
        this.dialogRef.close(true);
      }
    });
  }

  closeAndSave(): void {
    if (!this.hasUnsavedChanges()) {
      this.dialogRef.close(false);
      return;
    }

    this.save();
  }

  addTag(event: MatChipInputEvent) {
    const rawValue = event.value ?? '';
    const name = rawValue.trim();

    event.chipInput?.clear();

    if (!name) {
      return; 
    }

    this.tagService.create({ actor: this.actor, name }).subscribe({
      next: (tagResponse) => {
        this.task.tags = [...(this.task.tags ?? []), tagResponse];
        this.cdr.detectChanges();
      },
      error: () => {
        // swallow or report error later
      }
    });
  }

  removeTag(tag: Tag): void {
    // remove from the current task; persisted when task is saved/autosaved
    this.task.tags = (this.task.tags ?? []).filter((t) => t !== tag);
    this.cdr.detectChanges();
  }

  editTag(tag: Tag, event: MatChipEditedEvent): void {
    const newName = event.value.trim();
    if (!newName || newName === tag.name) {
      return;
    }

    const dto = buildTagPatchDTO({ actor: this.actor, id: tag.id, name: newName.trim() });
    this.tagService.patch(dto).subscribe({
      next: (updated) => {
        tag.name = updated.name;
        this.cdr.detectChanges();
      },
    });
  }

  cancelCreate(): void {
    this.closeAndSave();
  }
}
