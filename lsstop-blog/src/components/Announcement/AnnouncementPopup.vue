<template>
  <v-dialog v-model="dialogVisible" max-width="700" scroll-strategy="none" persistent>
    <v-card class="announcement-popup">
      <v-card-title class="popup-title">
        <v-icon size="20" class="mr-2">mdi-bullhorn-outline</v-icon>
        <span class="popup-title-text">{{ currentAnnouncement?.title }}</span>
      </v-card-title>
      <v-card-text class="popup-content">
        <div class="markdown-body" v-html="announcementHtml" />
      </v-card-text>
      <v-card-actions class="popup-actions">
        <v-checkbox
          v-model="todayNoShow"
          label="今日不再显示"
          density="compact"
          hide-details
          class="today-checkbox"
        />
        <v-spacer />
        <div class="popup-nav" v-if="popupList.length > 1">
          <v-btn variant="text" size="small" @click="prev">
            <v-icon size="18">mdi-chevron-left</v-icon>
            上一条
          </v-btn>
          <span class="popup-nav-index">{{ currentIndex + 1 }}/{{ popupList.length }}</span>
          <v-btn variant="text" size="small" @click="next">
            下一条
            <v-icon size="18">mdi-chevron-right</v-icon>
          </v-btn>
        </div>
        <v-btn variant="tonal" color="primary" @click="close">我知道了</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import useAnnouncementStore from '@/stores/modules/announcement';
import { markdownToHtml } from '@/utils/markdown';

const STORAGE_KEY = 'announcement_popup_hidden_date';

const announcementStore = useAnnouncementStore();
const popupList = computed(() => announcementStore.popupList);

const dialogVisible = ref(false);
const currentIndex = ref(0);
const todayNoShow = ref(false);

const currentAnnouncement = computed(() => {
  if (popupList.value.length === 0) return null;
  return popupList.value[currentIndex.value] || null;
});

const announcementHtml = computed(() => {
  if (!currentAnnouncement.value?.content) return '';
  return markdownToHtml(currentAnnouncement.value.content);
});

function getTodayString() {
  const now = new Date();
  return `${now.getFullYear()}-${now.getMonth() + 1}-${now.getDate()}`;
}

function shouldShow() {
  const hiddenDate = localStorage.getItem(STORAGE_KEY);
  const today = getTodayString();
  return hiddenDate !== today;
}

function prev() {
  if (popupList.value.length <= 1) return;
  currentIndex.value = currentIndex.value > 0 ? currentIndex.value - 1 : popupList.value.length - 1;
}

function next() {
  if (popupList.value.length <= 1) return;
  currentIndex.value = currentIndex.value < popupList.value.length - 1 ? currentIndex.value + 1 : 0;
}

function close() {
  if (todayNoShow.value) {
    localStorage.setItem(STORAGE_KEY, getTodayString());
  }
  dialogVisible.value = false;
}

// 监听公告列表，有弹窗公告时自动显示
watch(
  () => popupList.value,
  (list) => {
    if (list.length > 0 && shouldShow()) {
      currentIndex.value = 0;
      dialogVisible.value = true;
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.announcement-popup {
  border-radius: 12px !important;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15) !important;
}

.popup-title {
  font-size: 1.15rem;
  font-weight: 600;
  padding: 20px 24px 16px;
  display: flex;
  align-items: center;
  color: var(--color-primary, #8e8cd8);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.popup-title-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: rgba(0, 0, 0, 0.85);
  flex: 1;
  min-width: 0;
}

.popup-content {
  padding: 20px 24px 24px;
  font-size: 0.95rem;
  line-height: 1.85;
  color: rgba(0, 0, 0, 0.7);
  min-height: 100px;
  max-height: 300px;
  overflow-y: auto;
}

.popup-actions {
  padding: 16px 24px;
  display: flex;
  align-items: center;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.today-checkbox {
  flex-shrink: 0;
}

.today-checkbox :deep(.v-label) {
  font-size: 0.85rem;
  color: rgba(0, 0, 0, 0.55);
  opacity: 1;
}

.today-checkbox :deep(.v-selection-control__input > .v-icon) {
  color: rgba(0, 0, 0, 0.4);
}

.today-checkbox :deep(.v-selection-control--dirty .v-selection-control__input > .v-icon) {
  color: var(--color-primary, #8e8cd8);
}

.popup-nav {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-right: 12px;
}

.popup-nav-index {
  font-size: 0.8rem;
  color: rgba(0, 0, 0, 0.5);
  min-width: 36px;
  text-align: center;
}

/* 暗色主题 */
.v-theme--dark .announcement-popup {
  background-color: #2d2d2d !important;
}

.v-theme--dark .popup-title {
  color: #a5a3e8;
  border-bottom-color: rgba(255, 255, 255, 0.08);
}

.v-theme--dark .popup-title-text {
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .popup-content {
  color: rgba(255, 255, 255, 0.7);
}

.v-theme--dark .popup-actions {
  border-top-color: rgba(255, 255, 255, 0.08);
}

.v-theme--dark .today-checkbox :deep(.v-label) {
  color: rgba(255, 255, 255, 0.55);
}

.v-theme--dark .today-checkbox :deep(.v-selection-control__input > .v-icon) {
  color: rgba(255, 255, 255, 0.4);
}

.v-theme--dark
  .today-checkbox
  :deep(.v-selection-control--dirty .v-selection-control__input > .v-icon) {
  color: #a5a3e8;
}

.v-theme--dark .popup-nav-index {
  color: rgba(255, 255, 255, 0.5);
}
</style>
