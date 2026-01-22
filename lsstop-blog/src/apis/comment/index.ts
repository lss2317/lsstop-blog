// import http from '@/utils/http'

/** 回复类型 */
export interface ReplyVO {
  id: string
  avatar: string
  nickname: string
  webSite?: string
  userId: string
  location?: string
  createTime: string
  commentContent: string
  likeCount: number
  replyUserId?: string
  replyNickname?: string
}

/** 评论类型 */
export interface CommentVO {
  id: string
  avatar: string
  nickname: string
  webSite?: string
  userId: string
  location?: string
  createTime: string
  commentContent: string
  likeCount: number
  replyCount: number
  replyDTOList?: ReplyVO[]
}
