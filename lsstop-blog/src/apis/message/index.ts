import http from '@/utils/http.ts';
import type { Message, AddMessageParams, AddMessageResult } from './types';

export * from './types';

// 查询留言板列表
export function listMessage() {
  return http.get<Message[]>('/message/listMessage');
}

// 用户添加留言
export const addMessage = (data: AddMessageParams) => {
  return http.post<AddMessageResult>('/message/addMessage', data);
};
