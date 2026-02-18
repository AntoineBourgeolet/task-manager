import { Task } from "../app/features/task/models/task";

export type ColumnId = 'TODO' | 'BLOCKED' | 'DOING' | 'TESTING' | 'DONE';
export type Board = Record<string, Task[]>;
