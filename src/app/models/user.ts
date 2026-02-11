
export interface User {
  id: number;
  username: string;
  email?: string;
}

export interface UserCreateDto {
  username: string;
  email?: string;
}
