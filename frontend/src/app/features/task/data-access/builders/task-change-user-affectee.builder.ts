import { TaskChangeUserAffecteeDTO } from '../../models/task-change-user-affectee-dto';

export function buildTaskChangeUserAffecteeDTO(
  overrides: Partial<TaskChangeUserAffecteeDTO> = {},
): TaskChangeUserAffecteeDTO {
  const defaults: TaskChangeUserAffecteeDTO = {
    actor: '',
    id: 0,
    newUser: '',
  };

  return { ...defaults, ...overrides };
}
