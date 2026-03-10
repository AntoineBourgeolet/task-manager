import { TagCreateDto } from "../../models/tag-create-dto";

export function buildTagCreateDTO(
  overrides: Partial<TagCreateDto> = {},
): TagCreateDto {
  const defaults: TagCreateDto = {
    actor: '',
    name:''
  };

  return { ...defaults, ...overrides };
}
