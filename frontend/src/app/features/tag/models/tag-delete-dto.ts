export interface TagDeleteDto {
  actor: string;
  /**
   * id of the tag to delete.  backend expects this in the request body
   */
  id: number;
}
