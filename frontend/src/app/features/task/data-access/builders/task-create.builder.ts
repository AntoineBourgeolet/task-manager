import { TaskCreateDto } from '../../models/task-create-dto';

export function buildTaskCreateDTO(
  overrides: Partial<TaskCreateDto> = {},
): TaskCreateDto {
  const defaults: TaskCreateDto = {
    actor: '',
    title: '',
    description: '',
    userAffectee: '',
    priority : 1,
    tags: [],
  };

  return { ...defaults, ...overrides };
}
