import { ChangeDetectorRef, Component, inject, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterOutlet } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { TaskCreateDialog } from './features/task/dialogs/task-create/task-create';
import { take } from 'rxjs';
import { TaskService } from './features/task/data-access/task.api';
import { UserCreateComponent } from './features/user/dialogs/user-create/user-create';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatIconModule, MatButtonModule, MatToolbarModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('task-manager-front');

  private readonly dialog = inject(MatDialog);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly taskService = inject(TaskService);

  createTask(): void {
    const dialogRef = this.dialog.open(TaskCreateDialog, {
      width: '600px',
    });

    dialogRef
      .afterClosed()
      .pipe(take(1))
      .subscribe((result) => {
        if (result) {
          this.cdr.markForCheck();
        }
      });
  }

  createUser(): void {
    const dialogRef = this.dialog.open(UserCreateComponent, {
      width: '600px',
    });

    dialogRef
      .afterClosed()
      .pipe(take(1))
      .subscribe((result) => {
        if (result) {
          this.cdr.markForCheck();
        }
      });
  }
}
