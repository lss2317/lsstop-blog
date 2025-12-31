import http from '@/utils/http.ts'

// 获取说说列表
export function listTalk() {
  return http({
    url: '/talk/listTalk',
    method: 'get',
  })
}
