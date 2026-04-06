<template>
  <!-- 聊天面板 -->
  <Transition name="chat-panel">
    <div v-show="chatStore.isOpen" class="chat-container" @click="handlePanelClick">
      <!-- 头部 -->
      <div class="chat-header">
        <v-icon size="28" color="#49b1f5">mdi-comment-processing</v-icon>
        <div class="chat-header-info">
          <span class="chat-header-title">聊天室</span>
          <span class="chat-header-count">
            <span class="chat-online-dot" />
            {{ chatStore.onlineCount }} 人在线
          </span>
        </div>
        <v-icon class="chat-close-btn" size="22" @click="chatStore.close()">mdi-close</v-icon>
      </div>

      <!-- 消息列表 -->
      <ChatMessageList
        ref="messageListRef"
        :messages="chatStore.messages"
        @load-more="loadMoreHistory"
      />

      <!-- 输入区 -->
      <ChatInput @send="sendMessage" />
    </div>
  </Transition>

  <!-- 浮窗按钮 -->
  <div class="chat-fab" @click="handleOpen">
    <span v-if="chatStore.unreadCount > 0" class="chat-unread">
      {{ chatStore.unreadCount > 99 ? '99+' : chatStore.unreadCount }}
    </span>
    <v-icon size="28" color="#fff">mdi-chat-processing</v-icon>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';
import ChatMessageList from './ChatMessageList.vue';
import ChatInput from './ChatInput.vue';
import useChatStore from '@/stores/modules/chat';
import useUserInfoStore from '@/stores/modules/userInfo';
import { useChatWebSocket } from '@/composables/useChatWebSocket';

const chatStore = useChatStore();
const userInfoStore = useUserInfoStore();
const { initWebSocket, sendMessage, loadMoreHistory } = useChatWebSocket();
const messageListRef = ref<InstanceType<typeof ChatMessageList> | null>(null);

// 打开聊天室（必须登录）
const handleOpen = () => {
  if (!userInfoStore.checkLogin('进入聊天室')) return;
  initWebSocket();
  chatStore.open();
  nextTick(() => messageListRef.value?.scrollToBottom(false));
};

// 点击面板内部（阻止冒泡，防止误关闭）
const handlePanelClick = (e: MouseEvent) => {
  e.stopPropagation();
};
</script>

<style scoped>
/* 聊天面板 */
.chat-container {
  position: fixed;
  z-index: 1200;
  display: flex;
  flex-direction: column;
  background: #f4f6fb;
  box-shadow: 0 5px 40px rgba(0, 0, 0, 0.16);
  font-size: 14px;
  overflow: hidden;
}

@media (min-width: 760px) {
  .chat-container {
    bottom: 90px;
    right: 60px;
    width: 380px;
    height: calc(85vh - 84px);
    max-height: 560px;
    min-height: 300px;
    border-radius: 16px;
  }
}

@media (max-width: 759px) {
  .chat-container {
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    border-radius: 0;
  }
}

/* 头部 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: #fff;
  gap: 12px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
  z-index: 1;
}

.chat-header-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header-title {
  font-size: 15px;
  font-weight: 600;
  color: #262626;
  line-height: 1.2;
}

.chat-header-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #8c8c8c;
  margin-top: 2px;
}

.chat-online-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #52c41a;
  display: inline-block;
}

.chat-close-btn {
  cursor: pointer;
  color: #8c8c8c !important;
  border-radius: 50%;
  padding: 4px;
  transition: all 0.2s;
}

.chat-close-btn:hover {
  background: rgba(0, 0, 0, 0.06);
  color: #595959 !important;
}

/* 浮窗按钮 */
.chat-fab {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #49b1f5, #1e90ff);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 1000;
  box-shadow: 0 4px 16px rgba(73, 177, 245, 0.4);
  transition: all 0.3s;
  user-select: none;
}

.chat-fab:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 20px rgba(73, 177, 245, 0.5);
}

.chat-fab:active {
  transform: scale(0.96);
}

/* 未读数 */
.chat-unread {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  font-size: 11px;
  line-height: 18px;
  text-align: center;
  color: #fff;
  background: #f24f2d;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(242, 79, 45, 0.4);
}

/* 面板动画 */
.chat-panel-enter-active {
  animation: chatBounceIn 0.35s ease-out;
}

.chat-panel-leave-active {
  animation: chatBounceOut 0.25s ease-in;
}

@keyframes chatBounceIn {
  0% {
    opacity: 0;
    transform: scale(0.8) translateY(20px);
  }
  60% {
    opacity: 1;
    transform: scale(1.02) translateY(-2px);
  }
  100% {
    transform: scale(1) translateY(0);
  }
}

@keyframes chatBounceOut {
  0% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  100% {
    opacity: 0;
    transform: scale(0.85) translateY(20px);
  }
}
</style>

<!-- 夜间模式 -->
<style>
.v-theme--dark .chat-container {
  background: #1a1a1a;
}

.v-theme--dark .chat-header {
  background: #242424;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.2);
}

.v-theme--dark .chat-header-title {
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .chat-header-count {
  color: rgba(255, 255, 255, 0.5);
}

.v-theme--dark .chat-close-btn {
  color: rgba(255, 255, 255, 0.5) !important;
}

.v-theme--dark .chat-close-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.8) !important;
}
</style>
