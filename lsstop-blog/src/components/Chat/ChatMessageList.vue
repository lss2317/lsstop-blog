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

      <!-- 消息项 -->
      <div :class="['chat-msg-row', isSelf(msg.userId) ? 'self' : 'other']">
        <img
          class="chat-avatar"
          :src="msg.avatar || chatStore.getAvatar(msg.userId)"
          alt=""
          @click="goToUserProfile(msg.userId)"
        />
        <div class="chat-msg-body">
          <div class="chat-msg-header">
            <span v-if="msg.ipRegion && isSelf(msg.userId)" class="chat-ip-tag">{{
              msg.ipRegion
            }}</span>
            <span class="chat-nickname">
              {{ msg.nickname || chatStore.getNickname(msg.userId) }}
            </span>
            <span v-if="msg.ipRegion && !isSelf(msg.userId)" class="chat-ip-tag">{{
              msg.ipRegion
            }}</span>
          </div>
          <!-- 文本内容 -->
          <div
            v-if="msg.content"
            v-fit-bubble
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
              @click="previewImages(msg.images!, imgIdx)"
            />
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref, watch } from 'vue';
import type { ChatMessage } from '@/apis/chat/types';
import useChatStore from '@/stores/modules/chat';
import useUserInfoStore from '@/stores/modules/userInfo';
import { parseEmoji } from '@/utils/emoji';
import { formatChatTime } from '@/utils/date';
import { useNavigate } from '@/composables/useNavigate';
import { previewImages } from '@/utils/photoPreview';

// 含表情图片的气泡：二分搜索收缩右侧空白，下限为原宽度的 95%
// 等所有图片加载完成后再计算，避免因图片尺寸未知导致错误换行
const fitBubbleWidth = (el: HTMLElement) => {
  const imgs = el.querySelectorAll('img');
  if (!imgs.length) return;

  const run = () => {
    el.style.opacity = '0';
    el.style.width = '';
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        const initialHeight = el.scrollHeight;
        if (initialHeight <= 0) {
          el.style.opacity = '';
          return;
        }

        const originalWidth = el.offsetWidth;
        let lo = Math.floor(originalWidth * 0.95);
        let hi = originalWidth;
        if (hi - lo < 4) {
          el.style.opacity = '';
          return;
        }

        while (hi - lo > 2) {
          const mid = Math.floor((lo + hi) / 2);
          el.style.width = mid + 'px';
          if (el.scrollHeight > initialHeight) {
            lo = mid + 1;
          } else {
            hi = mid;
          }
        }
        el.style.width = hi + 1 + 'px';
        el.style.opacity = '';
      });
    });
  };

  // 检查是否所有图片已加载完成
  const allLoaded = () => Array.from(imgs).every((img) => img.complete && img.naturalWidth > 0);

  if (allLoaded()) {
    run();
  } else {
    el.style.opacity = '0';
    let loaded = 0;
    const total = imgs.length;
    const onLoad = () => {
      if (++loaded >= total) run();
    };
    imgs.forEach((img) => {
      if (img.complete && img.naturalWidth > 0) {
        onLoad();
      } else {
        img.addEventListener('load', onLoad, { once: true });
        img.addEventListener('error', onLoad, { once: true });
      }
    });
  }
};

const vFitBubble = {
  mounted: (el: HTMLElement) => fitBubbleWidth(el),
};

const props = defineProps<{
  messages: ChatMessage[];
}>();

defineEmits<{
  loadMore: [];
}>();

const chatStore = useChatStore();
const userInfoStore = useUserInfoStore();
const messageContainer = ref<HTMLElement | null>(null);
const { navigateTo } = useNavigate();

// 点击头像跳转用户主页
const goToUserProfile = (userId: string) => {
  chatStore.close();
  navigateTo(`/user/${userId}`);
};

// 是否在底部附近（用于判断是否自动滚底）
const isNearBottom = ref(true);

// 加载更多时保持滚动位置
let savedScrollHeight = 0;

const saveScrollPosition = () => {
  if (!messageContainer.value) return;
  savedScrollHeight = messageContainer.value.scrollHeight;
};

