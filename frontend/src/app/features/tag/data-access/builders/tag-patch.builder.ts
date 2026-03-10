import { TagPatchDto } from "../../models/tag-patch-dto";

export function buildTagPatchDTO(
  overrides: Partial<TagPatchDto> = {},
): TagPatchDto {
  const defaults: TagPatchDto = {
    actor: '',
    id: 0,
    name: '',
  };

  return { ...defaults, ...overrides };
}
