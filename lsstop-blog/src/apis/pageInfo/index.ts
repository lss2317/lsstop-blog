import http from '@/utils/http.ts'

/** 页面信息 */
export interface PageInfoVo {
  /** 页面名称 */
  pageName: string
  /** 页面标签 */
  pageLabel: string
  /** 页面封面 */
  pageCover: string
}

// 获取页面列表
export function listPageInfo() {
  return http.get<PageInfoVo[]>('/pageInfo/listPageInfo')
}