const restoreScrollPosition = () => {
  nextTick(() => {
    if (!messageContainer.value) return;
    const newScrollHeight = messageContainer.value.scrollHeight;
    messageContainer.value.scrollTop = newScrollHeight - savedScrollHeight;
  });
};

// 判断是否是自己
const isSelf = (userId: string) => {
  return userInfoStore.userInfo.userId === userId;
};

// 解析消息内容（表情 + 换行）
// parseEmoji 内部已做 escapeHtml，无需重复转义
const parseContent = (content: string | null) => {
  if (!content) return '';
  return parseEmoji(content);
};

// 时间分隔（间隔超过5分钟显示）
const showTimeDivider = (index: number) => {
  const prevMsg = props.messages[index - 1];
  if (!prevMsg) return true;
  const prev = new Date(prevMsg.createTime).getTime();
  const curr = new Date(props.messages[index]!.createTime).getTime();
  return (curr - prev) / 60000 >= 5;
};

// 滚动到底部
const scrollToBottom = (smooth = true) => {
  nextTick(() => {
    if (!messageContainer.value) return;
    const container = messageContainer.value;
    const doScroll = () => {
      container.scrollTo({
        top: container.scrollHeight,
        behavior: smooth ? 'smooth' : 'auto',
      });
    };
    doScroll();
    // 处理懒加载图片：图片加载后 scrollHeight 会变化，需要再次滚底
    const imgs = container.querySelectorAll<HTMLImageElement>('img.chat-msg-image');
    imgs.forEach((img) => {
      if (!img.complete) {
        img.addEventListener('load', doScroll, { once: true });
      }
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
  saveScrollPosition,
  restoreScrollPosition,
});
</script>

<style scoped>
.chat-message-list {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 16px 16px 8px;
  scrollbar-width: thin;
  scrollbar-color: rgba(0, 0, 0, 0.15) transparent;
}

.chat-message-list::-webkit-scrollbar {
  width: 4px;
}

.chat-message-list::-webkit-scrollbar-track {
  background: transparent;
}

.chat-message-list::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.15);
  border-radius: 4px;
}

.chat-message-list::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.25);
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
  margin-bottom: 16px;
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
  cursor: pointer;
}

.chat-msg-row.other .chat-avatar {
  margin-right: 10px;
}

.chat-msg-row.self .chat-avatar {
  margin-left: 10px;
}

/* 消息体 */
.chat-msg-body {
  max-width: calc(100% - 46px);
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.chat-msg-row.self .chat-msg-body {
  align-items: flex-end;
}

/* 昵称 + IP 属地 行 */
.chat-msg-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.chat-nickname {
  font-size: 13px;
  color: #595959;
  font-weight: 500;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-ip-tag {
  font-size: 11px;
  color: #8c8c8c;
  background: rgba(0, 0, 0, 0.04);
  padding: 0 6px;
  border-radius: 4px;
  line-height: 18px;
  white-space: nowrap;
}

/* 气泡 */
.chat-bubble {
  padding: 8px 14px;
  font-size: 13px;
  line-height: 1.5;
  word-break: break-all;
  white-space: pre-line;
  width: fit-content;
  max-width: 100%;
  border-radius: 14px;
}

.chat-bubble.other {
  background: #fff;
  color: #4c4948;
}

.chat-bubble.self {
  background: #007aff;
  color: #fff;
}

/* 气泡内表情 */
.chat-bubble :deep(img) {
  width: 18px;
  height: 18px;
  vertical-align: middle;
  margin: 0 1px;
}

/* 图片消息 */
.chat-images {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 4px;
}

.chat-msg-row.self .chat-images {
  justify-content: flex-end;
}

.chat-msg-image {
  max-width: 180px;
  border-radius: 8px;
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

.v-theme--dark .chat-message-list {
  scrollbar-color: rgba(255, 255, 255, 0.15) transparent;
}

.v-theme--dark .chat-message-list::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.15);
}

.v-theme--dark .chat-message-list::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.25);
}

.v-theme--dark .chat-bubble.other {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .chat-bubble.self {
  background: #0066d6;
}

.v-theme--dark .chat-nickname {
  color: rgba(255, 255, 255, 0.7);
}

.v-theme--dark .chat-ip-tag {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.4);
}

.v-theme--dark .chat-empty {
  color: rgba(255, 255, 255, 0.3);
}
</style>
