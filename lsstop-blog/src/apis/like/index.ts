import http from '@/utils/http.ts';
import type { LikeParams, UserLike } from './types';

export * from './types';

// 点赞/取消点赞
export const toggleLike = (data: LikeParams) => {
  return http.post<null>('/like/toggle', data, { showProgress: false });
};

// 获取用户点赞数据
export const getUserLike = (userId: string) => {
  return http.get<UserLike>(`/like/userLike/${userId}`);
};
