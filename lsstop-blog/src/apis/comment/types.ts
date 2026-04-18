/** 回复类型 */
export interface Reply {
  /** 回复ID */
  id: number;
  /** 用户头像 */
  avatar: string;
  /** 用户昵称 */
  nickname: string;
  /** 用户ID */
  userId: string;
  /** IP归属地 */
  ipRegion: string;
  /** 创建时间 */
  createTime: string;
  /** 回复内容 */
  content: string;
  /** 点赞数 */
  likeCount: number;
  /** 被回复用户ID */
  replyUserId?: string;
  /** 被回复用户昵称 */
  replyNickname?: string;
}

/** 评论类型 */
export interface Comment {
  /** 评论ID */
  id: number;
  /** 用户头像 */
  avatar: string;
  /** 用户昵称 */
  nickname: string;
  /** 用户ID */
  userId: string;
  /** IP归属地 */
  ipRegion: string;
  /** 创建时间 */
  createTime: string;
  /** 评论内容 */
  content: string;
  /** 点赞数 */
  likeCount: number;
  /** 回复数量 */
  replyCount: number;
  /** 回复列表 */
  replyList?: Reply[];
}

/** 评论列表分页VO */
export interface CommentPageVO {
  /** 评论列表 */
  list: Comment[];
  /** 评论总数 */
  total: number;
}

/** 评论查询参数 */
export interface CommentQueryParams {
  /** 评论类型 */
  type: number;
  /** 评论目标ID */
  typeId?: number;
  /** 当前页码 */
  current: number;
  /** 排序方式 */
  sortType: string;
}

/** 添加评论请求参数 */
export interface AddCommentParams {
  /** 评论目标类型 */
  targetType: number;
  /** 评论目标ID */
  targetId: number;
  /** 评论内容 */
  content: string;
  /** 回复的目标评论ID（可选，用于回复评论）*/
  parentId?: number;
  /** 回复的目标用户ID（可选，用于回复特定用户）*/
  replyUserId?: string;
}

/** 添加评论返回结果 */
export interface AddCommentResult {
  /** 评论ID */
  id: number;
  /** 用户ID */
  userId: string;
  /** 用户头像 */
  avatar: string;
  /** 用户昵称 */
  nickname: string;
  /** 评论内容 */
  content: string;
  /** IP归属地 */
  ipRegion: string;
  /** 审核状态（0-正常 1-待审核）*/
  review: number;
  /** 创建时间 */
  createTime: string;
}

/** 子评论查询参数 */
export interface ReplyQueryParams {
  /** 父评论ID */
  parentId: number;
  /** 当前页码 */
  current: number;
  /** 排序方式 */
  sortType: string;
}
