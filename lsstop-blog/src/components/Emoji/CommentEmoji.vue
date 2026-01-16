<template>
  <div class="emoji-wrapper">
    <span
      class="emoji-item"
      v-for="(value, key, index) of EmojiList"
      :key="index"
      @click="addEmoji(key)"
    >
      <v-img :lazy-src="value" :src="value" :title="key" class="emoji" width="24" height="24" />
    </span>
  </div>
</template>

<script setup lang="ts">
import EmojiList from '@/constants/emoji'

const emit = defineEmits<{
  (e: 'addEmoji', key: string): void
}>()

function addEmoji(key: string): void {
  emit('addEmoji', key)
}
</script>

<style scoped>
.emoji {
  user-select: none;
  margin: 0.4rem;
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
}

.emoji-item:hover {
  transition: all 0.2s;
  border-radius: 0.25rem;
  background: #dddddd;
}

:global(.v-theme--dark) .emoji :deep(img) {
  background-color: #2d2d3a;
}

.emoji-wrapper {
  width: 275px;
  height: 200px;
  overflow: auto;
}
</style>
