import { LiveAnnouncer } from '@angular/cdk/a11y';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { UserService } from '../../data-access/user.api';
import { UserCreateDto } from '../../models/user-create.dto';
import { TaskEventsService } from '../../../task/data-access/task-events.service';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { buildUserCreateDTO } from '../../data-access/builders';

@Component({
  selector: 'user-create',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    FormsModule,
  ],
  templateUrl: './user-create.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './user-create.css',
})
export class UserCreateComponent {
  private readonly userService: UserService = inject(UserService);
  private readonly dialogRef = inject(MatDialogRef<UserCreateComponent>);
  private readonly taskEvents = inject(TaskEventsService);

  public readonly addOnBlur = true;
  public readonly announcer = inject(LiveAnnouncer);

  public username: string = '';
  public email: string = '';
  public actor: string = 'AntoineActor';

  userCreateDto: UserCreateDto = buildUserCreateDTO();


  createUser(): void {
    this.userCreateDto = { actor: this.actor, username: this.username, email: this.email };
    this.userService.create(this.userCreateDto).subscribe(() => {
      this.taskEvents.notifyRefresh();
      this.dialogRef.close();
    });
  }

  cancelCreate(): void {
    this.dialogRef.close();
  }
}
