// 登录方式枚举
export enum LoginType {
  /** 邮箱密码登录 */
  EMAIL_PASSWORD = 1,
  /** QQ登录 */
  QQ = 2,
  /** 微博登录 */
  WEIBO = 3,
}

// 验证码类型枚举
export enum CodeType {
  /** 注册 */
  REGISTER = 1,
  /** 登录 */
  LOGIN = 2,
  /** 忘记密码 */
  FORGOT_PASSWORD = 3,
}

// 已注销用户默认信息
export const DEACTIVATED_USER = {
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
  nickname: '该用户已注销',
}
