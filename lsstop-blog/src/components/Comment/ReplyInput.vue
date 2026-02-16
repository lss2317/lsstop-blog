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
        />
      </div>
    </div>
    <div class="reply-actions">
      <div class="lc-emoji-trigger-wrapper">
        <span
          ref="emojiTriggerRef"
          :class="['lc-tool-icon', showEmoji ? 'active' : '']"
          title="表情"
          @click="toggleEmoji"
        >
          <v-icon size="20">mdi-emoticon-outline</v-icon>
        </span>
        <div :class="['lc-emoji-panel', emojiDirection]" v-show="showEmoji">
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
          {{ submitting ? '提交中...' : '回复' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import CommentEmoji from '@/components/Emoji/CommentEmoji.vue';
import { useEmoji } from '@/composables/useEmoji';
import { adjustTextareaHeight } from '@/utils/format';
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
  addEmoji: [key: string];
}>();

const textareaRef = ref<HTMLTextAreaElement | null>(null);

const {
  showEmoji,
  emojiDirection,
  emojiTriggerRef,
  toggleEmoji,
  registerClickOutside,
  unregisterClickOutside,
} = useEmoji();

const onInput = (event: Event) => {
  const textarea = event.target as HTMLTextAreaElement;
  adjustTextareaHeight(textarea);
  emit('update:modelValue', textarea.value);
};

const addEmoji = (key: string) => {
  emit('addEmoji', key);
};

// 监听 modelValue 变化（表情等程序化更新），调整高度
watch(
  () => props.modelValue,
  () => {
    nextTick(() => {
      if (textareaRef.value) {
        adjustTextareaHeight(textareaRef.value);
      }
    });
  },
);

onMounted(() => {
  registerClickOutside();
});

onUnmounted(() => {
  unregisterClickOutside();
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
