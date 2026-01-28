
export interface Task {
  id: number | string;
  title: string;
  description?: string;
  userAffectee?: string | null;
  priority?: number;
  tags?: string[] | null;
  status?: 'todo' | 'blocked' | 'doing' | 'testing' | 'done';
}

export type ColumnId = 'todo' | 'blocked' | 'doing' | 'testing' | 'done';
export type Board = Record<ColumnId, Task[]>;
