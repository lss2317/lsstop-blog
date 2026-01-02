export enum CommentTypeEnum {
  ARTICLE = 1,
  FRIEND_LINK = 2,
  TALK = 3
}

export const CommentTypeDesc: Record<CommentTypeEnum, string> = {
  [CommentTypeEnum.ARTICLE]: '文章',
  [CommentTypeEnum.FRIEND_LINK]: '友链',
  [CommentTypeEnum.TALK]: '说说'
}
