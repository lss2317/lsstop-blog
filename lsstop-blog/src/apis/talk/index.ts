import http from '@/utils/http.ts';
import type { TalkItem } from '@/utils/talk';

// 获取说说列表
export function listTalk() {
  return http.get<TalkItem[]>('/talk/listTalk');
}

// 获取说说详细信息
export function getTalk(talkId: number) {
  return http.get<TalkItem>('/talk/getTalk', { params: { talkId } });
}
