import http from '@/utils/http';
import type { ChatMessage } from '@/stores/modules/chat';

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
 * 发送消息（HTTP 持久化，不广播，WS 负责广播）
 */
export function sendMessage(data: { content?: string; images?: string[] }) {
  return http.post<void>('/chat/sendMessage', data);
}
