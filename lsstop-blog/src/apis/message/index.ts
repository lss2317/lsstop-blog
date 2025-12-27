import http from '@/utils/http.ts'

// 查询留言板列表
export function listMessage() {
  return http({
    url: '/message/listMessage',
    method: 'get',
  })
}

// 用户添加留言
export const addMessage = (data: object) => {
  return http.request({
    url: '/message/addMessage',
    method: 'post',
    data,
  })
}
