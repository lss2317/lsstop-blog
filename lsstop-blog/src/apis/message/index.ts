import http from '@/utils/http.ts'

/** 留言信息 */
export interface MessageVo {
  /** 用户头像 */
  avatar: string
  /** 用户昵称 */
  nickname: string
  /** 留言内容 */
  messageContent: string
}

/** 添加留言请求参数 */
export interface AddMessageDto {
  /** 用户头像 */
  avatar: string
  /** 用户昵称 */
  nickname: string
  /** 留言内容 */
  messageContent: string
}

// 查询留言板列表
export function listMessage() {
  return http.get<MessageVo[]>('/message/listMessage')
}

// 用户添加留言
export const addMessage = (data: AddMessageDto) => {
  return http.post<null>('/message/addMessage', data)
}
