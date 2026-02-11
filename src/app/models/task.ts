
export interface Task {
  id: number | string;
  title: string;
  description?: string;
  userAffectee?: string | null;
  priority?: number;
  tags?: string[] | null;
  status?: 'todo' | 'blocked' | 'doing' | 'testing' | 'done';
}

export interface TaskCreateDto {
  title: string;
  description?: string;
  userAffectee?: string | null;
  priority?: number;
  tags?: Tag[] | null;
}


export type ColumnId = 'todo' | 'blocked' | 'doing' | 'testing' | 'done';
export type Board = Record<ColumnId, Task[]>;

export interface Tag {
  name: string;
}

export var columnsTemplate = [
    { id: 'todo', title: 'À faire' },
    { id: 'blocked', title: 'En attente' },
    { id: 'doing', title: 'En cours' },
    { id: 'testing', title: 'En test' },
    { id: 'done', title: 'Terminé' }
  ] as const;