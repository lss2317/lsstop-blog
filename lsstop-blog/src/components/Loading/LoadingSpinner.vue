<template>
  <div class="loading-spinner">
    <v-progress-circular indeterminate color="green" size="32" />
    <span>{{ text }}</span>
  </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{ text?: string }>(), {
  text: '加载中...',
});
</script>

<style scoped>
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  gap: 12px;
  color: #8c8c8c;
  font-size: 14px;
}
</style>
