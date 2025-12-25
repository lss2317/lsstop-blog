import { defineStore } from 'pinia'
import { shallowRef } from 'vue'
import { getWebsiteConfig } from '@/apis/websiteConfig'

interface WebsiteConfig {
  websiteAvatar: string
  websiteName: string
  websiteAuthor: string
  websiteIntro: string
  websiteNotice: string
  websiteCreateTime: string
  websiteRecordNo: string
  qq: string
  github: string
  gitee: string
  touristAvatar: string
  userAvatar: string
  commentReview: number
  emailNotice: number
  reward: number
  weixinQrcode: string
  alipayQrcode: string
  chatRoom: number
  musicPlayer: number
  websocketUrl: string
  about: string
}

const defaultConfig: WebsiteConfig = {
  websiteAvatar: '',
  websiteName: '',
  websiteAuthor: '',
  websiteIntro: '',
  websiteNotice: '',
  websiteCreateTime: '',
  websiteRecordNo: '',
  qq: '',
  github: '',
  gitee: '',
  touristAvatar: '',
  userAvatar: '',
  commentReview: 0,
  emailNotice: 0,
  reward: 0,
  weixinQrcode: '',
  alipayQrcode: '',
  chatRoom: 0,
  musicPlayer: 0,
  websocketUrl: '',
  about: '',
}

const useWebsiteConfigStore = defineStore('websiteConfig', () => {
  const config = shallowRef<WebsiteConfig>({ ...defaultConfig })
  const isLoaded = shallowRef(false)

  // 获取网站配置（已有数据则不重复请求）
  async function fetchWebsiteConfig() {
    if (isLoaded.value) return
    const res = await getWebsiteConfig()
    config.value = res.data
    isLoaded.value = true
  }

  return {
    config,
    isLoaded,
    fetchWebsiteConfig,
  }
})

export default useWebsiteConfigStore
