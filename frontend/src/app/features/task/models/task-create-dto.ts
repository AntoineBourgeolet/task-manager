import { TagCreateDto } from '../../tag/models/tag-create-dto';

export interface TaskCreateDto {
  actor: string;
  title: string;
  description: string | null | undefined;
  userAffectee: string | null | undefined;
  priority: number | null | undefined;
  tags: TagCreateDto[] | null | undefined;
}
