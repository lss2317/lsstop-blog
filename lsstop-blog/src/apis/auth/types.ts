/** 登录请求参数 */
export interface LoginParams {
  /** 邮箱 */
  email: string;
  /** 密码 */
  password: string;
}

/** 验证码登录请求参数 */
export interface CodeLoginParams {
  /** 邮箱 */
  email: string;
  /** 验证码 */
  code: string;
}

/** 注册请求参数 */
export interface RegisterParams {
  /** 邮箱 */
  email: string;
  /** 密码 */
  password: string;
  /** 验证码 */
  code: string;
}

/** 发送验证码请求参数 */
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

/** 重置密码请求参数 */
export interface ResetPasswordParams {
  /** 邮箱 */
  email: string;
  /** 验证码 */
  code: string;
  /** 新密码 */
  newPassword: string;
}

/** 退出登录请求参数 */
export interface LogoutParams {
  /** 刷新令牌 */
  refreshToken: string;
}

/** QQ登录请求参数 */
export interface QQLoginParams {
  /** QQ授权码 */
  code: string;
}

/** 微博登录请求参数 */
export interface WeiboLoginParams {
  /** 微博授权码 */
  code: string;
}
