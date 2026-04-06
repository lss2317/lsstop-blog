<template>
  <div class="chat-footer">
    <!-- 表情框 -->
    <div class="chat-emoji-box" v-show="showEmoji" @mousedown.prevent>
      <CommentEmoji @addEmoji="addEmoji" />
    </div>
    <!-- 一体化输入栏 -->
    <div class="chat-input-bar">
      <textarea
        ref="textareaRef"
        v-model="content"
        class="chat-textarea"
        placeholder="发送消息"
        rows="1"
        @input="adjustHeight"
        @focus="saveCursor"
        @keyup="saveCursor"
        @mouseup="saveCursor"
        @keydown.enter.exact="handleEnter"
      />
      <div class="chat-input-actions">
        <span
          ref="emojiTriggerRef"
          :class="['chat-action-icon', showEmoji ? 'active' : '']"
          title="表情"
          @mousedown.prevent
          @click="handleToggleEmoji"
        >
          <v-icon size="20">mdi-emoticon-outline</v-icon>
        </span>
        <span class="chat-action-icon disabled" title="图片（暂未开放）">
          <v-icon size="20">mdi-folder-outline</v-icon>
        </span>
        <span
          :class="['chat-send-icon', content.trim() ? 'active' : '']"
          title="发送"
          @click="handleSend"
        >
          <v-icon size="16">mdi-arrow-up</v-icon>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onUnmounted } from 'vue';
import CommentEmoji from '@/components/Emoji/CommentEmoji.vue';
import { useEmoji } from '@/composables/useEmoji';

const emit = defineEmits<{
  send: [content: string];
}>();

const content = ref('');
const textareaRef = ref<HTMLTextAreaElement | null>(null);
const initialHeight = ref(0);
const savedCursorPos = ref<{ start: number; end: number } | null>(null);

const { showEmoji, emojiTriggerRef, toggleEmoji, closeEmoji } = useEmoji();

// 点击聊天容器内部（表情框和触发按钮以外的区域）关闭表情框
const handleContainerClick = (event: MouseEvent) => {
  if (!showEmoji.value) return;
  const target = event.target as HTMLElement;
  if (!target.closest('.chat-emoji-box') && !target.closest('.chat-action-icon')) {
    closeEmoji();
  }
};

// 保存光标位置
const saveCursor = () => {
  if (!textareaRef.value) return;
  savedCursorPos.value = {
    start: textareaRef.value.selectionStart ?? content.value.length,
    end: textareaRef.value.selectionEnd ?? content.value.length,
  };
};

const handleToggleEmoji = (event?: MouseEvent) => {
  if (!showEmoji.value && textareaRef.value) {
    saveCursor();
  }
  toggleEmoji(event);
};

// 自适应高度
const updateHeight = () => {
  if (!textareaRef.value) return;
  const textarea = textareaRef.value;
  if (!initialHeight.value) {
    initialHeight.value = textarea.offsetHeight;
  }
  const maxHeight = 80;
  textarea.style.height = 'auto';
  void getComputedStyle(textarea).height;
  const scrollHeight = textarea.scrollHeight;
  if (scrollHeight > initialHeight.value) {
    textarea.style.height = Math.min(scrollHeight, maxHeight) + 'px';
    textarea.style.overflowY = scrollHeight > maxHeight ? 'auto' : 'hidden';
  } else {
    textarea.style.height = initialHeight.value + 'px';
    textarea.style.overflowY = 'hidden';
  }
};

let rafId = 0;
const adjustHeight = () => {
  if (rafId) cancelAnimationFrame(rafId);
  rafId = requestAnimationFrame(updateHeight);
};

// 添加表情
const addEmoji = (key: string) => {
  const textarea = textareaRef.value;
  if (!textarea) {
    content.value += key;
    return;
  }
  const start = savedCursorPos.value?.start ?? textarea.selectionStart ?? content.value.length;
  const end = savedCursorPos.value?.end ?? textarea.selectionEnd ?? content.value.length;
  const text = content.value;
  content.value = text.slice(0, start) + key + text.slice(end);
  const newPos = start + key.length;
  savedCursorPos.value = { start: newPos, end: newPos };
  nextTick(() => {
    textarea.setSelectionRange(newPos, newPos);
    textarea.focus();
    updateHeight();
  });
};

