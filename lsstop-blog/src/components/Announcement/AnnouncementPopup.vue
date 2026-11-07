<template>
  <v-dialog
    v-if="currentAnnouncement"
    :key="currentAnnouncement.id"
    v-model="dialogVisible"
    :aria-labelledby="titleId"
    :opacity="1"
    class="announcement-overlay"
    :max-width="dialogMaxWidth"
    max-height="70dvh"
    transition="announcement-dialog-transition"
    persistent
    no-click-animation
    @keydown.esc.stop.prevent="dismiss()"
    @after-leave="onAfterLeave"
  >
    <section class="announcement-popup">
      <header class="announcement-popup__header">
        <h2 :id="titleId" class="announcement-popup__title">
          <v-icon size="16" aria-hidden="true">mdi-bell</v-icon>
          <span>网站公告</span>
        </h2>
        <button
          type="button"
          class="announcement-popup__close"
          aria-label="关闭公告"
          @click="dismiss()"
        >
          <v-icon size="18" aria-hidden="true">mdi-close</v-icon>
        </button>
      </header>

      <div class="announcement-popup__body">
        <div class="markdown-body" v-html="announcementHtml" />
      </div>

      <footer class="announcement-popup__actions">
        <button
          type="button"
          class="announcement-popup__remember"
          title="今日不再显示此公告"
          @click="dismiss(true)"
        >
          我记住啦~
        </button>
        <button type="button" @click="dismiss()">确认</button>
      </footer>
    </section>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed, ref, shallowRef, useId, watch } from 'vue';
import type { AnnouncementVo } from '@/apis/announcement';
import useAnnouncementStore from '@/stores/modules/announcement';
import { markdownToHtml, stripMarkdown } from '@/utils/markdown';

const STORAGE_KEY = 'announcement_popup_hidden_date';

const announcementStore = useAnnouncementStore();
const titleId = useId();
const popupList = computed(() => announcementStore.popupList);
const displayedIds = new Set<number>();
const currentAnnouncement = shallowRef<AnnouncementVo | null>(null);
const dialogVisible = ref(false);
const announcementHtml = computed(() =>
  markdownToHtml(currentAnnouncement.value?.content ?? '', { highlightCode: false }),
);
const dialogMaxWidth = computed(() => {
  const content = currentAnnouncement.value?.content ?? '';
  const needsReadingWidth =
    stripMarkdown(content).length >= 220 || /<table\b/i.test(announcementHtml.value);

  return needsReadingWidth ? 'clamp(600px, 68vw, 780px)' : '600px';
});

function getTodayString() {
  const now = new Date();
  return `${now.getFullYear()}-${now.getMonth() + 1}-${now.getDate()}`;
}

function showNext() {
  if (currentAnnouncement.value) return;

  const today = getTodayString();
  // 保留旧版本已设置的当日全局免打扰，到次日自然失效。
  if (localStorage.getItem(STORAGE_KEY) === today) return;

  const next = popupList.value.find(
    ({ id }) => !displayedIds.has(id) && localStorage.getItem(`${STORAGE_KEY}:${id}`) !== today,
  );
  if (!next) return;

  currentAnnouncement.value = next;
  dialogVisible.value = true;
}

function dismiss(remember = false) {
  if (!dialogVisible.value || !currentAnnouncement.value) return;

  const { id } = currentAnnouncement.value;
  displayedIds.add(id);
  if (remember) localStorage.setItem(`${STORAGE_KEY}:${id}`, getTodayString());
  dialogVisible.value = false;
}

function onAfterLeave() {
  currentAnnouncement.value = null;
  showNext();
}

watch(popupList, showNext, { immediate: true });
</script>

<style scoped>
.announcement-popup {
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

.v-theme--dark .announcement-popup {
  --notice-surface: #171717;
  --notice-ink: #f2f2f2;
  --notice-muted: #bcbcbc;
  --notice-border: #333;
}

.announcement-popup__header,
.announcement-popup__actions {
  display: flex;
  flex: none;
  align-items: center;
  gap: 10px;
  padding-inline: var(--notice-padding);
}

.announcement-popup__header {
  justify-content: space-between;
  padding-block: 14px;
}

.announcement-popup__title {
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

.announcement-popup__title .v-icon {
  flex: none;
}

.announcement-popup__close,
.announcement-popup__actions button {
  border: 0;
  cursor: pointer;
  transition:
    background-color 180ms ease,
    color 180ms ease;
}

.announcement-popup__close {
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

.announcement-popup__close:hover {
  color: var(--notice-accent);
  background: var(--notice-accent-tint);
}

.announcement-popup__body {
  flex: 1 1 auto;
  min-height: 0;
  padding-inline: var(--notice-padding);
  overflow: auto;
  overscroll-behavior: contain;
  scrollbar-width: thin;
  scrollbar-color: rgb(73 177 245 / 35%) transparent;
}

.announcement-popup__body .markdown-body {
  --color-accent-fg: var(--notice-accent);

  color: var(--notice-ink);
  font-size: 15px;
  line-height: 1.5;
}

.announcement-popup__body :deep(p) {
  font-size: inherit;
}

.announcement-popup__body :deep(:is(h1, h2, h3, h4)) {
  margin-block: 16px 8px;
  padding-bottom: 0;
  font-size: 1em;
  border-bottom: 0;
}

.announcement-popup__body :deep(.markdown-body > :first-child) {
  margin-top: 0;
}

.announcement-popup__actions {
  justify-content: flex-end;
  padding-block: 17px 20px;
}

.announcement-popup__actions button {
  min-height: 32px;
  padding: 5px 12px;
  color: var(--notice-accent);
  background: var(--notice-accent-tint);
  border-radius: 6px;
  font-size: 14px;
  white-space: nowrap;
}

.announcement-popup__actions .announcement-popup__remember {
  background: transparent;
}

.announcement-popup__actions button:hover {
  color: var(--notice-surface);
  background: var(--notice-accent-hover);
}

.announcement-popup button:focus-visible {
  outline: 2px solid var(--notice-accent);
  outline-offset: 2px;
}

:global(.announcement-overlay.v-overlay > .v-overlay__scrim) {
  background: rgb(0 0 0 / 50%) !important;
  -webkit-backdrop-filter: blur(5px);
  backdrop-filter: blur(5px);
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

  .announcement-popup {
    --notice-padding: 16px;
  }
}

@media (pointer: coarse) {
  .announcement-popup__close,
  .announcement-popup__actions button {
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
