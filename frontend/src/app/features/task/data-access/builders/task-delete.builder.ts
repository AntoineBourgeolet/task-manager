import { TaskDeleteDto } from '../../models/task-delete-dto';

export function buildDeleteDto(overrides: Partial<TaskDeleteDto> = {}): TaskDeleteDto {
  const defaults: TaskDeleteDto = {
    actor: ''
    };

  return { ...defaults, ...overrides };
}
