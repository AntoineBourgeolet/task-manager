import { TagDeleteDto } from "../../models/tag-delete-dto";

export function buildTagDeleteDTO(
  overrides: Partial<TagDeleteDto> = {},
): TagDeleteDto {
  const defaults: TagDeleteDto = {
    actor: '',
    id: 0,
  };

  return { ...defaults, ...overrides };
}
