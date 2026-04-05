<template>
  <div ref="messageContainer" class="chat-message-list" @scroll="handleScroll">
    <!-- 加载更多 -->
    <div
      v-if="chatStore.hasMore && messages.length > 0"
      class="chat-load-more"
      @click="$emit('loadMore')"
    >
      <span>加载更多</span>
    </div>

    <!-- 空状态 -->
    <div v-if="messages.length === 0" class="chat-empty">
      <v-icon size="48" color="#d9d9d9">mdi-chat-outline</v-icon>
      <p>暂无消息，快来聊天吧~</p>
    </div>

    <!-- 消息列表 -->
    <template v-for="(msg, index) in messages" :key="msg.id">
      <!-- 时间分隔 -->
      <div v-if="showTimeDivider(index)" class="chat-time-divider">
        <span>{{ formatChatTime(msg.createTime) }}</span>
      </div>

      <!-- 消息气泡 -->
      <div :class="['chat-msg-row', isSelf(msg.userId) ? 'self' : 'other']">
        <img class="chat-avatar" :src="msg.avatar || chatStore.getAvatar(msg.userId)" alt="" />
        <div class="chat-msg-body">
          <span v-if="!isSelf(msg.userId)" class="chat-nickname">
            {{ msg.nickname || chatStore.getNickname(msg.userId) }}
          </span>
          <!-- 文本内容 -->
          <div
            v-if="msg.content"
            :class="['chat-bubble', isSelf(msg.userId) ? 'self' : 'other']"
            v-html="parseContent(msg.content)"
          />
          <!-- 图片列表 -->
          <div v-if="msg.images && msg.images.length > 0" class="chat-images">
            <img
              v-for="(imgUrl, imgIdx) in msg.images"
              :key="imgIdx"
              class="chat-msg-image"
              :src="imgUrl"
              alt=""
              loading="lazy"
            />
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted } from 'vue';
import type { ChatMessage } from '@/stores/modules/chat';
import useChatStore from '@/stores/modules/chat';
import useUserInfoStore from '@/stores/modules/userInfo';
import { parseEmoji } from '@/utils/emoji';
import dayjs from 'dayjs';

const props = defineProps<{
  messages: ChatMessage[];
}>();

defineEmits<{
  loadMore: [];
}>();

const chatStore = useChatStore();
const userInfoStore = useUserInfoStore();
const messageContainer = ref<HTMLElement | null>(null);

// 是否在底部附近（用于判断是否自动滚底）
const isNearBottom = ref(true);

// 判断是否是自己
const isSelf = (userId: string) => {
  return userInfoStore.userInfo.userId === userId;
};

// 解析消息内容（表情 + 换行）
const parseContent = (content: string | null) => {
  if (!content) return '';
  const escaped = content
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\n/g, '<br/>');
  return parseEmoji(escaped);
};

// 时间分隔（间隔超过5分钟显示）
const showTimeDivider = (index: number) => {
  if (index === 0) return true;
  const prev = dayjs(props.messages[index - 1].createTime);
  const curr = dayjs(props.messages[index].createTime);
  return curr.diff(prev, 'minute') >= 5;
};

// 格式化聊天时间
const formatChatTime = (time: string) => {
  const date = dayjs(time);
  const now = dayjs();
  if (date.isSame(now, 'day')) return date.format('HH:mm');
  if (date.isSame(now.subtract(1, 'day'), 'day')) return '昨天 ' + date.format('HH:mm');
  if (date.isSame(now, 'year')) return date.format('MM-DD HH:mm');
  return date.format('YYYY-MM-DD HH:mm');
};

// 滚动到底部
const scrollToBottom = (smooth = true) => {
  nextTick(() => {
    if (!messageContainer.value) return;
    messageContainer.value.scrollTo({
      top: messageContainer.value.scrollHeight,
      behavior: smooth ? 'smooth' : 'auto',
    });
  });
};

// 监听滚动判断是否在底部
const handleScroll = () => {
  if (!messageContainer.value) return;
  const { scrollTop, scrollHeight, clientHeight } = messageContainer.value;
  isNearBottom.value = scrollHeight - scrollTop - clientHeight < 60;
};

// 消息变化时自动滚底
watch(
  () => props.messages.length,
  () => {
    if (isNearBottom.value) {
      scrollToBottom();
    }
  },
);

// 初始化滚到底部
onMounted(() => {
  scrollToBottom(false);
});

// 暴露方法
defineExpose({
  scrollToBottom,
});
</script>

<style scoped>
.chat-message-list {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 16px 16px 8px;
}

.chat-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #bfbfbf;
  gap: 12px;
}

.chat-empty p {
  font-size: 13px;
  margin: 0 !important;
}

/* 时间分隔 */
.chat-time-divider {
  text-align: center;
  margin: 12px 0;
}

.chat-time-divider span {
  font-size: 11px;
  color: #bfbfbf;
  background: rgba(0, 0, 0, 0.04);
  padding: 2px 10px;
  border-radius: 10px;
}

/* 消息行 */
.chat-msg-row {
  display: flex;
  margin-bottom: 12px;
  align-items: flex-start;
}

.chat-msg-row.self {
  flex-direction: row-reverse;
}

/* 头像 */
.chat-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.chat-msg-row.other .chat-avatar {
  margin-right: 10px;
}

.chat-msg-row.self .chat-avatar {
  margin-left: 10px;
}

/* 消息体 */
.chat-msg-body {
  max-width: calc(100% - 56px);
  display: flex;
  flex-direction: column;
}

.chat-msg-row.self .chat-msg-body {
  align-items: flex-end;
}

.chat-nickname {
  font-size: 11px;
  color: #8c8c8c;
  margin-bottom: 4px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 气泡 */
.chat-bubble {
  padding: 8px 14px;
  font-size: 13px;
  line-height: 1.5;
  word-wrap: break-word;
  word-break: break-all;
  white-space: pre-line;
  width: fit-content;
  max-width: 100%;
}

.chat-bubble.other {
  background: #fff;
  color: #4c4948;
  border-radius: 4px 18px 18px 18px;
}

.chat-bubble.self {
  background: #12b7f5;
  color: #fff;
  border-radius: 18px 4px 18px 18px;
}

/* 气泡内表情 */
.chat-bubble :deep(img) {
  vertical-align: text-bottom;
  margin: 0 1px;
}

/* 图片消息 */
.chat-images {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 4px;
}

.chat-msg-image {
  max-width: 180px;
  max-height: 180px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
}

/* 加载更多 */
.chat-load-more {
  text-align: center;
  padding: 8px 0;
  cursor: pointer;
}

.chat-load-more span {
  font-size: 12px;
  color: #49b1f5;
  padding: 2px 12px;
  border-radius: 10px;
  background: rgba(73, 177, 245, 0.08);
  transition: background 0.2s;
}

.chat-load-more span:hover {
  background: rgba(73, 177, 245, 0.16);
}
</style>

<!-- 夜间模式 -->
<style>
.v-theme--dark .chat-time-divider span {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.4);
}

.v-theme--dark .chat-bubble.other {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .chat-nickname {
  color: rgba(255, 255, 255, 0.5);
}

.v-theme--dark .chat-empty {
  color: rgba(255, 255, 255, 0.3);
}
</style>
