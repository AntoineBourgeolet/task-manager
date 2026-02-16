import { ChangeDetectorRef, Component, inject, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterOutlet } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { TaskCreate } from './pages/task-create/task-create';
import { take } from 'rxjs';
import { TaskService } from './services/task/task.service';
import { routes } from './app.routes';
import { UserCreateDto } from './models/user/user';
import { UserCreateComponent } from './pages/user-create.component/user-create.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,
    MatIconModule,
    MatButtonModule,
    MatToolbarModule
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('task-manager-front');


  constructor(private dialog: MatDialog, private cdr: ChangeDetectorRef) { }

  taskService: TaskService = inject(TaskService);


  createTask(): void {
    const dialogRef = this.dialog.open(TaskCreate
      , {
        width: '600px'
      });


    dialogRef.afterClosed().pipe(take(1)).subscribe((result) => {
      if (result) {
        this.reloadTasks();

        this.cdr.markForCheck();
      }
    });
  }

  createUser(): void {
    const dialogRef = this.dialog.open(UserCreateComponent
      , {
        width: '600px'
      });


    dialogRef.afterClosed().pipe(take(1)).subscribe((result) => {
      if (result) {
        this.reloadTasks();

        this.cdr.markForCheck();
      }
    });

  }


  private reloadTasks(): void {

  }

}
