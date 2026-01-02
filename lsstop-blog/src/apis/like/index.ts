import http from '@/utils/http.ts'

/**
 * 点赞请求参数
 */
export interface LikeDto {
  // 用户id
  userId: string
  // 目标id（说说id/文章id/评论id）
  targetId: number
  // 点赞类型（1说说 2文章 3评论）
  type: number
}

/** 用户点赞数据响应 */
export interface UserLikeVo {
  talkLikeIds: number[]
  articleLikeIds: number[]
  commentLikeIds: number[]
}

// 点赞/取消点赞
export const toggleLike = (data: LikeDto) => {
  return http.request({
    url: '/like/toggle',
    method: 'post',
    data,
  })
}

// 获取用户点赞数据
export const getUserLike = (userId: string) => {
  return http.request<UserLikeVo>({
    url: `/like/userLike/${userId}`,
    method: 'get',
  })
}
