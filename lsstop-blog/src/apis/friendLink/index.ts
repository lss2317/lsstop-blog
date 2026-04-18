import http from '@/utils/http.ts';
import type { FriendLink } from './types';

export * from './types';

// 获取友链列表
export function listFriendLink() {
  return http.get<FriendLink[]>('/friendLink/listFriendLink');
}
