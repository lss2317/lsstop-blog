<template>
  <v-dialog
    v-if="announcement"
    v-model="dialogVisible"
    :aria-labelledby="titleId"
    :opacity="1"
    class="announcement-overlay"
    :max-width="dialogMaxWidth"
    max-height="70dvh"
    transition="announcement-dialog-transition"
    persistent
    no-click-animation
    @keydown.esc.stop.prevent="emit('close')"
    @after-leave="emit('afterLeave')"
  >
    <section class="announcement-dialog">
      <header class="announcement-dialog__header">
        <h2 :id="titleId" class="announcement-dialog__title">
          <v-icon size="16" aria-hidden="true">mdi-bell</v-icon>
          <span>网站公告</span>
        </h2>
        <button
          type="button"
          class="announcement-dialog__close"
          aria-label="关闭公告"
          @click="emit('close')"
        >
          <v-icon size="18" aria-hidden="true">mdi-close</v-icon>
        </button>
      </header>

      <div class="announcement-dialog__body">
        <div class="markdown-body" v-html="announcementHtml" />
      </div>

      <footer class="announcement-dialog__actions">
        <button
          v-if="rememberable"
          type="button"
          class="announcement-dialog__remember"
          title="今日不再显示此公告"
          @click="emit('remember')"
        >
          我记住啦~
        </button>
        <button type="button" @click="emit('close')">确认</button>
      </footer>
    </section>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed, useId } from 'vue';
import type { AnnouncementVo } from '@/apis/announcement';
import { markdownToHtml, stripMarkdown } from '@/utils/markdown';

interface Props {
  modelValue: boolean;
  announcement: AnnouncementVo | null;
  rememberable?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  rememberable: false,
});

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  close: [];
  remember: [];
  afterLeave: [];
}>();

const titleId = useId();
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});
const announcementHtml = computed(() =>
  markdownToHtml(props.announcement?.content ?? '', { highlightCode: false }),
);
const dialogMaxWidth = computed(() => {
  const content = props.announcement?.content ?? '';
  const needsReadingWidth =
    stripMarkdown(content).length >= 220 || /<table\b/i.test(announcementHtml.value);

  return needsReadingWidth ? 'clamp(600px, 68vw, 780px)' : '600px';
});
</script>

<style scoped>
.announcement-dialog {
  --notice-surface: #fff;
  --notice-ink: #171717;
  --notice-muted: #757575;
  --notice-border: #f2f2f2;
  --notice-accent: var(--color-primary, #49b1f5);
  --notice-accent-hover: var(--color-primary-hover, #3da6e8);
  --notice-accent-tint: rgb(73 177 245 / 16%);
  --notice-padding: 20px;

  display: flex;
  flex-direction: column;
  min-height: min(180px, 70dvh);
  max-height: inherit;
  overflow: hidden;
  color: var(--notice-ink);
  background: linear-gradient(rgb(73 177 245 / 5%), transparent), var(--notice-surface);
  border: 1px solid var(--notice-border);
  border-radius: 15px;
}

.v-theme--dark .announcement-dialog {
  --notice-surface: #171717;
  --notice-ink: #f2f2f2;
  --notice-muted: #bcbcbc;
  --notice-border: #333;
}

.announcement-dialog__header,
.announcement-dialog__actions {
  display: flex;
  flex: none;
  align-items: center;
  gap: 10px;
  padding-inline: var(--notice-padding);
}

.announcement-dialog__header {
  justify-content: space-between;
  padding-block: 14px;
}

.announcement-dialog__title {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
  margin: 0;
  color: inherit;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.45;
  overflow-wrap: anywhere;
}

.announcement-dialog__title .v-icon {
  flex: none;
}

.announcement-dialog__close,
.announcement-dialog__actions button {
  border: 0;
  cursor: pointer;
  transition:
    background-color 180ms ease,
    color 180ms ease;
}

.announcement-dialog__close {
  display: grid;
  flex: none;
  place-items: center;
  width: 30px;
  height: 30px;
  padding: 0;
  color: var(--notice-muted);
  background: transparent;
  border-radius: 50%;
}

.announcement-dialog__close:hover {
  color: var(--notice-accent);
  background: var(--notice-accent-tint);
}

.announcement-dialog__body {
  flex: 1 1 auto;
  min-height: 0;
  padding-inline: var(--notice-padding);
  overflow: auto;
  overscroll-behavior: contain;
  scrollbar-width: thin;
  scrollbar-color: rgb(73 177 245 / 35%) transparent;
}

.announcement-dialog__body .markdown-body {
  --color-accent-fg: var(--notice-accent);

  color: var(--notice-ink);
  font-size: 15px;
  line-height: 1.5;
}

.announcement-dialog__body :deep(p) {
  font-size: inherit;
}

.announcement-dialog__body :deep(:is(h1, h2, h3, h4)) {
  margin-block: 16px 8px;
  padding-bottom: 0;
  font-size: 1em;
  border-bottom: 0;
}

.announcement-dialog__body :deep(.markdown-body > :first-child) {
  margin-top: 0;
}

.announcement-dialog__actions {
  justify-content: flex-end;
  padding-block: 17px 20px;
}

.announcement-dialog__actions button {
  min-height: 32px;
  padding: 5px 12px;
  color: var(--notice-accent);
  background: var(--notice-accent-tint);
  border-radius: 6px;
  font-size: 14px;
  white-space: nowrap;
}

.announcement-dialog__actions .announcement-dialog__remember {
  background: transparent;
}

.announcement-dialog__actions button:hover {
  color: var(--notice-surface);
  background: var(--notice-accent-hover);
}

.announcement-dialog button:focus-visible {
  outline: 2px solid var(--notice-accent);
  outline-offset: 2px;
}

:global(.announcement-overlay.v-overlay > .v-overlay__scrim) {
  background: rgb(0 0 0 / 50%) !important;
}

:global(.announcement-dialog-transition-enter-active),
:global(.announcement-dialog-transition-leave-active) {
  transition:
    opacity 260ms ease,
    transform 260ms ease;
}

:global(.announcement-dialog-transition-enter-from),
:global(.announcement-dialog-transition-leave-to) {
  opacity: 0;
  transform: scale(0.95);
}

@media (max-width: 480px) {
  :global(.announcement-overlay > .v-overlay__content) {
    width: calc(100% - 28px);
    margin: 14px;
  }

  .announcement-dialog {
    --notice-padding: 16px;
  }
}

@media (pointer: coarse) {
  .announcement-dialog__close,
  .announcement-dialog__actions button {
    min-width: 44px;
    min-height: 44px;
  }
}

@media (prefers-reduced-motion: reduce) {
  :global(.announcement-dialog-transition-enter-active),
  :global(.announcement-dialog-transition-leave-active),
  :global(.announcement-overlay > .v-overlay__scrim) {
    transition: none !important;
  }
}
</style>
