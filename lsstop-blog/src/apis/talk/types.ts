/** 说说数据 */
export interface TalkItem {
  /** 说说ID */
  id: number;
  /** 用户ID */
  userId: string;
  /** 用户头像 */
  avatar: string;
  /** 用户昵称 */
  nickname: string;
  /** 创建时间 */
  createTime: string;
  /** 是否置顶：0否 1是 */
  isTop: number;
  /** 说说内容 */
  content: string;
  /** 图片列表 */
  imgList: string[] | null;
  /** 点赞数 */
  likeCount: number | null;
  /** 评论数 */
  commentCount: number | null;
}
