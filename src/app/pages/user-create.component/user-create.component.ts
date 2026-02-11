import { LiveAnnouncer } from '@angular/cdk/a11y';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { UserService } from '../../services/user/user.service';
import { UserCreateDto } from '../../models/user';
import { TaskEventsService } from '../../services/events/task-events/task-events.service';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatChipGrid, MatChipInput, MatChipRow, MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-user-create.component',
  imports: [MatFormFieldModule, MatInputModule, MatSelectModule, MatDialogModule, MatButtonModule, 
     MatIconModule, MatChipsModule,FormsModule],
  templateUrl: './user-create.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './user-create.component.css',
})
export class UserCreateComponent {

 userService: UserService = inject(UserService);

 constructor(
  private dialogRef: MatDialogRef<UserCreateComponent>,
  private taskEvents: TaskEventsService
) {}
  readonly addOnBlur = true;
  readonly announcer = inject(LiveAnnouncer);
  username: string = "";
  email: string = "";

userCreateDto: UserCreateDto = { username: '', email: '' };

  ngOnInit(): void {
    //Called after the constructor, initializing input properties, and the first call to ngOnChanges.
    //Add 'implements OnInit' to the class.
    
  }

  createTask(): void {
   this.userCreateDto = { username: this.username, email: this.email};
    this.userService.create(this.userCreateDto).subscribe(() => {
      
    this.taskEvents.notifyRefresh(); // 🔥 DÉCLENCHEUR

      this.dialogRef.close();
     });
  }

  cancelCreate(): void {
    this.dialogRef.close();
  }




}
