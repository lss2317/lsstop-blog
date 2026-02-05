import http from '@/utils/http.ts';

/** 留言信息 */
export interface Message {
  /** 用户头像 */
  avatar: string;
  /** 用户昵称 */
  nickname: string;
  /** 留言内容 */
  messageContent: string;
}

/** 添加留言请求参数 */
export interface AddMessageParams {
  /** 用户头像 */
  avatar: string;
  /** 用户昵称 */
  nickname: string;
  /** 留言内容 */
  messageContent: string;
}

// 查询留言板列表
export function listMessage() {
  return http.get<Message[]>('/message/listMessage');
}

/** 添加留言返回结果 */
export interface AddMessageResult {
  /** 用户头像 */
  avatar: string;
  /** 用户昵称 */
  nickname: string;
  /** 留言内容 */
  messageContent: string;
  /** 审核状态（0-正常 1-待审核） */
  review: number;
}

// 用户添加留言
export const addMessage = (data: AddMessageParams) => {
  return http.post<AddMessageResult>('/message/addMessage', data);
};
