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
}

export type WebSocketStatus = 'CONNECTING' | 'OPEN' | 'CLOSING' | 'CLOSED';

/**
 * WebSocket 连接管理
 * @param url WebSocket 地址
 * @param options 配置选项
 */
export function useWebSocket(url: string, options: WebSocketOptions = {}) {
  const {
    autoReconnect = true,
    maxReconnectAttempts = 5,
    reconnectInterval = 3000,
    heartbeatInterval = 30000,
    heartbeatMessage = 'ping',
  } = options;

  const ws = shallowRef<WebSocket | null>(null);
  const status = ref<WebSocketStatus>('CLOSED');
  const data = ref<string | null>(null);

  let reconnectAttempts = 0;
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  let heartbeatTimer: ReturnType<typeof setInterval> | null = null;

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

    clearTimers();
    status.value = 'CONNECTING';

    const socket = new WebSocket(url);

    socket.onopen = () => {
      status.value = 'OPEN';
      reconnectAttempts = 0;
      startHeartbeat();
    };

    socket.onmessage = (event) => {
      // 忽略心跳响应
      if (event.data === 'pong') return;
      data.value = event.data;
    };

    socket.onclose = () => {
      status.value = 'CLOSED';
      clearTimers();

      // 自动重连
      if (autoReconnect && reconnectAttempts < maxReconnectAttempts) {
        reconnectAttempts++;
        reconnectTimer = setTimeout(connect, reconnectInterval);
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
    if (ws.value) {
      status.value = 'CLOSING';
      ws.value.close();
      ws.value = null;
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
    send,
  };
}
