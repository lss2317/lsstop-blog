import http from '@/utils/http.ts'

// 获取友链列表
export function listFriendLink() {
  return http({
    url: '/friendLink/listFriendLink',
    method: 'get',
  })
}
