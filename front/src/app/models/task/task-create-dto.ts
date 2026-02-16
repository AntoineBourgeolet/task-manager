import { Tag } from "../tag/tag";

export interface TaskCreateDto {
    actor: string;
    title: string;
    description?: string | null | undefined;
    userAffectee?: string | null | undefined;
    priority?: number | null | undefined;
    tags?: Tag[] | null | undefined;
}

