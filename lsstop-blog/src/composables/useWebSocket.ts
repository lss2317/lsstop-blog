import { ref, shallowRef, onUnmounted } from 'vue';

export interface WebSocketOptions {
  /** 自动重连 */
  autoReconnect?: boolean;
  /** 最大重连次数 */
  maxReconnectAttempts?: number;
  /** 重连间隔（毫秒） */
  reconnectInterval?: number;
  /** 心跳间隔（毫秒），0 表示禁用 */
  heartbeatInterval?: number;
  /** 心跳消息 */
  heartbeatMessage?: string;
  /** 消息回调（每条消息都会触发，不受去重影响） */
  onMessage?: (data: string) => void;
  /** 重连前的异步钩子（可用于刷新 token 等），在自动重连和手动 reconnect 时触发 */
  onBeforeReconnect?: () => void | Promise<void>;
}

export type WebSocketStatus = 'CONNECTING' | 'OPEN' | 'CLOSING' | 'CLOSED';

/**
 * WebSocket 连接管理
 * @param urlOrFactory WebSocket 地址，或返回地址的工厂函数（每次连接/重连时调用）
 * @param options 配置选项
 */
export function useWebSocket(
  urlOrFactory: string | (() => string),
  options: WebSocketOptions = {},
) {
  const {
    autoReconnect = true,
    maxReconnectAttempts = 5,
    reconnectInterval = 3000,
    heartbeatInterval = 30000,
    heartbeatMessage = 'ping',
    onMessage,
    onBeforeReconnect,
  } = options;

  const resolveUrl = typeof urlOrFactory === 'function' ? urlOrFactory : () => urlOrFactory;

  const ws = shallowRef<WebSocket | null>(null);
  const status = ref<WebSocketStatus>('CLOSED');
  const data = ref<string | null>(null);

  let reconnectAttempts = 0;
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  let heartbeatTimer: ReturnType<typeof setInterval> | null = null;
  let isReconnecting = false;

  // 清除定时器
  const clearTimers = () => {
    if (reconnectTimer) {
      clearTimeout(reconnectTimer);
      reconnectTimer = null;
    }
    if (heartbeatTimer) {
      clearInterval(heartbeatTimer);
      heartbeatTimer = null;
    }
  };

  // 摘除旧 socket 事件并关闭，防止 onclose 异步触发污染状态
  const destroySocket = () => {
    if (ws.value) {
      ws.value.onopen = null;
      ws.value.onmessage = null;
      ws.value.onclose = null;
      ws.value.onerror = null;
      ws.value.close();
      ws.value = null;
    }
  };

  // 启动心跳
  const startHeartbeat = () => {
    if (heartbeatInterval <= 0) return;
    heartbeatTimer = setInterval(() => {
      if (ws.value?.readyState === WebSocket.OPEN) {
        ws.value.send(heartbeatMessage);
      }
    }, heartbeatInterval);
  };

  // 连接
  const connect = () => {
    if (ws.value?.readyState === WebSocket.OPEN) return;

    // 清理旧连接（防止旧 socket 事件污染）
    destroySocket();
    clearTimers();
    status.value = 'CONNECTING';

    const socket = new WebSocket(resolveUrl());

    socket.onopen = () => {
      status.value = 'OPEN';
      reconnectAttempts = 0;
      startHeartbeat();
    };

    socket.onmessage = (event) => {
      // 忽略心跳响应
      if (event.data === 'pong') return;
      data.value = event.data;
      // 回调方式确保每条消息都能被处理
      onMessage?.(event.data);
    };

    socket.onclose = () => {
      status.value = 'CLOSED';
      clearTimers();

      // 自动重连
      if (autoReconnect && reconnectAttempts < maxReconnectAttempts) {
        reconnectAttempts++;
        reconnectTimer = setTimeout(async () => {
          try {
            await onBeforeReconnect?.();
          } catch {
            // 钩子失败（如 token 刷新失败），中止本次重连
            return;
          }
          connect();
        }, reconnectInterval);
      }
    };

    socket.onerror = () => {
      socket.close();
    };

    ws.value = socket;
  };

  // 断开
  const disconnect = () => {
    reconnectAttempts = maxReconnectAttempts; // 阻止自动重连
    clearTimers();
    destroySocket();
    status.value = 'CLOSED';
  };

  // 重置重连计数并重新连接
  const reconnect = async () => {
    if (isReconnecting) return;
    isReconnecting = true;
    try {
      reconnectAttempts = 0;
      destroySocket();
      clearTimers();
      await onBeforeReconnect?.();
      connect();
    } catch {
      // 钩子失败不阻断重连
      connect();
    } finally {
      isReconnecting = false;
    }
  };

  // 发送消息
  const send = (message: string | object) => {
    if (ws.value?.readyState !== WebSocket.OPEN) return false;
    const msg = typeof message === 'string' ? message : JSON.stringify(message);
    ws.value.send(msg);
    return true;
  };

  // 组件卸载时断开连接
  onUnmounted(() => {
    disconnect();
  });

  return {
    ws,
    status,
    data,
    connect,
    disconnect,
    reconnect,
    send,
  };
}
