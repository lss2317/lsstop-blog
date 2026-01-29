import { defineStore } from 'pinia'
import { shallowRef } from 'vue'
import { getWebsiteConfig, type WebsiteConfigVo } from '@/apis/websiteConfig'

const defaultConfig: WebsiteConfigVo = {
  siteAvatar: '',
  siteName: '',
  siteAuthor: '',
  siteIntro: '',
  about: '',
  siteStartTime: '',
  qqUrl: '',
  githubUrl: '',
  giteeUrl: '',
  defaultUserAvatar: '',
  enableChatRoom: 0,
  enableMusicPlayer: 0,
}

const useWebsiteConfigStore = defineStore('websiteConfig', () => {
  const config = shallowRef<WebsiteConfigVo>({ ...defaultConfig })
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
