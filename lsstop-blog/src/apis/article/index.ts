import http from '@/utils/http'

/** 简单标签信息 */
export interface TagSimpleVO {
  /** 标签ID */
  id: number
  /** 标签名称 */
  tagName: string
}

/** 文章列表项 */
export interface ArticleListVO {
  /** 文章ID */
  id: number
  /** 文章封面图URL */
  articleCover: string
  /** 文章标题 */
  articleTitle: string
  /** 分类ID */
  categoryId: number
  /** 分类名称 */
  categoryName: string
  /** 标签列表 */
  tags: TagSimpleVO[]
  /** 发表时间 */
  createTime: string
}

/** 根据分类ID获取文章列表 */
export function getArticleListByCategory(categoryId: number) {
  return http.get<ArticleListVO[]>(`/article/category/${categoryId}`)
}

/** 根据标签ID获取文章列表 */
export function getArticleListByTag(tagId: number) {
  return http.get<ArticleListVO[]>(`/article/tag/${tagId}`)
}
