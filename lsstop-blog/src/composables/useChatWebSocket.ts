import { useWebSocket } from '@/composables/useWebSocket';
import useChatStore, { type ChatMessage } from '@/stores/modules/chat';
import useUserInfoStore from '@/stores/modules/userInfo';
import useWebsiteConfigStore from '@/stores/modules/websiteConfig';
import { useSnackbarStore } from '@/stores/modules/snackbar';
import { tokenManager } from '@/utils/token';
import { listMessage } from '@/apis/chat';
import { ResponseCode } from '@/constants/http';

/** WS 下行消息类型 */
interface WsDownMessage {
  type: 'onlineUserCount' | 'message' | 'error';
  data: unknown;
}

/**
 * 聊天室 WebSocket 连接管理 + 历史消息加载
 */
/** 每页消息条数 */
const PAGE_SIZE = 50;

export function useChatWebSocket() {
  const chatStore = useChatStore();
  const userInfoStore = useUserInfoStore();
  const websiteConfigStore = useWebsiteConfigStore();
  const snackbarStore = useSnackbarStore();

  let wsInstance: ReturnType<typeof useWebSocket> | null = null;
  let wsInitialized = false;
  let historyLoaded = false;

  // 历史消息

  /** 加载历史消息（首次进入时调用） */
  const loadHistory = async () => {
    if (historyLoaded) return;
    historyLoaded = true;
    try {
      const res = await listMessage();
      if (res.code === ResponseCode.SUCCESS && res.data) {
        const list = res.data;
        chatStore.setMessages(list);
        // 从历史消息中提取用户信息缓存
        cacheUsersFromMessages(list);
        // 满页说明可能还有更多
        chatStore.hasMore = list.length >= PAGE_SIZE;
      }
    } catch {
      // 静默失败
    }
  };

  /** 加载更多历史消息 */
  const loadMoreHistory = async () => {
    if (!chatStore.hasMore) return;
    const msgs = chatStore.messages;
    if (msgs.length === 0) return;
    const lastId = msgs[0]!.id;
    try {
      const res = await listMessage(lastId);
      if (res.code === ResponseCode.SUCCESS && res.data) {
        const list = res.data;
        chatStore.prependMessages(list);
        cacheUsersFromMessages(list);
        chatStore.hasMore = list.length >= PAGE_SIZE;
      }
    } catch {
      // 静默失败
    }
  };

  /** 从消息列表中提取 userId → {nickname, avatar} 缓存 */
  const cacheUsersFromMessages = (messages: ChatMessage[]) => {
    const entries = messages
      .filter((m) => m.nickname && m.avatar)
      .map((m) => ({ userId: m.userId, nickname: m.nickname!, avatar: m.avatar! }));
    if (entries.length > 0) {
      chatStore.updateUserMap(entries);
    }
  };

  // WebSocket

  /** 处理 WS 下行消息 */
  const handleWsMessage = (msg: WsDownMessage) => {
    switch (msg.type) {
      case 'onlineUserCount':
        chatStore.setOnlineCount(msg.data as number);
        break;

      case 'message': {
        const data = msg.data as ChatMessage;
        chatStore.addMessage(data);
        // 如果是自己发的消息，确保 userMap 有自己的信息
        const { userId: selfId, nickname: selfNick, avatar: selfAvatar } = userInfoStore.userInfo;
        if (selfId && data.userId === selfId) {
          chatStore.updateUserMap([
            { userId: selfId, nickname: selfNick ?? '', avatar: selfAvatar ?? '' },
          ]);
        }
        break;
      }

      case 'error':
        snackbarStore.error(msg.data as string);
        break;
    }
  };

  /** 初始化 WebSocket 连接，返回历史消息加载 Promise */
  const initWebSocket = () => {
    if (wsInitialized) return Promise.resolve();
    wsInitialized = true;

    // 先加载历史消息
    const historyReady = loadHistory();

    // 把当前用户信息放入 userMap
    const { userId, nickname, avatar } = userInfoStore.userInfo;
    if (userId) {
      chatStore.updateUserMap([{ userId, nickname: nickname ?? '', avatar: avatar ?? '' }]);
    }

    const baseUrl = websiteConfigStore.config.websocketUrl;
    const wsBase =
      baseUrl.startsWith('ws://') || baseUrl.startsWith('wss://') ? baseUrl : `ws://${baseUrl}`;

    const buildWsUrl = () => {
      const token = tokenManager.getAccessToken();
      return `${wsBase}/ws/chat?token=${token}`;
    };

    wsInstance = useWebSocket(buildWsUrl, {
      autoReconnect: true,
      maxReconnectAttempts: 10,
      reconnectInterval: 3000,
      heartbeatInterval: 30000,
      heartbeatMessage: 'ping',
      onMessage: (raw) => {
        try {
          const msg = JSON.parse(raw) as WsDownMessage;
          handleWsMessage(msg);
        } catch {
          // 忽略解析失败
        }
      },
      onBeforeReconnect: async () => {
        const newToken = await tokenManager.refreshAccessToken();
        if (!newToken) {
          // token 刷新失败，抛出异常以中止重连
          throw new Error('Token refresh failed, abort reconnect');
        }
      },
    });

    wsInstance.connect();

    // 注册断连函数到 store，供退出登录等场景调用
    chatStore.registerDisconnect(disconnectWebSocket);

    return historyReady;
  };

  /** 发送消息（WS 上行） */
  const sendMessage = (content: string, images?: string[]) => {
    if (!wsInstance || wsInstance.status.value !== 'OPEN') {
      // 尝试重连，而非只报错
      snackbarStore.warning('连接已断开，正在重连...');
      wsInstance?.reconnect();
      return;
    }

    // 上行格式：{ "content": "文本", "images": ["URL"] }
    // 发送原始文本，表情由显示端 parseEmoji 负责解析
    const payload: Record<string, unknown> = {};
    if (content) payload.content = content;
    if (images && images.length > 0) payload.images = images;
    wsInstance.send(JSON.stringify(payload));
  };

  /** 断开 WebSocket 连接并重置状态 */
  const disconnectWebSocket = () => {
    wsInstance?.disconnect();
    wsInstance = null;
    wsInitialized = false;
    historyLoaded = false;
  };

  return {
    initWebSocket,
    disconnectWebSocket,
    sendMessage,
    loadMoreHistory,
  };
}
