import http from '@/utils/http';

/** 简单标签信息 */
export interface TagSimple {
  /** 标签ID */
  id: number;
  /** 标签名称 */
  tagName: string;
}

/** 文章列表项 */
export interface ArticleList {
  /** 文章ID */
  id: number;
  /** 文章封面图URL */
  articleCover: string;
  /** 文章标题 */
  articleTitle: string;
  /** 分类ID */
  categoryId: number;
  /** 分类名称 */
  categoryName: string;
  /** 标签列表 */
  tags: TagSimple[];
  /** 发表时间 */
  createTime: string;
}

/** 根据分类ID获取文章列表 */
export function getArticleListByCategory(categoryId: number) {
  return http.get<ArticleList[]>(`/article/category/${categoryId}`);
}

/** 根据标签ID获取文章列表 */
export function getArticleListByTag(tagId: number) {
  return http.get<ArticleList[]>(`/article/tag/${tagId}`);
}

/** 简化文章（用于上一篇/下一篇/最新/推荐文章） */
export interface ArticleSimple {
  /** 文章ID */
  id: number;
  /** 文章封面图URL */
  articleCover: string;
  /** 文章标题 */
  articleTitle: string;
  /** 创建时间 */
  createTime: string;
}

/** 文章详情 */
export interface Article {
  /** 文章ID */
  id: number;
  /** 文章封面图URL */
  articleCover: string;
  /** 文章标题 */
  articleTitle: string;
  /** 文章内容 */
  articleContent: string;
  /** 分类ID */
  categoryId: number;
  /** 分类名称 */
  categoryName: string;
  /** 标签列表 */
  tags: TagSimple[];
  /** 浏览量 */
  viewCount: number;
  /** 点赞数 */
  likeCount: number;
  /** 创建时间 */
  createTime: string;
  /** 更新时间 */
  updateTime: string;
  /** 上一篇文章 */
  preArticle: ArticleSimple | null;
  /** 下一篇文章 */
  nextArticle: ArticleSimple | null;
  /** 最新文章列表 */
  newestArticles: ArticleSimple[];
  /** 推荐文章列表 */
  recommendArticles: ArticleSimple[];
}

/** 根据ID获取文章详情 */
export function getArticleById(id: number) {
  return http.get<Article>(`/article/${id}`);
}
