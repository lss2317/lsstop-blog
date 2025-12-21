import http from '@/utils/http.ts'

// 查询友链列表
export function listMessage() {
  return http({
    url: '/message/blogListMessage',
    method: 'get',
  })
}
