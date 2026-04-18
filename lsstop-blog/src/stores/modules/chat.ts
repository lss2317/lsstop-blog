import { defineStore } from 'pinia';
import { shallowRef } from 'vue';
import type { ChatMessage, ChatUserInfo } from '@/apis/chat/types';

export type { ChatMessage, ChatUserInfo };

const useChatStore = defineStore('chat', () => {
  // 消息列表
  const messages = shallowRef<ChatMessage[]>([]);
  // 在线人数（WS 直接推数字）
  const onlineCount = shallowRef(0);
  // 聊天室是否打开
  const isOpen = shallowRef(false);
  // 未读消息数
  const unreadCount = shallowRef(0);
  // 用户信息缓存 userId → {nickname, avatar}
  const userMap = shallowRef<Map<string, ChatUserInfo>>(new Map());
  // 是否还有更多历史消息
  const hasMore = shallowRef(true);
  // WebSocket 断连回调（由 composable 注册）
  let _disconnectFn: (() => void) | null = null;

  // 打开聊天室
  function open() {
    isOpen.value = true;
    unreadCount.value = 0;
  }

  // 关闭聊天室
  function close() {
    isOpen.value = false;
  }

  // 追加新消息
  function addMessage(message: ChatMessage) {
    messages.value = [...messages.value, message];
    if (!isOpen.value) {
      unreadCount.value++;
    }
  }

  // 设置消息列表（替换）
  function setMessages(list: ChatMessage[]) {
    messages.value = list;
  }

  // 前置历史消息（加载更多时用）
  function prependMessages(list: ChatMessage[]) {
    messages.value = [...list, ...messages.value];
  }

  // 设置在线人数
  function setOnlineCount(count: number) {
    onlineCount.value = count;
  }

  // 批量更新用户信息缓存
  function updateUserMap(entries: Array<{ userId: string; nickname: string; avatar: string }>) {
    const newMap = new Map(userMap.value);
    entries.forEach(({ userId, nickname, avatar }) => {
      newMap.set(userId, { nickname, avatar });
    });
    userMap.value = newMap;
  }

  // 获取用户昵称
  function getNickname(userId: string): string {
    return userMap.value.get(userId)?.nickname ?? '未知用户';
  }

  // 获取用户头像
  function getAvatar(userId: string): string {
    return userMap.value.get(userId)?.avatar ?? '';
  }

  // 清空状态
  function clear() {
    messages.value = [];
    onlineCount.value = 0;
    unreadCount.value = 0;
    userMap.value = new Map();
    hasMore.value = true;
  }

  // 注册 WebSocket 断连函数（由 composable 调用）
  function registerDisconnect(fn: () => void) {
    _disconnectFn = fn;
  }

  // 触发 WebSocket 断连（退出登录等场景）
  function disconnect() {
    _disconnectFn?.();
    _disconnectFn = null;
  }

  return {
    messages,
    onlineCount,
    isOpen,
    unreadCount,
    userMap,
    hasMore,
    open,
    close,
    addMessage,
    setMessages,
    prependMessages,
    setOnlineCount,
    updateUserMap,
    getNickname,
    getAvatar,
    clear,
    registerDisconnect,
    disconnect,
  };
});

export default useChatStore;
