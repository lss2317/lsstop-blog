import http from '@/utils/http.ts'
import type { UserInfo } from '@/stores/modules/userInfo'

// 登录请求参数
export interface LoginParams {
  /** 登录方式标识符（邮箱） */
  identifier: string
  /** 登录凭证（密码/验证码等） */
  credential: string
  /** 登录方式：1邮箱密码 2QQ 3微博 */
  loginType: number
}

// 注册请求参数
export interface RegisterParams {
  /** 邮箱 */
  email: string
  /** 密码 */
  password: string
  /** 验证码 */
  code: string
}

// 忘记密码请求参数
export interface ForgotPasswordParams {
  /** 邮箱 */
  email: string
  /** 验证码 */
  code: string
  /** 新密码 */
  newPassword: string
}

// 发送验证码请求参数
export interface SendCodeParams {
  /** 邮箱 */
  email: string
  /** 验证码类型：1注册 2登录 3忘记密码 */
  type: number
}

// 用户登录
export const login = (data: LoginParams) => {
  return http.post<UserInfo>('/auth/login', data)
}

// 用户注册
export const register = (data: RegisterParams) => {
  return http.post<null>('/auth/register', data)
}

// 忘记密码
export const forgotPassword = (data: ForgotPasswordParams) => {
  return http.post<null>('/auth/forgotPassword', data)
}

// 发送邮箱验证码
export const sendEmailCode = (data: SendCodeParams) => {
  return http.post<null>('/auth/sendCode', data)
}

// 退出登录
export const logout = () => {
  return http.post<null>('/auth/logout')
}
