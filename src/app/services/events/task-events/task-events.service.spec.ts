import { TestBed } from '@angular/core/testing';

import { TaskEventsService } from './task-events.service';

describe('TaskEventsService', () => {
  let service: TaskEventsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaskEventsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
