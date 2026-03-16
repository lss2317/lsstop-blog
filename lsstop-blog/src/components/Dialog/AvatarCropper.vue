<template>
  <v-dialog
    v-model="visible"
    max-width="432"
    persistent
    scroll-strategy="none"
    transition="dialog-transition"
  >
    <div class="avatar-cropper-dialog">
      <!-- 关闭按钮 -->
      <button class="cropper-close-btn" @click="handleCancel">
        <v-icon size="24">mdi-close</v-icon>
      </button>

      <!-- 标题 -->
      <div class="cropper-header">
        <h3 class="cropper-title">上传头像</h3>
      </div>

      <!-- 裁剪区域 -->
      <div class="cropper-content">
        <canvas
          ref="canvasRef"
          :width="canvasSize * 2"
          :height="canvasSize * 2"
          :style="{
            width: canvasSize + 'px',
            height: canvasSize + 'px',
            cursor: isDragging ? 'grabbing' : 'grab',
          }"
          @mousedown="startDrag"
          @touchstart.prevent="startDrag"
          @touchmove.prevent="onDrag"
          @touchend="endDrag"
          @wheel.prevent="onWheel"
        />

        <!-- 缩放控制 -->
        <div class="cropper-controls">
          <button class="zoom-btn" :disabled="!ready" @click="zoomOut">
            <v-icon size="16">mdi-minus-circle-outline</v-icon>
          </button>
          <input
            v-model.number="scale"
            class="zoom-slider"
            type="range"
            :min="minScale"
            :max="maxScale"
            step="0.01"
            :disabled="!ready"
            :style="sliderBackground"
          />
          <button class="zoom-btn" :disabled="!ready" @click="zoomIn">
            <v-icon size="16">mdi-plus-circle-outline</v-icon>
          </button>
        </div>
      </div>

      <!-- 底部按钮 -->
      <div class="cropper-footer">
        <button class="cropper-btn cropper-btn-cancel" @click="handleCancel">取消</button>
        <button class="cropper-btn cropper-btn-save" :disabled="saveDisabled" @click="handleSave">
          <v-progress-circular v-if="loading" indeterminate size="16" width="2" />
          <span v-else>保存</span>
        </button>
      </div>
    </div>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onUnmounted } from 'vue';

interface Props {
  modelValue: boolean;
  imageFile: File | null;
  preloadedImage?: HTMLImageElement | null;
  loading?: boolean;
  /** 输出图片尺寸 */
  outputSize?: number;
}

const props = withDefaults(defineProps<Props>(), {
  preloadedImage: null,
  loading: false,
  outputSize: 400,
});

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  save: [file: File];
  cancel: [];
  error: [message: string];
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});

// ==================== 常量定义 ====================
const CANVAS_SIZE = 360; // 画布显示尺寸
const RETINA_SCALE = 2; // 高清缩放倍数
const MASK_PADDING = 20; // 遮罩边距（实际像素）
// 裁剪区域直径（显示像素）
// 注：此公式在 RETINA_SCALE = 2 时成立，修改 RETINA_SCALE 需重新推导
const CROP_DIAMETER = CANVAS_SIZE - MASK_PADDING;
const ZOOM_STEP = 0.1; // 按钮缩放步长
const WHEEL_ZOOM_STEP = 0.05; // 滚轮缩放步长

// Canvas 相关
const canvasRef = ref<HTMLCanvasElement | null>(null);
const canvasSize = CANVAS_SIZE;
const image = ref<HTMLImageElement | null>(null);

// 缩放相关
const scale = ref(1);
const minScale = ref(1);
// 确保有足够的放大空间（至少 2 倍或 +1）
const maxScale = computed(() => Math.max(minScale.value * 2, minScale.value + 1));

// 位置相关
const posX = ref(0);
const posY = ref(0);

// 拖拽相关
const isDragging = ref(false);
const dragStartX = ref(0);
const dragStartY = ref(0);
const startPosX = ref(0);
const startPosY = ref(0);

// 加载版本号（用于解决竞态问题）
let loadId = 0;

// 初始化完成状态
const ready = ref(false);

// 保存按钮禁用状态
const saveDisabled = computed(() => props.loading || !ready.value);

// 滑块背景样式
const sliderBackground = computed(() => {
  const range = maxScale.value - minScale.value;
  if (range <= 0) return { backgroundSize: '0% 100%' };
  const percent = ((scale.value - minScale.value) / range) * 100;
  return {
    backgroundSize: `${Math.max(0, Math.min(100, percent))}% 100%`,
  };
});

