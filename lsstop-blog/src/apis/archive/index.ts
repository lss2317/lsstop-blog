import http from '@/utils/http';
import type { ArticleArchive } from './types';

export * from './types';

/**
 * 获取文章归档列表
 */
export const listArchives = () => {
  return http.get<ArticleArchive[]>('/article/archives');
};
