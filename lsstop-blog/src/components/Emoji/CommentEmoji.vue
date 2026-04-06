<template>
  <div class="emoji-wrapper">
    <span class="emoji-item" v-for="(value, key) in EmojiList" :key="key" @click="addEmoji(key)">
      <v-img :lazy-src="value" :src="value" :title="key" class="emoji" width="24" height="24" />
    </span>
  </div>
</template>

<script setup lang="ts">
import EmojiList from '@/constants/emoji';

const emit = defineEmits<{
  (e: 'addEmoji', key: string): void;
}>();

function addEmoji(key: string): void {
  emit('addEmoji', key);
}
</script>

<style scoped>
.emoji {
  user-select: none;
  display: inline-block;
  vertical-align: middle;
  cursor: pointer;
}

.emoji :deep(img) {
  cursor: pointer;
}

.emoji-item {
  cursor: pointer;
  display: inline-block;
  padding: 4px;
  border-radius: 0.25rem;
  transition: background-color 0.2s;
}

.emoji-item:hover {
  background: #dddddd;
}

.emoji-wrapper {
  width: 275px;
  height: 200px;
  overflow: auto;
  scrollbar-width: thin;
  scrollbar-color: #49b1f5 transparent;
}

.emoji-wrapper::-webkit-scrollbar {
  width: 4px;
}

.emoji-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.emoji-wrapper::-webkit-scrollbar-thumb {
  background: #49b1f5;
  border-radius: 4px;
}

.emoji-wrapper::-webkit-scrollbar-thumb:hover {
  background: #3aa0e0;
}

@media (max-width: 640px) {
  .emoji-wrapper {
    width: 200px;
  }
}
</style>
