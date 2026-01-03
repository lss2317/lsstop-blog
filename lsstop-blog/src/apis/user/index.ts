import http from '@/utils/http.ts'
import type { UserInfo } from '@/stores/modules/userInfo'

// 获取用户信息
export const getUserInfo = () => {
  return http.get<UserInfo>('/user/info')
}
