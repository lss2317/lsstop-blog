import http from '@/utils/http'

/** 回复类型 */
export interface Reply {
  id: number
  avatar: string
  nickname: string
  userId: string
  ipRegion: string
  createTime: string
  content: string
  likeCount: number
  replyUserId?: string
  replyNickname?: string
}

/** 评论类型 */
export interface Comment {
  id: number
  avatar: string
  nickname: string
  userId: string
  ipRegion: string
  createTime: string
  content: string
  likeCount: number
  replyCount: number
  replyList?: Reply[]
}

/** 评论列表分页VO */
export interface CommentPageVO {
  list: Comment[]
  total: number
}

/** 评论查询参数 */
export interface CommentQueryParams {
  type: number
  typeId?: string
  current: number
  sortType: string
}

/**
 * 获取评论列表
 */
export function getComments(params: CommentQueryParams) {
  return http.get<CommentPageVO>('/comment/listComment', { params })
}
