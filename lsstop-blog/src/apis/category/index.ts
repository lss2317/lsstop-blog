import http from '@/utils/http.ts'

/** 分类信息 */
export interface Category {
  /** 分类ID */
  id: number
  /** 分类名称 */
  categoryName: string
  /** 文章数量 */
  articleCount: number
}

/** 获取分类列表 */
export function listCategory() {
  return http.get<Category[]>('/category/listCategory')
}