// 监听对话框打开
watch(visible, async (val) => {
  if (val) {
    // 等待 DOM 更新后再初始化
    await nextTick();
    // 如果有预加载的图片，检查有效性后直接使用
    if (props.preloadedImage?.complete && props.preloadedImage.naturalWidth > 0) {
      image.value = props.preloadedImage;
      initCropper();
    } else if (props.imageFile) {
      loadImage(props.imageFile);
    }
  } else {
    // 关闭时重置状态
    resetState();
  }
});

// 监听 imageFile 变化（弹窗已打开时重新选择图片的场景）
watch(
  () => props.imageFile,
  (file) => {
    if (visible.value && file && !props.preloadedImage) {
      loadImage(file);
    }
  },
);

// 重置状态
const resetState = () => {
  loadId++; // 使旧的加载任务失效
  ready.value = false;
  image.value = null;
  scale.value = 1;
  minScale.value = 1;
  posX.value = 0;
  posY.value = 0;
  isDragging.value = false;
  dragStartX.value = 0;
  dragStartY.value = 0;
  startPosX.value = 0;
  startPosY.value = 0;
  removeWindowListeners();
};

// 加载图片（使用 URL.createObjectURL 更快）
const loadImage = (file: File) => {
  const currentLoadId = ++loadId;
  const img = new Image();
  const url = URL.createObjectURL(file);

  img.onload = () => {
    URL.revokeObjectURL(url);
    // 竞态检查：确保这是最新的加载任务
    if (currentLoadId !== loadId) return;
    image.value = img;
    initCropper();
  };

  img.onerror = () => {
    URL.revokeObjectURL(url);
    // 竞态检查
    if (currentLoadId !== loadId) return;
    visible.value = false;
    emit('cancel');
  };

  img.src = url;
};

// 初始化裁剪器
const initCropper = async () => {
  if (!image.value) return;

  const img = image.value;
  // 计算最小缩放比例，让图片填满裁剪区域
  const scaleX = canvasSize / img.width;
  const scaleY = canvasSize / img.height;
  minScale.value = Math.max(scaleX, scaleY);

  // 居中
  posX.value = 0;
  posY.value = 0;

  // 等待 DOM 更新，确保 input 的 min/max 属性已生效
  // 否则 v-model 会被浏览器钳位到旧的 min/max 范围
  await nextTick();

  // 设置 scale 会触发 watch，自动调用 draw()
  scale.value = minScale.value;

  // 标记初始化完成
  ready.value = true;
};

// 绘制画布
const draw = () => {
  const canvas = canvasRef.value;
  const ctx = canvas?.getContext('2d');
  if (!canvas || !ctx || !image.value) return;

  const img = image.value;
  const displaySize = canvasSize * RETINA_SCALE; // 高清绘制
  const maskRadius = displaySize / 2 - MASK_PADDING; // 遮罩圆形半径

  // 清空画布
  ctx.clearRect(0, 0, displaySize, displaySize);

  // 填充背景
  ctx.fillStyle = '#f0f0f0';
  ctx.fillRect(0, 0, displaySize, displaySize);

  // 计算图片绘制参数
  const imgWidth = img.width * scale.value * RETINA_SCALE;
  const imgHeight = img.height * scale.value * RETINA_SCALE;
  const imgX = (displaySize - imgWidth) / 2 + posX.value * RETINA_SCALE;
  const imgY = (displaySize - imgHeight) / 2 + posY.value * RETINA_SCALE;

  // 绘制图片
  ctx.drawImage(img, imgX, imgY, imgWidth, imgHeight);

  // 绘制圆形遮罩（外部半透明）
  ctx.fillStyle = 'rgba(0, 0, 0, 0.5)';
  ctx.fillRect(0, 0, displaySize, displaySize);

  // 裁剪出圆形区域
  ctx.save();
  ctx.globalCompositeOperation = 'destination-out';
  ctx.beginPath();
  ctx.arc(displaySize / 2, displaySize / 2, maskRadius, 0, Math.PI * 2);
  ctx.fill();
  ctx.restore();

  // 重新绘制圆形区域内的图片
  ctx.save();
  ctx.beginPath();
  ctx.arc(displaySize / 2, displaySize / 2, maskRadius, 0, Math.PI * 2);
  ctx.clip();
  ctx.drawImage(img, imgX, imgY, imgWidth, imgHeight);
  ctx.restore();

  // 绘制圆形边框
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.8)';
  ctx.lineWidth = 2;
  ctx.beginPath();
  ctx.arc(displaySize / 2, displaySize / 2, maskRadius, 0, Math.PI * 2);
  ctx.stroke();
};

