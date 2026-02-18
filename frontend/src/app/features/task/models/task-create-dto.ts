import { Tag } from '../../tag/models/tag';

export interface TaskCreateDto {
  actor: string;
  title: string;
  description: string | null | undefined;
  userAffectee: string | null | undefined;
  priority: number | null | undefined;
  tags: Tag[] | null | undefined;
}
