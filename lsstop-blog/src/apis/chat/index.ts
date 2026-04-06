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
