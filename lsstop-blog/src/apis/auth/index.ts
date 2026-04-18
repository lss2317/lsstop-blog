import http from '@/utils/http.ts';
import type { UserInfo } from '@/stores/modules/userInfo';
import type {
  LoginParams,
  CodeLoginParams,
  RegisterParams,
  SendCodeParams,
  ResetPasswordParams,
  LogoutParams,
  QQLoginParams,
  WeiboLoginParams,
} from './types';

export * from './types';

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

// 重置密码
export const resetPassword = (data: ResetPasswordParams) => {
  return http.post<null>('/auth/reset-password', data, { showProgress: false });
};

// 发送邮箱验证码
export const sendEmailCode = (data: SendCodeParams) => {
  return http.post<null>('/auth/code', data, { showProgress: false });
};

// 退出登录
export const logout = (data: LogoutParams) => {
  return http.post<null>('/auth/logout', data, { showProgress: false });
};

// QQ登录
export const qqLogin = (data: QQLoginParams) => {
  return http.post<UserInfo>('/auth/login/qq', data, { showProgress: false });
};

// 微博登录
export const weiboLogin = (data: WeiboLoginParams) => {
  return http.post<UserInfo>('/auth/login/weibo', data, { showProgress: false });
};
