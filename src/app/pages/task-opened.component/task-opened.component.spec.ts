import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskOpenedComponent } from './task-opened.component';

describe('TaskOpenedComponent', () => {
  let component: TaskOpenedComponent;
  let fixture: ComponentFixture<TaskOpenedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskOpenedComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TaskOpenedComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
