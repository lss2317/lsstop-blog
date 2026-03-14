<template>
  <div class="reply-input-container">
    <div class="reply-input-box">
      <div class="reply-avatar">
        <img :src="avatar" alt="用户头像" />
      </div>
      <div class="reply-input-wrapper">
        <textarea
          ref="textareaRef"
          class="reply-input"
          :value="modelValue"
          :placeholder="placeholder"
          rows="1"
          @input="onInput"
          @focus="saveCursor"
          @keyup="saveCursor"
          @mouseup="saveCursor"
        />
      </div>
    </div>
    <div class="reply-actions">
      <div class="lc-emoji-trigger-wrapper">
        <span
          ref="emojiTriggerRef"
          :class="['lc-tool-icon', showEmoji ? 'active' : '']"
          title="表情"
          @mousedown.prevent
          @click="handleToggleEmoji"
        >
          <v-icon size="20">mdi-emoticon-outline</v-icon>
        </span>
        <div :class="['lc-emoji-panel', emojiDirection]" v-show="showEmoji" @mousedown.prevent>
          <CommentEmoji @addEmoji="addEmoji" />
        </div>
      </div>
      <div class="reply-btns">
        <button class="cancel-btn" :disabled="submitting" @click="$emit('cancel')">取消</button>
        <button
          class="submit-btn"
          :disabled="!modelValue.trim() || submitting"
          @click="$emit('submit')"
        >
          <Transition name="btn-fade" mode="out-in">
            <v-progress-circular
              v-if="submitting"
              key="loading"
              indeterminate
              size="16"
              width="2"
            />
            <span v-else key="text">回复</span>
          </Transition>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import CommentEmoji from '@/components/Emoji/CommentEmoji.vue';
import { useEmoji } from '@/composables/useEmoji';
import { onMounted, onUnmounted, ref, watch, nextTick } from 'vue';

const props = defineProps<{
  avatar: string;
  placeholder?: string;
  modelValue: string;
  submitting?: boolean;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: string];
  submit: [];
  cancel: [];
}>();

const textareaRef = ref<HTMLTextAreaElement | null>(null);
const initialHeight = ref(0);
// 保存的光标位置（打开表情框时记录）
const savedCursorPos = ref<{ start: number; end: number } | null>(null);

const {
  showEmoji,
  emojiDirection,
  emojiTriggerRef,
  toggleEmoji,
  registerClickOutside,
  unregisterClickOutside,
} = useEmoji();

// 保存光标位置（focus/keyup/mouseup 时调用）
const saveCursor = () => {
  if (!textareaRef.value) return;
  const textarea = textareaRef.value;
  savedCursorPos.value = {
    start: textarea.selectionStart ?? props.modelValue.length,
    end: textarea.selectionEnd ?? props.modelValue.length,
  };
};

// 打开表情框时保存光标位置
const handleToggleEmoji = (event?: MouseEvent) => {
  if (!showEmoji.value && textareaRef.value) {
    saveCursor();
  }
  toggleEmoji(event);
};

// 实际高度计算逻辑（不带节流）
const updateHeight = () => {
  if (!textareaRef.value) return;
  const textarea = textareaRef.value;
  // 兜底：如果初始高度还未获取，先获取一次
  if (!initialHeight.value) {
    initialHeight.value = textarea.offsetHeight;
  }
  // 从 CSS 读取 max-height
  const maxHeight = parseInt(getComputedStyle(textarea).maxHeight) || 200;
  textarea.style.height = 'auto';
  void getComputedStyle(textarea).height; // 强制同步布局计算
  const scrollHeight = textarea.scrollHeight;
  if (scrollHeight > initialHeight.value) {
    textarea.style.height = Math.min(scrollHeight, maxHeight) + 'px';
    textarea.style.overflowY = scrollHeight > maxHeight ? 'auto' : 'hidden';
  } else {
    textarea.style.height = initialHeight.value + 'px';
    textarea.style.overflowY = 'hidden';
  }
};

// 调整高度（rAF 节流，用于 @input）
let rafId = 0;
const adjustHeight = () => {
  if (rafId) cancelAnimationFrame(rafId);
  rafId = requestAnimationFrame(updateHeight);
};

const onInput = (event: Event) => {
  const textarea = event.target as HTMLTextAreaElement;
  emit('update:modelValue', textarea.value);
  adjustHeight();
};

// 添加表情（插入到保存的光标位置）
const addEmoji = (key: string) => {
  const textarea = textareaRef.value;
  if (!textarea) {
    emit('update:modelValue', props.modelValue + key);
    return;
  }
  // 使用保存的光标位置，如果没有则使用当前位置或末尾
  const start = savedCursorPos.value?.start ?? textarea.selectionStart ?? props.modelValue.length;
  const end = savedCursorPos.value?.end ?? textarea.selectionEnd ?? props.modelValue.length;
  const text = props.modelValue;
  const newValue = text.slice(0, start) + key + text.slice(end);
  emit('update:modelValue', newValue);
  // 更新保存的光标位置（表情后面），以便连续插入
  const newPos = start + key.length;
  savedCursorPos.value = { start: newPos, end: newPos };
  // 恢复光标位置
  nextTick(() => {
    textarea.setSelectionRange(newPos, newPos);
    textarea.focus();
    updateHeight(); // 直接调用，不走 rAF 节流
  });
};

// 监听 modelValue 变化（表情等程序化更新），调整高度
watch(
  () => props.modelValue,
  () => {
    nextTick(() => {
      adjustHeight();
    });
  },
);

onMounted(() => {
  registerClickOutside();
  // 获取初始高度
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
.reply-input-container {
  margin-top: 16px;
}

.reply-input-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reply-avatar {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.reply-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.reply-input-wrapper {
  flex: 1;
}

.reply-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  background: #fff;
  resize: none;
  overflow-y: hidden;
  min-height: 42px;
  max-height: 200px;
  line-height: 1.5;
  font-family: inherit;
  box-sizing: border-box;
  word-break: break-all;
}

.reply-input::placeholder {
  color: #bfbfbf;
}

.reply-input:focus {
  border-color: #d9d9d9;
}

.reply-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  margin-left: calc(32px + 12px); /* 头像宽度 + 间距 */
}

.reply-btns {
  display: flex;
  gap: 12px;
}

.cancel-btn {
  padding: 8px 20px;
  background: #000a200d;
  color: #262626;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.cancel-btn:hover {
  background: #000a201a;
}

.submit-btn {
  min-width: 72px;
  padding: 8px 20px;
  background: #2db55d;
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background: #26a452;
}

.submit-btn:disabled {
  background: #88d4a0;
  cursor: not-allowed;
}
</style>

<!-- 夜间模式样式（非scoped） -->
<style>
/* 按钮内容切换过渡 */
.btn-fade-enter-active,
.btn-fade-leave-active {
  transition: opacity 0.15s ease;
}

.btn-fade-enter-from,
.btn-fade-leave-to {
  opacity: 0;
}

.v-theme--dark .reply-input {
  background: var(--color-bg-light);
  border-color: var(--color-border);
  color: var(--color-text-primary);
}

.v-theme--dark .reply-input::placeholder {
  color: var(--color-text-placeholder);
}

.v-theme--dark .reply-input:focus {
  border-color: var(--color-border-focus);
}

.v-theme--dark .cancel-btn {
  background: var(--color-bg-light);
  color: var(--color-text-primary);
}

.v-theme--dark .cancel-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}
</style>
