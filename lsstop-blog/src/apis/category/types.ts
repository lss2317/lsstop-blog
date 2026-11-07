/** 分类中的文章预览 */
export interface CategoryArticle {
  /** 文章ID */
  id: number;
  /** 文章标题 */
  articleTitle: string;
}

/** 分类信息 */
export interface Category {
  /** 分类ID */
  id: number;
  /** 分类名称 */
  categoryName: string;
  /** 文章数量 */
  articleCount: number;
  /** 最新文章 */
  articles: CategoryArticle[];
}
