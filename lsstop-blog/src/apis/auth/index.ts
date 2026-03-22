import http from '@/utils/http.ts';
import type { UserInfo } from '@/stores/modules/userInfo';

// 登录请求参数
export interface LoginParams {
  /** 邮箱 */
  email: string;
  /** 密码 */
  password: string;
}

// 验证码登录请求参数
export interface CodeLoginParams {
  /** 邮箱 */
  email: string;
  /** 验证码 */
  code: string;
}

// 注册请求参数
export interface RegisterParams {
  /** 邮箱 */
  email: string;
  /** 密码 */
  password: string;
  /** 验证码 */
  code: string;
}

// 发送验证码请求参数
export interface SendCodeParams {
  /** 邮箱 */
  email: string;
  /** 验证码用途：1-登录 2-注册 3-重置密码 4-修改邮箱 */
  purpose: number;
}

/** 验证码用途枚举 */
export const CodePurpose = {
  LOGIN: 1,
  REGISTER: 2,
  RESET_PASSWORD: 3,
  UPDATE_EMAIL: 4,
} as const;

// 邮箱登录
export const emailLogin = (data: LoginParams) => {
  return http.post<UserInfo>('/auth/login/email', data, { showProgress: false });
};

// 验证码登录
export const emailCodeLogin = (data: CodeLoginParams) => {
  return http.post<UserInfo>('/auth/login/email-code', data, { showProgress: false });
};

// 用户注册
export const register = (data: RegisterParams) => {
  return http.post<UserInfo>('/auth/register', data, { showProgress: false });
};

// 重置密码请求参数
export interface ResetPasswordParams {
  /** 邮箱 */
  email: string;
  /** 验证码 */
  code: string;
  /** 新密码 */
  newPassword: string;
}

// 重置密码
export const resetPassword = (data: ResetPasswordParams) => {
  return http.post<null>('/auth/reset-password', data, { showProgress: false });
};

// 发送邮箱验证码
export const sendEmailCode = (data: SendCodeParams) => {
  return http.post<null>('/auth/code', data, { showProgress: false });
};

// 退出登录请求参数
export interface LogoutParams {
  /** 刷新令牌 */
  refreshToken: string;
}

// 退出登录
export const logout = (data: LogoutParams) => {
  return http.post<null>('/auth/logout', data, { showProgress: false });
};

// QQ登录请求参数
export interface QQLoginParams {
  /** QQ授权码 */
  code: string;
}

// QQ登录
export const qqLogin = (data: QQLoginParams) => {
  return http.post<UserInfo>('/auth/login/qq', data, { showProgress: false });
};

// 微博登录请求参数
export interface WeiboLoginParams {
  /** 微博授权码 */
  code: string;
}

// 微博登录
export const weiboLogin = (data: WeiboLoginParams) => {
  return http.post<UserInfo>('/auth/login/weibo', data, { showProgress: false });
};
