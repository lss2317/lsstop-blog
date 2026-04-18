import http from '@/utils/http';
import type { ArticleList, Article, ArticleHomePage, ArticleSearchItem } from './types';

export * from './types';

/** 根据分类ID获取文章列表 */
export function getArticleListByCategory(categoryId: number) {
  return http.get<ArticleList[]>(`/article/category/${categoryId}`);
}

/** 根据标签ID获取文章列表 */
export function getArticleListByTag(tagId: number) {
  return http.get<ArticleList[]>(`/article/tag/${tagId}`);
}

/** 根据ID获取文章详情 */
export function getArticleById(id: number) {
  return http.get<Article>(`/article/${id}`);
}

/** 获取主页文章列表 */
export function getArticleHomeList(current: number) {
  return http.get<ArticleHomePage>('/article/listArticleHome', { params: { current } });
}

/** 搜索文章（标题搜索） */
export function searchArticleByTitle(keyword: string) {
  return http.get<ArticleSearchItem[]>('/article/search/title', {
    params: { keyword },
    showProgress: false,
  });
}

/** 搜索文章（内容搜索） */
export function searchArticleByContent(keyword: string) {
  return http.get<ArticleSearchItem[]>('/article/search/content', {
    params: { keyword },
    showProgress: false,
  });
}
