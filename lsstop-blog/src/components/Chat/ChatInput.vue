<template>
  <div class="chat-footer">
    <!-- 表情框 -->
    <div class="chat-emoji-box" v-show="showEmoji" @mousedown.prevent>
      <CommentEmoji @addEmoji="addEmoji" />
    </div>
    <!-- 图片预览区 -->
    <div v-if="imageFiles.length > 0" class="chat-image-preview-bar">
      <div v-for="(img, idx) in imagePreviews" :key="idx" class="chat-image-preview-item">
        <img :src="img" alt="" @click="previewImages(imagePreviews, idx)" />
        <span class="chat-image-remove" @click="removeImage(idx)">
          <v-icon size="14">mdi-close</v-icon>
        </span>
      </div>
      <div
        v-if="imageFiles.length < maxImageCount"
        class="chat-image-add-btn"
        @click="triggerImageSelect"
      >
        <v-icon size="22" color="#8c8c8c">mdi-plus</v-icon>
      </div>
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
          <v-icon size="28">mdi-emoticon-outline</v-icon>
        </span>
        <span class="chat-action-icon" title="图片" @click="triggerImageSelect">
          <v-icon size="28">mdi-camera-outline</v-icon>
        </span>
        <span :class="['chat-send-icon', canSend ? 'active' : '']" title="发送" @click="handleSend">
          <v-icon size="16">mdi-arrow-up</v-icon>
        </span>
      </div>
    </div>
    <!-- 隐藏的文件选择 -->
    <input
      ref="fileInputRef"
      type="file"
      accept="image/*"
      multiple
      style="display: none"
      @change="handleFileChange"
    />
    <!-- 上传中遮罩 -->
    <div v-if="uploading" class="chat-upload-overlay">
      <v-progress-circular indeterminate size="24" width="2" color="#49b1f5" />
      <span>上传中...</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue';
import CommentEmoji from '@/components/Emoji/CommentEmoji.vue';
import { useEmoji } from '@/composables/useEmoji';
import { uploadChatImage } from '@/apis/chat';
import { validateImageFile } from '@/utils/validate';
import { useSnackbarStore } from '@/stores/modules/snackbar';
import { ResponseCode } from '@/constants/http';
import { getErrorMessage } from '@/utils/error';
import { previewImages } from '@/utils/photoPreview';

const emit = defineEmits<{
  send: [content: string, images?: string[]];
}>();

const snackbar = useSnackbarStore();
const content = ref('');
const textareaRef = ref<HTMLTextAreaElement | null>(null);
const fileInputRef = ref<HTMLInputElement | null>(null);
const initialHeight = ref(0);
const savedCursorPos = ref<{ start: number; end: number } | null>(null);

// 图片相关
const maxImageCount = 3;
const maxImageSizeMB = 5;
const imageFiles = ref<File[]>([]);
const imagePreviews = ref<string[]>([]);
const uploading = ref(false);

const { showEmoji, emojiTriggerRef, toggleEmoji, closeEmoji } = useEmoji();

// 是否可以发送（有文本或有图片）
const canSend = computed(() => content.value.trim() || imageFiles.value.length > 0);

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

// 触发图片选择
const triggerImageSelect = () => {
  fileInputRef.value?.click();
};

// Canvas 压缩图片（超过阈值才压缩）
const compressThreshold = 500 * 1024; // 500KB
const compressMaxWidth = 1920;
const compressQuality = 0.85;

const compressImage = (file: File): Promise<File> => {
  // 小于阈值或非可压缩格式，直接返回
  if (file.size <= compressThreshold || !file.type.startsWith('image/')) {
    return Promise.resolve(file);
  }
  return new Promise((resolve) => {
    const img = new Image();
    const url = URL.createObjectURL(file);
    img.onload = () => {
      URL.revokeObjectURL(url);
      // 计算缩放尺寸
      let { width, height } = img;
      if (width > compressMaxWidth) {
        height = Math.round(height * (compressMaxWidth / width));
        width = compressMaxWidth;
      }
      const canvas = document.createElement('canvas');
      canvas.width = width;
      canvas.height = height;
      const ctx = canvas.getContext('2d')!;
      ctx.drawImage(img, 0, 0, width, height);
      canvas.toBlob(
        (blob) => {
          if (blob && blob.size < file.size) {
            resolve(new File([blob], file.name, { type: blob.type }));
          } else {
            // 压缩后反而更大，返回原文件
            resolve(file);
          }
        },
        'image/jpeg',
        compressQuality,
      );
    };
    img.onerror = () => {
      URL.revokeObjectURL(url);
      resolve(file);
    };
    img.src = url;
  });
};

