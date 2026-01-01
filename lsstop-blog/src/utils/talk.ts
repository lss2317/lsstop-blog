import { DEACTIVATED_USER } from '@/constants/user'

// 说说数据接口
export interface TalkItem {
  id: number
  avatar: string
  nickname: string
  createTime: string
  isTop: number
  content: string
  imgList: string[] | null
  likeCount: number | null
  commentCount: number | null
}

// 判断用户是否已注销
export function isUserDeactivated(item: TalkItem): boolean {
  return !item.nickname || !item.avatar
}

// 获取用户头像（已注销用户使用默认头像）
export function getUserAvatar(item: TalkItem): string {
  return item.avatar || DEACTIVATED_USER.avatar
}

// 获取用户昵称（已注销用户使用默认昵称）
export function getUserNickname(item: TalkItem): string {
  return item.nickname || DEACTIVATED_USER.nickname
}
