/** 标签中的文章预览 */
export interface TagArticle {
  /** 文章ID */
  id: number;
  /** 文章标题 */
  articleTitle: string;
}

/** 标签信息 */
export interface Tag {
  /** 标签ID */
  id: number;
  /** 标签名称 */
  tagName: string;
  /** 文章数量 */
  articleCount: number;
  /** 最新文章 */
  articles: TagArticle[];
}
