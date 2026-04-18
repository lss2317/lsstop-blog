/** 公告信息 */
export interface AnnouncementVo {
  /** 公告ID */
  id: number;
  /** 公告标题 */
  title: string;
  /** 公告内容 */
  content: string;
  /** 类型：1-弹窗公告 2-首页展示 3-两者都有 */
  type: number;
  /** 创建时间 */
  createTime: string;
}

/** 公告类型枚举 */
export const AnnouncementType = {
  /** 弹窗公告 */
  POPUP: 1,
  /** 首页展示 */
  HOME: 2,
  /** 两者都有 */
  BOTH: 3,
} as const;
