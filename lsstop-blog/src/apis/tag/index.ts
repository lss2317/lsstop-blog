import http from '@/utils/http.ts';
import type { Tag } from './types';

export * from './types';

/** 获取标签列表 */
export function listTag() {
  return http.get<Tag[]>('/tag/listTag');
}
