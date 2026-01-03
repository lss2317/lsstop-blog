import http from '@/utils/http.ts'

/** 网站配置信息 */
export interface WebsiteConfigVo {
  /** 网站头像 */
  websiteAvatar: string
  /** 网站名称 */
  websiteName: string
  /** 网站作者 */
  websiteAuthor: string
  /** 网站介绍 */
  websiteIntro: string
  /** 网站公告 */
  websiteNotice: string
  /** 网站创建时间 */
  websiteCreateTime: string
  /** 网站备案号 */
  websiteRecordNo: string
  /** QQ号 */
  qq: string
  /** GitHub地址 */
  github: string
  /** Gitee地址 */
  gitee: string
  /** 游客头像 */
  touristAvatar: string
  /** 用户默认头像 */
  userAvatar: string
  /** 评论审核 */
  commentReview: number
  /** 邮件提醒 */
  emailNotice: number
  /** 打赏状态 */
  reward: number
  /** 微信二维码 */
  weixinQrcode: string
  /** 支付宝二维码 */
  alipayQrcode: string
  /** 聊天室状态 */
  chatRoom: number
  /** 音乐播放器状态 */
  musicPlayer: number
  /** WebSocket地址 */
  websocketUrl: string
  /** 关于信息 */
  about: string
}

// 获取网站配置信息
export function getWebsiteConfig() {
  return http.get<WebsiteConfigVo>('/websiteConfig/getWebsiteConfig')
}
