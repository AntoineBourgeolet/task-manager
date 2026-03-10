import { ColumnId } from "../../../../environments/type";
import { Tag } from "../../tag/models/tag";

export interface Task {
  id: number;
  title: string;
  description?: string;
  userAffectee?: string | null;
  priority?: number;
  tags?: Tag[] | null;
  status?: ColumnId;
}


