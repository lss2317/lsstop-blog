import http from '@/utils/http.ts';
import type { WebsiteConfigVo, VisitStats } from './types';

export * from './types';

// 获取网站配置信息
export function getWebsiteConfig() {
  return http.get<WebsiteConfigVo>('/websiteConfig/getWebsiteConfig');
}

// 上报访问并获取访问量
export function reportVisit() {
  return http.get<VisitStats>('/websiteConfig/visit/report');
}