// 监听缩放变化
watch(scale, () => {
  constrainPosition();
  draw();
});

// 限制位置不超出边界
const constrainPosition = () => {
  if (!image.value) return;

  const img = image.value;
  const imgWidth = img.width * scale.value;
  const imgHeight = img.height * scale.value;

  // 计算可移动范围
  const maxOffsetX = Math.max(0, (imgWidth - canvasSize) / 2);
  const maxOffsetY = Math.max(0, (imgHeight - canvasSize) / 2);

  posX.value = Math.max(-maxOffsetX, Math.min(maxOffsetX, posX.value));
  posY.value = Math.max(-maxOffsetY, Math.min(maxOffsetY, posY.value));
};

// 拖拽事件
const startDrag = (e: MouseEvent | TouchEvent) => {
  const point = 'touches' in e ? e.touches[0] : e;
  if (!point) return;

  isDragging.value = true;
  dragStartX.value = point.clientX;
  dragStartY.value = point.clientY;
  startPosX.value = posX.value;
  startPosY.value = posY.value;

  // 绑定 window 事件，提升拖拽体验
  window.addEventListener('mousemove', onWindowMouseMove);
  window.addEventListener('mouseup', onWindowMouseUp);
};

const onDrag = (e: MouseEvent | TouchEvent) => {
  if (!isDragging.value) return;

  const point = 'touches' in e ? e.touches[0] : e;
  if (!point) return;

  const deltaX = point.clientX - dragStartX.value;
  const deltaY = point.clientY - dragStartY.value;

  posX.value = startPosX.value + deltaX;
  posY.value = startPosY.value + deltaY;

  constrainPosition();
  draw();
};

const onWindowMouseMove = (e: MouseEvent) => {
  onDrag(e);
};

const onWindowMouseUp = () => {
  endDrag();
};

const endDrag = () => {
  isDragging.value = false;
  removeWindowListeners();
};

const removeWindowListeners = () => {
  window.removeEventListener('mousemove', onWindowMouseMove);
  window.removeEventListener('mouseup', onWindowMouseUp);
};

// 组件卸载时清理
onUnmounted(() => {
  removeWindowListeners();
});

// 滚轮缩放
const onWheel = (e: WheelEvent) => {
  const delta = e.deltaY > 0 ? -WHEEL_ZOOM_STEP : WHEEL_ZOOM_STEP;
  scale.value = Math.max(minScale.value, Math.min(maxScale.value, scale.value + delta));
};

// 缩放按钮
const zoomIn = () => {
  scale.value = Math.min(maxScale.value, scale.value + ZOOM_STEP);
};

const zoomOut = () => {
  scale.value = Math.max(minScale.value, scale.value - ZOOM_STEP);
};

// 保存裁剪结果
const handleSave = () => {
  if (!image.value) return;

  const outputCanvas = document.createElement('canvas');
  const outputCtx = outputCanvas.getContext('2d');
  if (!outputCtx) return;

  const size = props.outputSize;
  outputCanvas.width = size;
  outputCanvas.height = size;

  const img = image.value;

  // 从显示坐标系到输出坐标系的缩放比（与 draw() 中的 RETINA_SCALE 逻辑一致）
  const ratio = size / CROP_DIAMETER;

  // 图片在输出画布中的尺寸和位置
  const imgWidth = img.width * scale.value * ratio;
  const imgHeight = img.height * scale.value * ratio;
  const imgX = (size - imgWidth) / 2 + posX.value * ratio;
  const imgY = (size - imgHeight) / 2 + posY.value * ratio;

  // 绘制方形图片（不做圆形裁剪，圆形只是预览参考）
  outputCtx.drawImage(img, imgX, imgY, imgWidth, imgHeight);

  // 转换为 Blob（使用 JPEG 格式，0.92 质量，在保持高清晰度的同时大幅减小文件体积）
  outputCanvas.toBlob(
    (blob) => {
      if (blob) {
        const file = new File([blob], 'avatar.jpg', { type: 'image/jpeg' });
        emit('save', file);
      } else {
        emit('error', '图片处理失败');
      }
    },
    'image/jpeg',
    0.92,
  );
};

// 取消（统一顺序：先关弹窗，再 emit）
const handleCancel = () => {
  visible.value = false;
  emit('cancel');
};
</script>

