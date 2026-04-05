<template>
  <div class="chat-footer">
    <!-- 表情框 -->
    <div class="chat-emoji-box" v-show="showEmoji" @mousedown.prevent>
      <CommentEmoji @addEmoji="addEmoji" />
    </div>
    <div class="chat-emoji-arrow" v-show="showEmoji" />
    <!-- 工具栏 + 输入 -->
    <div class="chat-input-row">
      <span
        ref="emojiTriggerRef"
        :class="['chat-tool-icon', showEmoji ? 'active' : '']"
        title="表情"
        @mousedown.prevent
        @click="handleToggleEmoji"
      >
        <v-icon size="20">mdi-emoticon-outline</v-icon>
      </span>
      <textarea
        ref="textareaRef"
        v-model="content"
        class="chat-textarea"
        placeholder="请输入消息..."
        rows="1"
        @input="adjustHeight"
        @focus="saveCursor"
        @keyup="saveCursor"
        @mouseup="saveCursor"
        @keydown.enter.exact="handleEnter"
      />
      <span :class="['chat-send-btn', content.trim() ? 'active' : '']" @click="handleSend">
        <v-icon size="20">mdi-arrow-up-circle</v-icon>
      </span>
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

const {
  showEmoji,
  emojiTriggerRef,
  toggleEmoji,
  closeEmoji,
  registerClickOutside,
  unregisterClickOutside,
} = useEmoji();

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
  registerClickOutside();
  nextTick(() => {
    if (textareaRef.value) {
      initialHeight.value = textareaRef.value.offsetHeight;
    }
  });
});

onUnmounted(() => {
  unregisterClickOutside();
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

.chat-input-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.chat-tool-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  color: #8c8c8c;
  flex-shrink: 0;
  transition: all 0.2s;
}

.chat-tool-icon:hover,
.chat-tool-icon.active {
  color: #49b1f5;
  background: rgba(73, 177, 245, 0.1);
}

.chat-textarea {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e5e5e5;
  border-radius: 18px;
  font-size: 13px;
  line-height: 1.4;
  outline: none;
  resize: none;
  overflow-y: hidden;
  min-height: 34px;
  max-height: 80px;
  background: #fff;
  font-family: inherit;
  box-sizing: border-box;
  word-break: break-all;
  transition: border-color 0.2s;
}

.chat-textarea::placeholder {
  color: #bfbfbf;
}

.chat-textarea:focus {
  border-color: #49b1f5;
}

.chat-send-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  color: #bfbfbf;
  flex-shrink: 0;
  transition: all 0.2s;
}

.chat-send-btn.active {
  color: #49b1f5;
}

.chat-send-btn.active:hover {
  background: rgba(73, 177, 245, 0.1);
}

.chat-emoji-box {
  position: absolute;
  bottom: 56px;
  left: 16px;
  background: #fff;
  border-radius: 8px;
  padding: 6px 6px 2px;
  box-shadow:
    0 8px 16px rgba(50, 50, 93, 0.08),
    0 4px 12px rgba(0, 0, 0, 0.07);
  z-index: 10;
}

.chat-emoji-arrow::before {
  display: block;
  height: 0;
  width: 0;
  content: '';
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-top: 8px solid #fff;
  position: absolute;
  bottom: 48px;
  left: 26px;
  filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.04));
}
</style>

<!-- 夜间模式 -->
<style>
.v-theme--dark .chat-footer {
  background: #1e1e1e;
}

.v-theme--dark .chat-textarea {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.15);
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .chat-textarea::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.v-theme--dark .chat-textarea:focus {
  border-color: #49b1f5;
}

.v-theme--dark .chat-emoji-box {
  background: #2d2d2d;
}

.v-theme--dark .chat-emoji-arrow::before {
  border-top-color: #2d2d2d;
}
</style>
