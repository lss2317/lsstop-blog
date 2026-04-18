import http from '@/utils/http.ts';
import type { UserInfo } from '@/stores/modules/userInfo';
import type {
  UserProfileInfo,
  UserPublicProfile,
  UpdateUserParams,
  UpdateEmailParams,
  UpdatePasswordParams,
  SocialType,
  UserRecentCommentVO,
} from './types';

export * from './types';

// 获取当前登录用户信息
export const getUserInfo = () => {
  return http.get<UserInfo>('/user/me');
};

/** 获取自己的主页详情 */
export const getUserProfile = () => {
  return http.get<UserProfileInfo>('/user/profile');
};

/** 获取指定用户的公开信息 */
export const getUserPublicProfile = (userId: string, showProgress = true) => {
  return http.get<UserPublicProfile>(`/user/profile/${userId}`, { showProgress });
};

/** 更新用户信息 */
export const updateUserInfo = (data: UpdateUserParams) => {
  return http.post<null>('/user/info', data, { showProgress: false });
};

/** 修改邮箱 */
export const updateEmail = (data: UpdateEmailParams) => {
  return http.post<null>('/user/email', data, { showProgress: false });
};

/** 修改密码 */
export const updatePassword = (data: UpdatePasswordParams) => {
  return http.post<null>('/user/password', data, { showProgress: false });
};

/** 解绑社交账号 */
export const unbindSocial = (type: SocialType) => {
  return http.post<null>(`/user/unbind/${type}`, null, { showProgress: false });
};

/** 绑定QQ */
export const bindQQ = (code: string) => {
  return http.post<null>('/user/bind/qq', { code }, { showProgress: false });
};

/** 绑定微博 */
export const bindWeibo = (code: string) => {
  return http.post<null>('/user/bind/weibo', { code }, { showProgress: false });
};

/** 更新用户头像 */
export const uploadAvatar = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return http.post<string>('/user/avatar', formData, { showProgress: false });
};

/** 获取用户最近评论 */
export const getUserRecentComments = (userId: string) => {
  return http.get<UserRecentCommentVO[]>(`/user/recentComments/${userId}`);
};
