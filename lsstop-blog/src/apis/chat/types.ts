/** 聊天消息（WS 下行不含 nickname/avatar，HTTP 历史接口含） */
export interface ChatMessage {
  /** 消息ID */
  id: number;
  /** 发送者ID */
  userId: string;
  /** 消息文本（可为空，纯图片消息时为 null） */
  content: string | null;
  /** 图片列表 */
  images: string[] | null;
  /** IP 属地 */
  ipRegion: string;
  /** 发送时间 yyyy-MM-ddTHH:mm:ss */
  createTime: string;
  /** 发送者昵称（仅历史消息接口返回） */
  nickname?: string;
  /** 发送者头像（仅历史消息接口返回） */
  avatar?: string;
}

/** 用户信息映射（nickname + avatar） */
export interface ChatUserInfo {
  /** 用户昵称 */
  nickname: string;
  /** 用户头像 */
  avatar: string;
}
