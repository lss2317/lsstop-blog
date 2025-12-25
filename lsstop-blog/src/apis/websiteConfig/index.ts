import http from '@/utils/http.ts'

// 获取网站配置信息
export function getWebsiteConfig() {
  return http({
    url: '/websiteConfig/getWebsiteConfig',
    method: 'get',
  })
}
