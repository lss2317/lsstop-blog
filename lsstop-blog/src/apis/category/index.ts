import http from '@/utils/http.ts';
import type { Category } from './types';

export * from './types';

/** 获取分类列表 */
export function listCategory() {
  return http.get<Category[]>('/category/listCategory');
}
