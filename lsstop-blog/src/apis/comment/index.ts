import http from '@/utils/http';

/** 回复类型 */
export interface Reply {
  id: number;
  avatar: string;
  nickname: string;
  userId: string;
  ipRegion: string;
  createTime: string;
  content: string;
  likeCount: number;
  replyUserId?: string;
  replyNickname?: string;
}

/** 评论类型 */
export interface Comment {
  id: number;
  avatar: string;
  nickname: string;
  userId: string;
  ipRegion: string;
  createTime: string;
  content: string;
  likeCount: number;
  replyCount: number;
  replyList?: Reply[];
}

/** 评论列表分页VO */
export interface CommentPageVO {
  list: Comment[];
  total: number;
}

/** 评论查询参数 */
export interface CommentQueryParams {
  type: number;
  typeId?: number;
  current: number;
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

/**
 * 获取评论列表
 */
export function getComments(params: CommentQueryParams) {
  return http.get<CommentPageVO>('/comment/listComment', { params });
}

/**
 * 添加评论或回复评论
 * 当parentId为null或undefined时，添加主评论
 * 当parentId存在时，回复对应评论
 */
export function addComment(data: AddCommentParams) {
  return http.post<AddCommentResult>('/comment/addComment', data, { showProgress: false });
}

/** 子评论查询参数 */
export interface ReplyQueryParams {
  parentId: number;
  current: number;
  sortType: string;
}

/**
 * 获取子评论列表
 */
export function getReplyList(params: ReplyQueryParams) {
  return http.get<Reply[]>('/comment/listReply', { params });
}
