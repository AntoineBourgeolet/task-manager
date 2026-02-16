import { TaskDeleteDto } from '../../models/task-delete-dto';

export function buildDeleteDto(overrides: Partial<TaskDeleteDto> = {}): TaskDeleteDto {
  const defaults: TaskDeleteDto = {
    actor: '',
    id: 0,
  };

  return { ...defaults, ...overrides };
}
