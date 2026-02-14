<template>
  <v-dialog v-model="visible" :max-width="maxWidth" persistent scroll-strategy="none">
    <v-card class="confirm-dialog-card">
      <div class="confirm-dialog-header">
        <div class="confirm-dialog-icon" :style="{ color: iconColor }">
          <v-icon size="40">{{ iconName }}</v-icon>
        </div>
        <div class="confirm-dialog-content">
          <div class="confirm-dialog-title">{{ title }}</div>
          <div class="confirm-dialog-text">{{ content }}</div>
        </div>
      </div>
      <div class="confirm-dialog-actions">
        <button class="confirm-btn cancel-btn" @click="handleCancel">{{ cancelText }}</button>
        <button class="confirm-btn primary-btn" :disabled="loading" @click="handleConfirm">
          <v-progress-circular v-if="loading" indeterminate size="16" width="2" />
          <span v-else>{{ confirmText }}</span>
        </button>
      </div>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useTheme } from 'vuetify';

/** 弹框类型 */
type DialogType = 'success' | 'error' | 'warning' | 'info';

interface Props {
  modelValue: boolean;
  type?: DialogType;
  title?: string;
  content?: string;
  confirmText?: string;
  confirmColor?: string;
  cancelText?: string;
  maxWidth?: number | string;
  loading?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  type: 'warning',
  title: '确定执行此操作？',
  content: '此操作无法撤销',
  confirmText: '确定',
  confirmColor: 'error',
  cancelText: '取消',
  maxWidth: 400,
  loading: false,
});

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  confirm: [];
  cancel: [];
}>();

const theme = useTheme();

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});

// 图标配置映射
const iconConfig: Record<DialogType, { icon: string; color: string; darkColor: string }> = {
  warning: { icon: 'mdi-help-circle', color: '#faad14', darkColor: '#fac31d' },
  error: { icon: 'mdi-close-circle', color: '#f63636', darkColor: '#f8615c' },
  success: { icon: 'mdi-check-circle', color: '#01b328', darkColor: '#28c244' },
  info: { icon: 'mdi-information', color: '#007aff', darkColor: '#1a90ff' },
};

const iconName = computed(() => iconConfig[props.type].icon);
const iconColor = computed(() => {
  const isDark = theme.global.current.value.dark;
  return isDark ? iconConfig[props.type].darkColor : iconConfig[props.type].color;
});
const handleConfirm = () => {
  emit('confirm');
};

const handleCancel = () => {
  visible.value = false;
  emit('cancel');
};
</script>

<style scoped>
.confirm-dialog-card {
  border-radius: 13px !important;
  padding: 20px;
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.04),
    0 10px 28px -4px rgba(0, 0, 0, 0.16) !important;
}

.confirm-dialog-header {
  display: flex;
  gap: 20px;
}

.confirm-dialog-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.confirm-dialog-content {
  flex: 1;
  min-width: 0;
}

.confirm-dialog-title {
  font-size: 18px;
  font-weight: 500;
  color: #1a1a1a;
  line-height: 1.5;
}

.confirm-dialog-text {
  font-size: 14px;
  color: #595959;
  line-height: 1.6;
  margin-top: 16px;
}

.confirm-dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 16px;
}

.confirm-btn {
  padding: 6px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cancel-btn {
  background: rgba(0, 0, 0, 0.05);
  border: none;
  color: #595959;
}

.cancel-btn:hover {
  background: rgba(0, 0, 0, 0.1);
}

.primary-btn {
  background: #f63636;
  border: none;
  color: #fff;
}

.primary-btn:hover:not(:disabled) {
  background: #cc2227;
}

.primary-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 暗黑模式 */
:deep(.v-theme--dark) .confirm-dialog-card,
.v-theme--dark .confirm-dialog-card {
  background: #262626;
}

.v-theme--dark .confirm-dialog-title {
  color: #f5f5f5;
}

.v-theme--dark .confirm-dialog-text {
  color: #b7b7b7;
}

.v-theme--dark .cancel-btn {
  background: rgba(255, 255, 255, 0.1);
  color: #b7b7b7;
}

.v-theme--dark .cancel-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}

.v-theme--dark .primary-btn {
  background: #f8615c;
}

.v-theme--dark .primary-btn:hover:not(:disabled) {
  background: #fa877f;
}
</style>
