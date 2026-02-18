import { UserCreateDto } from '../../models/user-create.dto';

export function buildUserCreateDTO(
  overrides: Partial<UserCreateDto> = {},
): UserCreateDto {
  const defaults: UserCreateDto = {
    actor: '',
    username: '',
    email: ''
  };

  return { ...defaults, ...overrides };
}