// 发送消息
const handleSend = () => {
  if (!content.value.trim()) return;
  emit('send', content.value);
  content.value = '';
  closeEmoji();
  nextTick(() => {
    if (textareaRef.value) {
      textareaRef.value.style.height = initialHeight.value + 'px';
      textareaRef.value.style.overflowY = 'hidden';
    }
  });
};

// Enter 发送
const handleEnter = (e: KeyboardEvent) => {
  e.preventDefault();
  handleSend();
};

onMounted(() => {
  // 监听聊天容器点击来关闭表情框（因为 chat-container 有 stopPropagation，document 监听无效）
  const chatContainer = document.querySelector('.chat-container');
  if (chatContainer) {
    chatContainer.addEventListener('click', handleContainerClick as EventListener);
  }
  nextTick(() => {
    if (textareaRef.value) {
      initialHeight.value = textareaRef.value.offsetHeight;
    }
  });
});

onUnmounted(() => {
  const chatContainer = document.querySelector('.chat-container');
  if (chatContainer) {
    chatContainer.removeEventListener('click', handleContainerClick as EventListener);
  }
  if (rafId) cancelAnimationFrame(rafId);
});
</script>

<style scoped>
.chat-footer {
  position: relative;
  padding: 10px 16px;
  background: #f7f7f7;
  border-radius: 0 0 1rem 1rem;
}

/* 一体化输入栏 */
.chat-input-bar {
  display: flex;
  align-items: flex-end;
  background: #e8e8e8;
  border-radius: 22px;
  padding: 4px 6px 4px 16px;
  gap: 2px;
  min-height: 40px;
  transition: background 0.2s;
}

.chat-input-bar:focus-within {
  background: #dcdcdc;
}

.chat-textarea {
  flex: 1;
  padding: 6px 0;
  border: none;
  font-size: 13px;
  line-height: 1.4;
  outline: none;
  resize: none;
  overflow-y: hidden;
  min-height: 30px;
  max-height: 80px;
  background: transparent;
  font-family: inherit;
  box-sizing: border-box;
  word-break: break-all;
  scrollbar-width: none;
  color: #262626;
}

.chat-textarea::-webkit-scrollbar {
  display: none;
}

.chat-textarea::placeholder {
  color: #999;
}

/* 右侧操作按钮组 */
.chat-input-actions {
  display: flex;
  align-items: center;
  gap: 2px;
  flex-shrink: 0;
}

.chat-action-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  color: #8c8c8c;
  transition: all 0.2s;
}

.chat-action-icon:hover,
.chat-action-icon.active {
  color: #49b1f5;
}

.chat-action-icon.disabled {
  color: #bfbfbf;
  cursor: not-allowed;
  opacity: 0.5;
}

.chat-action-icon.disabled:hover {
  color: #bfbfbf;
}

/* 发送按钮 */
.chat-send-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  cursor: pointer;
  color: #fff;
  background: #ccc;
  transition: all 0.2s;
  margin-bottom: 2px;
}

.chat-send-icon.active {
  background: #007aff;
}

.chat-send-icon.active:hover {
  background: #0066d6;
}

/* 表情弹出框 */
.chat-emoji-box {
  position: absolute;
  bottom: 56px;
  right: 16px;
  background: #fff;
  border-radius: 12px;
  padding: 6px 6px 2px;
  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.12),
    0 4px 12px rgba(0, 0, 0, 0.08);
  z-index: 10;
}
</style>

<!-- 夜间模式 -->
<style>
.v-theme--dark .chat-footer {
  background: #1e1e1e;
}

.v-theme--dark .chat-input-bar {
  background: #2d2d2d;
}

.v-theme--dark .chat-input-bar:focus-within {
  background: #333;
}

.v-theme--dark .chat-textarea {
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .chat-textarea::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.v-theme--dark .chat-emoji-box {
  background: #2d2d2d;
}
</style>
