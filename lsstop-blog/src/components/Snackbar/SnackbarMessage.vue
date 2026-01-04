<template>
  <v-snackbar
    v-model="snackbarStore.show"
    :timeout="snackbarStore.timeout"
    location="top"
    content-class="custom-snackbar"
    :class="['snackbar-wrapper', `snackbar-${snackbarStore.type}`]"
  >
    <div class="snackbar-content">
      <div :class="['snackbar-icon', `icon-${snackbarStore.type}`]">
        <v-icon size="20">{{ snackbarStore.icon }}</v-icon>
      </div>
      <span class="snackbar-text">{{ snackbarStore.text }}</span>
    </div>
  </v-snackbar>
</template>

<script setup lang="ts">
import { useSnackbarStore } from '@/stores/modules/snackbar'

const snackbarStore = useSnackbarStore()
</script>

<style scoped>
.snackbar-wrapper :deep(.v-snackbar__wrapper) {
  border-radius: 12px;
  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.12),
    0 4px 8px rgba(0, 0, 0, 0.08);
  min-width: 280px;
  max-width: 420px;
}

.snackbar-wrapper :deep(.v-snackbar__content) {
  padding: 12px 16px;
}

/* 不同类型的背景色 */
.snackbar-success :deep(.v-snackbar__wrapper) {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.snackbar-error :deep(.v-snackbar__wrapper) {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.snackbar-warning :deep(.v-snackbar__wrapper) {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.snackbar-info :deep(.v-snackbar__wrapper) {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.snackbar-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.snackbar-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.2);
  flex-shrink: 0;
}

.snackbar-icon :deep(.v-icon) {
  color: #fff;
}

.snackbar-text {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.5;
  word-break: break-word;
  white-space: pre-wrap;
}
</style>
