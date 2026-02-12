import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule, MatChipEditedEvent, MatChipGrid, MatChipInputEvent, MatChipRow, MatChipInput } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { UserService } from '../../services/user/user.service';
import { User } from '../../models/user/user';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task/task.service';
import { TaskEventsService } from '../../services/events/task-events/task-events.service';
import { Tag } from '../../models/tag/tag';
import { TaskCreateDto } from '../../models/task/task-create-dto';
import { TaskChangeUserAffecteeDTO } from '../../models/task/task-change-user-affectee-dto';
import { TaskChangeStatusDTO } from '../../models/task/task-change-status-dto';
import { TaskDeleteDto } from '../../models/task/task-delete-dto';

@Component({
  selector: 'app-task-create',
  imports: [MatFormFieldModule, MatInputModule, MatSelectModule, MatDialogModule, MatButtonModule, MatChipGrid,
    MatChipRow, MatIconModule, MatChipInput, MatChipsModule,FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './task-create.html',
  styleUrl: './task-create.css',
})
export class TaskCreate {


constructor(
  private dialogRef: MatDialogRef<TaskCreate>,
  private taskEvents: TaskEventsService
) {}


  userService: UserService = inject(UserService);
  taskService: TaskService = inject(TaskService);

  actor: string = "AntoineActor";
  users: User[] = [];
  readonly addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  readonly tags = signal<Tag[]>([]);
  readonly announcer = inject(LiveAnnouncer);
  description: string = "";
  titre: string = "";
  priority: number = 3;
  utilisateurAffecte: string = "";

taskCreateDto: TaskCreateDto = { actor: 'AntoineActor', title: '', description: '', userAffectee: null, priority: 1, tags: [] };


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
   this.taskCreateDto = { actor: this.actor, title: this.titre, description: this.description, userAffectee: this.utilisateurAffecte, priority: this.priority, tags: this.tags() };
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

    // Add our tag
    if (value) {
      this.tags.update(tags => [...tags, { name: value }]);
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  remove(tag: Tag): void {
    this.tags.update(tags => {
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

    // Remove tag if it no longer has a name
    if (!value) {
      this.remove(tag);
      return;
    }

    // Edit existing tag
    this.tags.update(tags => {
      const index = tags.indexOf(tag);
      if (index >= 0) {
        tags[index].name = value;
        return [...tags];
      }
      return tags;
    });
  }


}