<style scoped>
.avatar-cropper-dialog {
  position: relative;
  display: inline-block;
  transform: none;
  overflow: hidden;
  border-radius: 13px;
  padding: 20px;
  padding-left: 24px;
  padding-right: 24px;
  text-align: left;
  background: #fff;
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.04),
    0 10px 28px -4px rgba(0, 0, 0, 0.16);
  min-width: 400px;
}

.cropper-close-btn {
  position: absolute;
  right: 0;
  top: 0;
  margin: 20px;
  cursor: pointer;
  border: none;
  background: transparent;
  border-radius: 4px;
  transition: all 0.2s;
  color: #8c8c8c;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cropper-close-btn:hover {
  color: #595959;
}

.cropper-header {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
}

.cropper-title {
  font-size: 18px;
  font-weight: 500;
  color: #262626;
  margin: 0;
}

.cropper-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.cropper-content canvas {
  touch-action: none;
  border-radius: 8px;
  animation: none !important; /* 覆盖全局 canvas 淡入动画 */
}

/* 对话框过渡动画 150ms */
:global(.dialog-transition-enter-active),
:global(.dialog-transition-leave-active) {
  transition: opacity 0.15s ease !important;
}

:global(.dialog-transition-enter-active .v-overlay__content),
:global(.dialog-transition-leave-active .v-overlay__content) {
  transition:
    transform 0.15s ease,
    opacity 0.15s ease !important;
}

:global(.dialog-transition-enter-from),
:global(.dialog-transition-leave-to) {
  opacity: 0;
}

:global(.dialog-transition-enter-from .v-overlay__content),
:global(.dialog-transition-leave-to .v-overlay__content) {
  transform: scale(0.95);
  opacity: 0;
}

.cropper-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 360px;
  margin-top: 20px;
}

.zoom-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #8c8c8c;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.zoom-btn:hover:not(:disabled) {
  color: #595959;
}

.zoom-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.zoom-slider:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.zoom-slider {
  width: 292px;
  height: 4px;
  appearance: none;
  border-radius: 4px;
  background: #e5e5e5;
  background-image: linear-gradient(#2db55d, #2db55d);
  background-repeat: no-repeat;
  cursor: pointer;
}

.zoom-slider::-webkit-slider-thumb {
  appearance: none;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #2db55d;
  cursor: pointer;
  border: 2px solid #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
}

.zoom-slider::-moz-range-thumb {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #2db55d;
  cursor: pointer;
  border: 2px solid #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
}

.cropper-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 48px;
}

.cropper-btn {
  border-radius: 4px;
  padding: 6px 12px;
  font-weight: 500;
  font-size: 14px;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.cropper-btn-cancel {
  background: #f5f5f5;
  color: #595959;
}

.cropper-btn-cancel:hover {
  background: #e8e8e8;
}

.cropper-btn-save {
  min-width: 56px;
  margin-left: 16px;
  background: #2db55d;
  color: #fff;
}

.cropper-btn-save:hover:not(:disabled) {
  background: #26a352;
}

.cropper-btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 响应式 */
@media (max-width: 480px) {
  .avatar-cropper-dialog {
    min-width: auto;
    width: 100%;
    border-radius: 0;
    padding: 16px;
  }

  .cropper-content canvas {
    width: 100% !important;
    height: auto !important;
    aspect-ratio: 1;
  }

  .cropper-controls {
    width: 100%;
  }

  .zoom-slider {
    flex: 1;
    margin: 0 12px;
    width: auto;
  }
}
</style>

<!-- 暗黑模式样式 -->
<style>
.v-theme--dark .avatar-cropper-dialog {
  background: #262626;
  border: 1px solid #404040;
}

.v-theme--dark .cropper-title {
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .cropper-close-btn {
  color: rgba(255, 255, 255, 0.45);
}

.v-theme--dark .cropper-close-btn:hover {
  color: rgba(255, 255, 255, 0.65);
}

.v-theme--dark .zoom-btn {
  color: rgba(255, 255, 255, 0.45);
}

.v-theme--dark .zoom-btn:hover:not(:disabled) {
  color: rgba(255, 255, 255, 0.65);
}

.v-theme--dark .zoom-slider {
  background-color: #404040;
}

.v-theme--dark .cropper-btn-cancel {
  background: #404040;
  color: rgba(255, 255, 255, 0.65);
}

.v-theme--dark .cropper-btn-cancel:hover {
  background: #525252;
}

.v-theme--dark .cropper-btn-save {
  background: #3cc16c;
}

.v-theme--dark .cropper-btn-save:hover:not(:disabled) {
  background: #4ece7b;
}
</style>
