import http from '@/utils/http.ts'

// 获取页面列表
export function listPageInfo() {
  return http({
    url: '/pageInfo/listPageInfo',
    method: 'get',
  })
}
