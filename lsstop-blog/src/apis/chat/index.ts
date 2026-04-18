import http from '@/utils/http';
import type { ChatMessage } from './types';

export * from './types';

/**
 * 获取历史消息（分页，每页50条）
 * @param lastId 上一页最后一条消息的 id，首次不传
 */
export function listMessage(lastId?: number) {
  return http.get<ChatMessage[]>('/chat/listMessage', {
    params: lastId != null ? { lastId } : undefined,
    showProgress: false,
  });
}

/**
 * 上传聊天图片
 * @param file 图片文件
 * @returns 图片 URL
 */
export function uploadChatImage(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return http.post<string>('/chat/image', formData, { showProgress: false });
}
