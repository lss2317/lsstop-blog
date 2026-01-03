import http from '@/utils/http.ts'

/** 友链信息 */
export interface FriendLinkVo {
  /** 友链名称 */
  linkName: string
  /** 友链头像 */
  linkAvatar: string
  /** 友链地址 */
  linkAddress: string
  /** 友链介绍 */
  linkIntro: string
}

// 获取友链列表
export function listFriendLink() {
  return http.get<FriendLinkVo[]>('/friendLink/listFriendLink')
}
