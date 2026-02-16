import { ColumnId } from "../../../../environments/type";

export interface Task {
  id: number;
  title: string;
  description?: string;
  userAffectee?: string | null;
  priority?: number;
  tags?: string[] | null;
  status?: ColumnId;
}


