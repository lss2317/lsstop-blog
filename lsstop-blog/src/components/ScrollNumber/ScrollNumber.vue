<template>
  <span class="scroll-number">
    <template v-for="(char, index) in chars" :key="index">
      <span v-if="isDigit(char)" class="digit-wrapper">
        <span class="digit-inner" :style="{ transform: `translateY(-${Number(char) * 10}%)` }">
          <span v-for="n in 10" :key="n" class="digit-item">{{ n - 1 }}</span>
        </span>
      </span>
      <span v-else class="text-char">{{ char }}</span>
    </template>
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  value: string;
}>();

const chars = computed(() => props.value.split(''));

function isDigit(char: string): boolean {
  return /\d/.test(char);
}
</script>

<style scoped>
.scroll-number {
  display: inline-flex;
  align-items: baseline;
}

.digit-wrapper {
  display: inline-block;
  height: 1.2em;
  overflow: hidden;
  vertical-align: bottom;
}

.digit-inner {
  display: flex;
  flex-direction: column;
  transition: transform 0.3s ease-out;
}

.digit-item {
  height: 1.2em;
  line-height: 1.2em;
  text-align: center;
}

.text-char {
  display: inline;
}
</style>
