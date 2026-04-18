import http from '@/utils/http.ts';
import type { AnnouncementVo } from './types';

export * from './types';

// 获取公告列表
export function listAnnouncement() {
  return http.get<AnnouncementVo[]>('/announcement/listAnnouncement');
}
