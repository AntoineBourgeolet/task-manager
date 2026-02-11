import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipGrid, MatChipInput, MatChipRow, MatChipsModule } from '@angular/material/chips';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { UserService } from '../../services/user/user.service';
import { TaskService } from '../../services/task/task.service';
import { User } from '../../models/user';
import { columnsTemplate, Tag, Task } from '../../models/task';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { CommonModule, LowerCasePipe } from '@angular/common';
import { ClipboardModule } from '@angular/cdk/clipboard';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-task-opened.component',
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
  templateUrl: './task-opened.component.html',
  styleUrl: './task-opened.component.css',
    changeDetection: ChangeDetectionStrategy.OnPush,

})
export class TaskOpenedComponent {

  columns = columnsTemplate;
  statusBinded = "";
  userService: UserService = inject(UserService);
  taskService: TaskService = inject(TaskService);

  task: Task = {id: "",title: "",description: "", priority: 1,userAffectee: "", status: 'todo', tags : []}
  users: User[] = [];
  readonly addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  readonly announcer = inject(LiveAnnouncer);
  constructor(

    @Inject(MAT_DIALOG_DATA) public data: { id: number },
  private dialogRef: MatDialogRef<TaskOpenedComponent>,
      private cdr: ChangeDetectorRef,

) {}


  ngOnInit(): void {
    this.loadUsers();
    this.loadTask();
  }

  loadUsers(): void {
    this.userService.getAllUser().subscribe({
      next: (usersResponse) => {
        this.users = usersResponse;
        console.log(this.users)
      },
    });
  }

  loadTask(): void{
    this.taskService.getTaskById(this.data.id).subscribe({
      next: (taskResponse) => {
        this.task = taskResponse;
        console.log(this.task.status);
        this.statusBinded = this.getStatusById(this.task.status);
        console.log(this.statusBinded);
        this.cdr.detectChanges();
      }
    })
  }

  

// Normalise l'ID en lowercase
private normalizeId(id?: string | null): string {
  return (id ?? '').toLowerCase(); // ou toLocaleLowerCase('fr-FR') si besoin
}

getStatusById(id?: string | null, fallback = ''): string {
  const nid = this.normalizeId(id);
  const col = this.columns.find(c => c.id === nid);
  return col?.title ?? fallback;
}



  cancelCreate(): void {
    this.dialogRef.close();
  }

  
}
