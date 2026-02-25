import { ColumnId } from "../../../../environments/type";
import { Tag } from "../../tag/models/tag";

export interface TaskPatchDto {
  actor: string;
  title: string | null | undefined;
  description: string | null | undefined;
  userAffectee: string | null | undefined;
  priority: number | null | undefined;
  tags: Tag[] | null | undefined;
  status: ColumnId | null | undefined;
}
