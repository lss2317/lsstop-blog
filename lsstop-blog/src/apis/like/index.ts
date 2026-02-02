import http from '@/utils/http.ts'

/**
 * 点赞请求参数
 */
export interface LikeParams {
  /** 目标id（说说id/文章id/评论id） */
  targetId: number
  /** 点赞类型（1说说 2文章 3评论） */
  type: number
}

/** 用户点赞数据响应 */
export interface UserLike {
  /** 点赞的说说ID列表 */
  talkLikeIds: number[]
  /** 点赞的文章ID列表 */
  articleLikeIds: number[]
  /** 点赞的评论ID列表 */
  commentLikeIds: number[]
}

// 点赞/取消点赞
export const toggleLike = (data: LikeParams) => {
  return http.post<null>('/like/toggle', data, { showProgress: false })
}

// 获取用户点赞数据
export const getUserLike = (userId: number) => {
  return http.get<UserLike>(`/like/userLike/${userId}`)
}
