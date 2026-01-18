import http from '@/utils/http'

/** 归档文章 */
export interface ArticleArchive {
  /** 文章ID */
  id: number
  /** 文章标题 */
  articleTitle: string
  /** 发布时间 */
  createTime: string
}

/**
 * 获取文章归档列表
 */
export const listArchives = () => {
  return http.get<ArticleArchive[]>('/article/archives')
}
