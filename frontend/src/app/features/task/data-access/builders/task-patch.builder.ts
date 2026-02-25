import { TaskPatchDto } from '../../models/task-patch-dto';

export function buildTaskPathDTO(
  overrides: Partial<TaskPatchDto> = {},
): TaskPatchDto {
  const defaults: TaskPatchDto = {
    actor: '',
    title: undefined,
    description: undefined,
    userAffectee: undefined,
    priority: undefined,
    tags: undefined,
    status: undefined
  };

  return { ...defaults, ...overrides };
}
