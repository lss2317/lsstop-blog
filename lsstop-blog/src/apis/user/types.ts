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
  /** IP所属地 */
  ipRegion: string;
}

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
  /** IP所属地 */
  ipRegion: string;
}

/** 更新用户信息参数 */
export interface UpdateUserParams {
  /** 昵称 */
  nickname: string;
  /** 个人网站 */
  website?: string;
  /** 个人简介 */
  intro?: string;
}

/** 修改邮箱参数 */
export interface UpdateEmailParams {
  /** 新邮箱 */
  newEmail: string;
  /** 验证码 */
  code: string;
}

/** 修改密码参数 */
export interface UpdatePasswordParams {
  /** 旧密码 */
  oldPassword: string;
  /** 新密码 */
  newPassword: string;
}

/** 解绑社交账号类型 */
export type SocialType = 'qq' | 'weibo';

/** 用户最近评论项 */
export interface UserRecentCommentVO {
  /** 评论ID */
  id: number;
  /** 评论内容 */
  content: string;
  /** 创建时间 */
  createTime: string;
  /** 评论目标类型 */
  targetType: number;
  /** 评论目标ID */
  targetId: number;
  /** 评论目标标题 */
  targetTitle: string | null;
}
