import http from '@/utils/http.ts';

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
  /** 是否开启聊天室(1:是、0:否) */
  enableChatRoom: number;
  /** 是否开启音乐播放器(1:是、0:否) */
  enableMusicPlayer: number;
}

// 获取网站配置信息
export function getWebsiteConfig() {
  return http.get<WebsiteConfigVo>('/websiteConfig/getWebsiteConfig');
}
