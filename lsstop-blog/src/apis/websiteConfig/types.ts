/** 网站配置信息 */
export interface WebsiteConfigVo {
  /** 网站头像 */
  siteAvatar: string;
  /** 网站名称 */
  siteName: string;
  /** 网站作者 */
  siteAuthor: string;
  /** 网站介绍 */
  siteIntro: string;
  /** 关于我 */
  about: string;
  /** 网站创建时间 */
  siteStartTime: string;
  /** QQ链接 */
  qqUrl: string;
  /** GitHub链接 */
  githubUrl: string;
  /** Gitee链接 */
  giteeUrl: string;
  /** 用户默认头像 */
  defaultUserAvatar: string;
  /** websocket地址 */
  websocketUrl: string;
}

/** 访问统计 */
export interface VisitStats {
  /** 总访问量 */
  viewsCount: number;
}
