import http from '@/utils/http.ts'

// 获取说说列表
export function listTalk() {
  return http({
    url: '/talk/listTalk',
    method: 'get',
  })
}

// 获取说说详细信息
export function getTalk(talkId: number) {
  return http({
    url: '/talk/getTalk',
    method: 'get',
    params: { talkId },
  })
}
