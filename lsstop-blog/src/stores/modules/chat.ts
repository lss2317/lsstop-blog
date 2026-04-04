import { defineStore } from 'pinia';
import { shallowRef, computed } from 'vue';

export interface ChatMessage {
  /** 消息ID */
  id: string;
  /** 发送者ID */
  userId: string;
  /** 发送者昵称 */
  nickname: string;
  /** 发送者头像 */
  avatar: string;
  /** 消息内容 */
  content: string;
  /** 发送时间 */
  createTime: string;
}

export interface OnlineUser {
  /** 用户ID */
  userId: string;
  /** 昵称 */
  nickname: string;
  /** 头像 */
  avatar: string;
}

const useChatStore = defineStore('chat', () => {
  // 消息列表
  const messages = shallowRef<ChatMessage[]>([]);
  // 在线用户列表
  const onlineUsers = shallowRef<OnlineUser[]>([]);
  // 聊天室是否打开
  const isOpen = shallowRef(false);
  // 未读消息数
  const unreadCount = shallowRef(0);

  // 在线人数
  const onlineCount = computed(() => onlineUsers.value.length);

  // 打开聊天室
  function open() {
    isOpen.value = true;
    unreadCount.value = 0;
  }

  // 关闭聊天室
  function close() {
    isOpen.value = false;
  }

  // 添加消息
  function addMessage(message: ChatMessage) {
    messages.value = [...messages.value, message];
    // 聊天室未打开时增加未读数
    if (!isOpen.value) {
      unreadCount.value++;
    }
  }

  // 设置消息列表（用于初始化历史消息）
  function setMessages(list: ChatMessage[]) {
    messages.value = list;
  }

  // 设置在线用户
  function setOnlineUsers(users: OnlineUser[]) {
    onlineUsers.value = users;
  }

  // 添加在线用户
  function addOnlineUser(user: OnlineUser) {
    if (!onlineUsers.value.some((u) => u.userId === user.userId)) {
      onlineUsers.value = [...onlineUsers.value, user];
    }
  }

  // 移除在线用户
  function removeOnlineUser(userId: string) {
    onlineUsers.value = onlineUsers.value.filter((u) => u.userId !== userId);
  }

  // 清空状态
  function clear() {
    messages.value = [];
    onlineUsers.value = [];
    unreadCount.value = 0;
  }

  return {
    messages,
    onlineUsers,
    isOpen,
    unreadCount,
    onlineCount,
    open,
    close,
    addMessage,
    setMessages,
    setOnlineUsers,
    addOnlineUser,
    removeOnlineUser,
    clear,
  };
});

export default useChatStore;
