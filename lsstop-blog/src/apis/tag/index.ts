import http from '@/utils/http.ts'

/** 标签信息 */
export interface Tag {
  /** 标签ID */
  id: number
  /** 标签名称 */
  tagName: string
  /** 文章数量 */
  articleCount: number
}

/** 获取标签列表 */
export function listTag() {
  return http.get<Tag[]>('/tag/listTag')
}
