
export interface User {
  id: number;
  username: string;
  email?: string;
}

export interface UserCreateDto {
  actor: string;
  username: string;
  email?: string;
}