// 处理文件选择
const handleFileChange = async (event: Event) => {
  const input = event.target as HTMLInputElement;
  const files = input.files;
  if (!files) return;

  const remaining = maxImageCount - imageFiles.value.length;
  const filesToAdd = Array.from(files).slice(0, remaining);

  for (const file of filesToAdd) {
    const validation = validateImageFile(file, maxImageSizeMB);
    if (!validation.valid) {
      snackbar.error(validation.error!);
      continue;
    }
    const compressed = await compressImage(file);
    // 压缩后仍超限则跳过
    if (compressed.size > maxImageSizeMB * 1024 * 1024) {
      snackbar.error(`图片压缩后仍超过 ${maxImageSizeMB}MB，请更换图片`);
      continue;
    }
    imageFiles.value.push(compressed);
    imagePreviews.value.push(URL.createObjectURL(compressed));
  }

  if (files.length > remaining) {
    snackbar.warning(`最多只能发送 ${maxImageCount} 张图片`);
  }

  // 清空 input，允许重复选择
  input.value = '';
};

// 移除图片
const removeImage = (index: number) => {
  URL.revokeObjectURL(imagePreviews.value[index]!);
  imageFiles.value.splice(index, 1);
  imagePreviews.value.splice(index, 1);
};

// 清空图片
const clearImages = () => {
  imagePreviews.value.forEach((url) => URL.revokeObjectURL(url));
  imageFiles.value = [];
  imagePreviews.value = [];
};

// 并行上传所有图片，返回 URL 列表
const uploadImages = async (): Promise<string[]> => {
  const results = await Promise.all(imageFiles.value.map((file) => uploadChatImage(file)));
  return results.map((res) => {
    if (res.code === ResponseCode.SUCCESS && res.data) return res.data;
    throw new Error(res.msg || '图片上传失败');
  });
};

// 发送消息
const handleSend = async () => {
  if (!canSend.value || uploading.value) return;

  let imageUrls: string[] | undefined;

  // 如果有图片，先上传
  if (imageFiles.value.length > 0) {
    uploading.value = true;
    try {
      imageUrls = await uploadImages();
    } catch (error) {
      snackbar.error(getErrorMessage(error));
      uploading.value = false;
      return;
    }
    uploading.value = false;
  }

  const text = content.value;
  emit('send', text, imageUrls);
  content.value = '';
  clearImages();
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
  clearImages();
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
  scrollbar-width: thin;
  scrollbar-color: rgba(0, 0, 0, 0.15) transparent;
  color: #262626;
}

.chat-textarea::-webkit-scrollbar {
  width: 3px;
}

.chat-textarea::-webkit-scrollbar-track {
  background: transparent;
}

.chat-textarea::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.15);
  border-radius: 3px;
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

/* 图片预览区 */
.chat-image-preview-bar {
  display: flex;
  gap: 8px;
  padding: 8px 4px;
  overflow-x: auto;
  scrollbar-width: none;
}

.chat-image-preview-bar::-webkit-scrollbar {
  display: none;
}

.chat-image-preview-item {
  position: relative;
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
}

.chat-image-preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.chat-image-remove {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}

.chat-image-remove:hover {
  background: rgba(0, 0, 0, 0.7);
}

.chat-image-add-btn {
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  border-radius: 8px;
  border: 1.5px dashed #d0d0d0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.2s;
}

.chat-image-add-btn:hover {
  border-color: #49b1f5;
}

/* 上传中遮罩 */
.chat-upload-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 13px;
  color: #49b1f5;
  border-radius: 0 0 1rem 1rem;
  z-index: 20;
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

.v-theme--dark .chat-image-preview-item {
  border-color: #444;
}

.v-theme--dark .chat-image-add-btn {
  border-color: #444;
}

.v-theme--dark .chat-image-add-btn:hover {
  border-color: #49b1f5;
}

.v-theme--dark .chat-upload-overlay {
  background: rgba(30, 30, 30, 0.85);
}
</style>
