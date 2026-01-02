export enum LikeTypeEnum {
  TALK = 1,
  ARTICLE = 2,
  COMMENT = 3
}

export const LikeTypeDesc: Record<LikeTypeEnum, string> = {
  [LikeTypeEnum.TALK]: '说说',
  [LikeTypeEnum.ARTICLE]: '文章',
  [LikeTypeEnum.COMMENT]: '评论'
}
