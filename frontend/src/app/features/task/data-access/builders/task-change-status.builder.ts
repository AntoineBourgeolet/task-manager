import { TaskChangeStatusDTO } from '../../models/task-change-status-dto';

export function buildTaskChangeStatusDTO(
  overrides: Partial<TaskChangeStatusDTO> = {},
): TaskChangeStatusDTO {
  const defaults: TaskChangeStatusDTO = {
    actor: '',
    id: 0,
    newStatus: '',
  };

  return { ...defaults, ...overrides };
}
