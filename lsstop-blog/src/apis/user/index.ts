import http from '@/utils/http.ts';
import type { UserInfo } from '@/stores/modules/userInfo';

// 获取当前登录用户信息
export const getUserInfo = () => {
  return http.get<UserInfo>('/user/me');
};

/** 用户主页详情 */
export interface UserProfileInfo {
  /** 用户id */
  userId: string;
  /** 昵称 */
  nickname: string;
  /** 头像 */
  avatar: string;
  /** 个人网站 */
  website: string | null;
  /** 个人简介 */
  intro: string | null;
  /** 邮箱（脱敏） */
  email: string | null;
  /** QQ绑定状态 */
  qqBound: boolean;
  /** 微博绑定状态 */
  weiboBound: boolean;
  /** 评论数量 */
  commentCount: number;
  /** 获赞数量 */
  likeCount: number;
  /** 注册时间 */
  createTime: string;
}

/** 获取自己的主页详情 */
export const getUserProfile = () => {
  return http.get<UserProfileInfo>('/user/profile');
};

/** 用户公开信息（去除敏感信息） */
export interface UserPublicProfile {
  /** 用户id */
  userId: string;
  /** 昵称 */
  nickname: string;
  /** 头像 */
  avatar: string;
  /** 个人网站 */
  website: string | null;
  /** 个人简介 */
  intro: string | null;
  /** 评论数量 */
  commentCount: number;
  /** 获赞数量 */
  likeCount: number;
  /** 注册时间 */
  createTime: string;
}

/** 获取指定用户的公开信息 */
export const getUserPublicProfile = (userId: string) => {
  return http.get<UserPublicProfile>(`/user/profile/${userId}`);
};

/** 更新用户信息参数 */
export interface UpdateUserParams {
  /** 昵称 */
  nickname: string;
  /** 个人网站 */
  website?: string;
  /** 个人简介 */
  intro?: string;
}

/** 更新用户信息 */
export const updateUserInfo = (data: UpdateUserParams) => {
  return http.post<null>('/user/info', data, { showProgress: false });
};

/** 修改邮箱参数 */
export interface UpdateEmailParams {
  /** 新邮箱 */
  newEmail: string;
  /** 验证码 */
  code: string;
}

/** 修改邮箱 */
export const updateEmail = (data: UpdateEmailParams) => {
  return http.post<null>('/user/email', data, { showProgress: false });
};

/** 修改密码参数 */
export interface UpdatePasswordParams {
  /** 旧密码 */
  oldPassword: string;
  /** 新密码 */
  newPassword: string;
}

/** 修改密码 */
export const updatePassword = (data: UpdatePasswordParams) => {
  return http.post<null>('/user/password', data, { showProgress: false });
};

/** 解绑社交账号类型 */
export type SocialType = 'qq' | 'weibo';

/** 解绑社交账号 */
export const unbindSocial = (type: SocialType) => {
  return http.delete<null>(`/user/social/${type}`, { showProgress: false });
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

/** 用户最近评论项 */
export interface UserRecentCommentVO {
  id: number;
  content: string;
  createTime: string;
  targetType: number;
  targetId: number;
  targetTitle: string | null;
}

/** 获取用户最近评论 */
export const getUserRecentComments = (userId: string) => {
  return http.get<UserRecentCommentVO[]>(`/user/recentComments/${userId}`);
};
