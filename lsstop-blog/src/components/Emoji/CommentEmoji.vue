<template>
  <div ref="emojiWrapper" class="emoji-wrapper" role="group" aria-label="选择表情">
    <section v-for="group in EmojiGroups" :key="group.id" class="emoji-group">
      <div class="emoji-group-title">{{ group.label }}</div>

      <div class="emoji-grid">
        <button
          v-for="emoji in group.emojis"
          :key="emoji.token"
          type="button"
          class="emoji-item"
          :data-emoji-token="emoji.token"
          :title="emoji.label"
          :aria-label="emoji.label"
          @click="addEmoji(emoji.token)"
          @mouseenter="startPreview(emoji)"
          @mouseleave="stopPreview(emoji.token)"
          @focus="startPreview(emoji)"
          @blur="stopPreview(emoji.token)"
        >
          <template v-if="isEmojiActivated(emoji.token)">
            <span
              v-if="isLottiePreview(emoji)"
              class="emoji-preview comment-emoji-lottie"
              :data-emoji-lottie="emoji.animatedUrl"
              :data-emoji-fallback="emoji.staticUrl"
              role="img"
              :aria-label="emoji.label"
            >
              <img :src="emoji.staticUrl" alt="" draggable="false" />
            </span>

            <img
              v-else
              :key="previewKey(emoji)"
              class="emoji-preview"
              :src="previewUrl(emoji)"
              :alt="emoji.label"
              width="28"
              height="28"
              decoding="async"
              draggable="false"
            />
          </template>

          <span v-else class="emoji-preview emoji-placeholder" aria-hidden="true" />
        </button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { EmojiGroups, type EmojiAsset } from '@/constants/emoji';
import { preloadEmojiLottiePlayer } from '@/utils/emojiLottie';
import { preloadEmojiAnimation, queueEmojiAnimationPreload } from '@/utils/emojiPreload';

const emit = defineEmits<{
  (e: 'addEmoji', key: string): void;
}>();

const previewingToken = ref<string | null>(null);
const activePreviewToken = ref<string | null>(null);
const activatedTokens = ref<ReadonlySet<string>>(new Set());
const emojiWrapper = ref<HTMLElement | null>(null);
const emojiByToken = new Map<string, EmojiAsset>(
  EmojiGroups.flatMap((group) => group.emojis.map((emoji) => [emoji.token, emoji])),
);
let visibilityObserver: IntersectionObserver | null = null;
const reduceMotion =
  typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

function isPreviewing(emoji: EmojiAsset): boolean {
  return !reduceMotion && previewingToken.value === emoji.token;
}

function isEmojiActivated(token: string): boolean {
  return activatedTokens.value.has(token);
}

function activateEmoji(emoji: EmojiAsset): void {
  if (!activatedTokens.value.has(emoji.token)) {
    const nextTokens = new Set(activatedTokens.value);
    nextTokens.add(emoji.token);
    activatedTokens.value = nextTokens;
  }

  if (reduceMotion || emoji.animationType === 'static') return;
  queueEmojiAnimationPreload(emoji);
  if (emoji.animationType === 'lottie') void preloadEmojiLottiePlayer();
}

function isLottiePreview(emoji: EmojiAsset): boolean {
  return isPreviewing(emoji) && emoji.animationType === 'lottie';
}

function previewUrl(emoji: EmojiAsset): string {
  return isPreviewing(emoji) && emoji.animationType === 'apng'
    ? emoji.animatedUrl
    : emoji.staticUrl;
}

function previewKey(emoji: EmojiAsset): string {
  const isAnimatedPreview = isPreviewing(emoji) && emoji.animationType !== 'static';
  return emoji.token + (isAnimatedPreview ? '-animated' : '-static');
}

async function startPreview(emoji: EmojiAsset): Promise<void> {
  activateEmoji(emoji);
  activePreviewToken.value = emoji.token;
  if (reduceMotion || emoji.animationType === 'static') return;

  const tasks: Promise<unknown>[] = [preloadEmojiAnimation(emoji)];
  if (emoji.animationType === 'lottie') tasks.push(preloadEmojiLottiePlayer());

  const [isReady] = await Promise.all(tasks).catch(() => [false]);
  if (isReady && activePreviewToken.value === emoji.token) {
    previewingToken.value = emoji.token;
  }
}

function stopPreview(token: string): void {
  if (activePreviewToken.value === token) activePreviewToken.value = null;
  if (previewingToken.value === token) previewingToken.value = null;
}

function addEmoji(token: string): void {
  emit('addEmoji', token);
}

onMounted(() => {
  if (!emojiWrapper.value) return;

  if (typeof IntersectionObserver === 'undefined') {
    EmojiGroups.flatMap((group) => group.emojis).forEach(activateEmoji);
    return;
  }

  visibilityObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        const token = (entry.target as HTMLElement).dataset.emojiToken;
        const emoji = token ? emojiByToken.get(token) : undefined;
        if (emoji) activateEmoji(emoji);
        visibilityObserver?.unobserve(entry.target);
      });
    },
    {
      root: emojiWrapper.value,
      rootMargin: '0px',
      threshold: 0.01,
    },
  );

  emojiWrapper.value
    .querySelectorAll<HTMLElement>('.emoji-item')
    .forEach((item) => visibilityObserver?.observe(item));
});

onBeforeUnmount(() => {
  visibilityObserver?.disconnect();
  visibilityObserver = null;
});
</script>

<style scoped>
.emoji-wrapper {
  width: 320px;
  height: 280px;
  box-sizing: border-box;
  padding: 2px 8px 8px;
  overflow-y: auto;
  overscroll-behavior: contain;
  scrollbar-width: thin;
  scrollbar-color: var(--color-primary, #49b1f5) transparent;
}

.emoji-group + .emoji-group {
  margin-top: 7px;
}

.emoji-group-title {
  margin: 0 0 3px;
  padding: 3px 0;
  color: var(--color-text-secondary, #8a8a8a);
  font-size: 11px;
  font-weight: 400;
  line-height: 16px;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(9, minmax(0, 1fr));
  gap: 2px;
  width: 100%;
}

.emoji-item {
  display: flex;
  width: 100%;
  height: 32px;
  min-width: 0;
  padding: 2px;
  align-items: center;
  justify-content: center;
  border: 0;
  border-radius: 8px;
  background: transparent;
  cursor: pointer;
  transition: background-color 0.12s ease;
}

.emoji-item:hover,
.emoji-item:focus-visible {
  outline: none;
  background: rgba(0, 0, 0, 0.06);
}

.emoji-preview {
  display: block;
  width: 28px;
  height: 28px;
  flex: none;
  overflow: hidden;
  object-fit: contain;
  user-select: none;
  pointer-events: none;
}

.emoji-preview :deep(img),
.emoji-preview :deep(svg) {
  display: block;
  width: 100%;
  height: 100%;
}

.emoji-wrapper::-webkit-scrollbar {
  width: 4px;
}

.emoji-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.emoji-wrapper::-webkit-scrollbar-thumb {
  border-radius: 4px;
  background: var(--color-primary, #49b1f5);
}

@media (max-width: 640px) {
  .emoji-wrapper {
    width: 260px;
    height: 260px;
  }

  .emoji-grid {
    grid-template-columns: repeat(7, minmax(0, 1fr));
  }
}
</style>
