
export interface Task {
  id: number;
  title: string;
  description?: string;
  userAffectee?: string | null;
  priority?: number;
  tags?: string[] | null;
  status?: 'TODO' | 'BLOCKED' | 'DOING' | 'TESTING' | 'DONE';
}


export type ColumnId = 'TODO' | 'BLOCKED' | 'DOING' | 'TESTING' | 'DONE';
export type Board = Record<ColumnId, Task[]>;



export var columnsTemplate = [
    { id: 'TODO', title: 'À faire' },
    { id: 'BLOCKED', title: 'En attente' },
    { id: 'DOING', title: 'En cours' },
    { id: 'TESTING', title: 'En test' },
    { id: 'DONE', title: 'Terminé' }
  ] as const;