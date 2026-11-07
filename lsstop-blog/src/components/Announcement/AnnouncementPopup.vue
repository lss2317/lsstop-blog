<template>
  <AnnouncementDialog
    v-if="currentAnnouncement"
    :key="currentAnnouncement.id"
    v-model="dialogVisible"
    :announcement="currentAnnouncement"
    rememberable
    @close="dismiss()"
    @remember="dismiss(true)"
    @after-leave="onAfterLeave"
  />
</template>

<script setup lang="ts">
import { computed, ref, shallowRef, watch } from 'vue';
import type { AnnouncementVo } from '@/apis/announcement';
import useAnnouncementStore from '@/stores/modules/announcement';
import AnnouncementDialog from './AnnouncementDialog.vue';

const STORAGE_KEY = 'announcement_popup_hidden_date';

const announcementStore = useAnnouncementStore();
const popupList = computed(() => announcementStore.popupList);
const displayedIds = new Set<number>();
const currentAnnouncement = shallowRef<AnnouncementVo | null>(null);
const dialogVisible = ref(false);

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
