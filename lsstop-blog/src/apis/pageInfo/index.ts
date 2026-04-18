import http from '@/utils/http.ts';
import type { PageInfoVo } from './types';

export * from './types';

// 获取页面列表
export function listPageInfo() {
  return http.get<PageInfoVo[]>('/pageInfo/listPageInfo');
}
