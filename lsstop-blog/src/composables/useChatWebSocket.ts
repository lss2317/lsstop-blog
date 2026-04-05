import { watch } from 'vue';
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
        // 不足50条说明没有更多了
        chatStore.hasMore = list.length < 50;
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
        chatStore.hasMore = list.length >= 50;
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

  /** 初始化 WebSocket 连接 */
  const initWebSocket = () => {
    if (wsInitialized) return;
    wsInitialized = true;

    // 先加载历史消息
    void loadHistory();

    // 把当前用户信息放入 userMap
    const { userId, nickname, avatar } = userInfoStore.userInfo;
    if (userId) {
      chatStore.updateUserMap([{ userId, nickname: nickname ?? '', avatar: avatar ?? '' }]);
    }

    const token = tokenManager.getAccessToken();
    const baseUrl = websiteConfigStore.config.websocketUrl;
    const wsBase =
      baseUrl.startsWith('ws://') || baseUrl.startsWith('wss://') ? baseUrl : `ws://${baseUrl}`;
    const wsUrl = `${wsBase}/ws/chat?token=${token}`;

    wsInstance = useWebSocket(wsUrl, {
      autoReconnect: true,
      maxReconnectAttempts: 10,
      reconnectInterval: 3000,
      heartbeatInterval: 30000,
      heartbeatMessage: 'ping',
    });

    // 监听 WS 消息
    watch(
      () => wsInstance!.data.value,
      (raw) => {
        if (!raw) return;
        try {
          const msg = JSON.parse(raw) as WsDownMessage;
          handleWsMessage(msg);
        } catch {
          // 忽略解析失败
        }
      },
    );

    wsInstance.connect();
  };

  /** 发送消息（WS 上行） */
  const sendMessage = (content: string) => {
    if (!wsInstance || wsInstance.status.value !== 'OPEN') {
      snackbarStore.error('连接已断开，请稍后重试');
      return;
    }

    // 上行格式：{ "content": "文本", "images": ["URL"] }
    // 发送原始文本，表情由显示端 parseEmoji 负责解析
    wsInstance.send(JSON.stringify({ content }));
  };

  return {
    initWebSocket,
    sendMessage,
    loadMoreHistory,
  };
}
