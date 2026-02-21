<template>
  <div class="announcement-swiper">
    <div class="announcement-header">
      <v-icon size="18" class="announcement-icon">mdi-bullhorn-outline</v-icon>
      <span class="announcement-label">公告</span>
      <div class="announcement-indicator" v-if="list.length > 1">
        <!-- 公告少时显示小点 -->
        <template v-if="list.length <= 5">
          <span
            v-for="(_, index) in list"
            :key="index"
            class="indicator-dot"
            :class="{ active: currentIndex % list.length === index }"
            @click.stop="goToIndex(index)"
          />
        </template>
        <!-- 公告多时显示数字+箭头 -->
        <template v-else>
          <v-icon size="16" class="indicator-arrow" @click.stop="prev">mdi-chevron-left</v-icon>
          <span class="indicator-text">{{ displayIndex }}/{{ list.length }}</span>
          <v-icon size="16" class="indicator-arrow" @click.stop="next">mdi-chevron-right</v-icon>
        </template>
      </div>
    </div>
    <div class="announcement-scroll" ref="scrollRef">
      <div
        class="announcement-track"
        :style="{ transform: transform }"
        :class="{ 'no-transition': isResetting }"
      >
        <template v-if="list.length > 0">
          <div
            v-for="(item, index) in list"
            :key="index"
            class="announcement-item"
            :style="{ width: itemWidth + 'px' }"
          >
            <div class="announcement-title">{{ item.title }}</div>
            <div class="announcement-content">{{ stripMarkdown(item.content) }}</div>
            <div class="announcement-action" @click="handleClick">查看详情</div>
          </div>
          <div
            v-for="(item, index) in list"
            :key="'copy-' + index"
            class="announcement-item"
            :style="{ width: itemWidth + 'px' }"
          >
            <div class="announcement-title">{{ item.title }}</div>
            <div class="announcement-content">{{ stripMarkdown(item.content) }}</div>
            <div class="announcement-action" @click="handleClick">查看详情</div>
          </div>
        </template>
        <div v-else class="announcement-item announcement-empty">
          <span>暂无公告</span>
        </div>
      </div>
    </div>
  </div>

  <!-- 公告详情弹框 -->
  <v-dialog v-model="dialogVisible" max-width="700" scroll-strategy="none">
    <v-card class="announcement-dialog">
      <v-card-title class="dialog-title">
        <v-icon size="20" class="mr-2">mdi-bullhorn-outline</v-icon>
        <span class="dialog-title-text">{{ currentAnnouncement?.title }}</span>
      </v-card-title>
      <v-card-text class="dialog-content">
        <div class="markdown-body" v-html="announcementHtml" />
      </v-card-text>
      <v-card-actions class="dialog-actions">
        <span class="dialog-time">{{ dateFormat.datetime(currentAnnouncement?.createTime) }}</span>
        <v-spacer />
        <div class="dialog-nav" v-if="list.length > 1">
          <v-btn variant="text" size="small" @click="prevAnnouncement">
            <v-icon size="18">mdi-chevron-left</v-icon>
            上一条
          </v-btn>
          <span class="dialog-nav-index">{{ dialogIndex + 1 }}/{{ list.length }}</span>
          <v-btn variant="text" size="small" @click="nextAnnouncement">
            下一条
            <v-icon size="18">mdi-chevron-right</v-icon>
          </v-btn>
        </div>
        <v-btn variant="text" @click="dialogVisible = false">关闭</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import type { AnnouncementVo } from '@/apis/announcement';
import { dateFormat } from '@/utils/date';
import { markdownToHtml, stripMarkdown } from '@/utils/markdown';

interface Props {
  list: AnnouncementVo[];
  interval?: number;
}

const props = withDefaults(defineProps<Props>(), {
  interval: 4000,
});

const scrollRef = ref<HTMLElement | null>(null);
const itemWidth = ref(0);
const currentIndex = ref(0);
const isResetting = ref(false);
const dialogVisible = ref(false);
const currentAnnouncement = ref<AnnouncementVo | null>(null);
const dialogIndex = ref(0);

let timer: ReturnType<typeof setInterval> | null = null;

const displayIndex = computed(() => {
  const index = currentIndex.value % props.list.length;
  return index + 1;
});

const announcementHtml = computed(() => {
  if (!currentAnnouncement.value?.content) return '';
  return markdownToHtml(currentAnnouncement.value.content);
});

const transform = computed(() => `translateX(-${currentIndex.value * itemWidth.value}px)`);

function updateWidth() {
  if (scrollRef.value) {
    itemWidth.value = scrollRef.value.offsetWidth;
  }
}

function startScroll() {
  if (timer || props.list.length <= 1) return;
  timer = setInterval(() => {
    if (currentIndex.value < props.list.length) {
      currentIndex.value++;
    } else {
      isResetting.value = true;
      currentIndex.value = 0;
      setTimeout(() => {
        isResetting.value = false;
      }, 50);
    }
  }, props.interval);
}

function stopScroll() {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
}

function handleClick() {
  if (props.list.length === 0) return;
  const index = currentIndex.value >= props.list.length ? 0 : currentIndex.value;
  const announcement = props.list[index];
  if (!announcement) return;
  dialogIndex.value = index;
  currentAnnouncement.value = announcement;
  dialogVisible.value = true;
}

function prevAnnouncement() {
  if (props.list.length <= 1) return;
  dialogIndex.value = dialogIndex.value > 0 ? dialogIndex.value - 1 : props.list.length - 1;
  currentAnnouncement.value = props.list[dialogIndex.value] || null;
}

function nextAnnouncement() {
  if (props.list.length <= 1) return;
  dialogIndex.value = dialogIndex.value < props.list.length - 1 ? dialogIndex.value + 1 : 0;
  currentAnnouncement.value = props.list[dialogIndex.value] || null;
}

function goToIndex(index: number) {
  currentIndex.value = index;
}

function prev() {
  if (currentIndex.value > 0) {
    currentIndex.value--;
  } else {
    currentIndex.value = props.list.length - 1;
  }
}

function next() {
  if (currentIndex.value < props.list.length - 1) {
    currentIndex.value++;
  } else {
    currentIndex.value = 0;
  }
}

onMounted(() => {
  updateWidth();
  window.addEventListener('resize', updateWidth);
});

watch(
  () => props.list,
  (newList) => {
    if (newList.length > 1) {
      startScroll();
    }
  },
  { immediate: true },
);

onUnmounted(() => {
  stopScroll();
  window.removeEventListener('resize', updateWidth);
});
</script>

<style scoped>
.announcement-swiper {
  padding: 12px 16px;
}

.announcement-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.announcement-label {
  font-size: 0.9rem;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.85);
}

.announcement-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: auto;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
  cursor: pointer;
}

.indicator-dot:hover {
  background-color: rgba(0, 0, 0, 0.3);
}

.indicator-dot.active {
  background-color: var(--color-primary, #8e8cd8);
  width: 18px;
  border-radius: 4px;
}

.indicator-arrow {
  cursor: pointer;
  color: rgba(0, 0, 0, 0.45);
  transition: color 0.2s;
}

.indicator-arrow:hover {
  color: var(--color-primary, #8e8cd8);
}

.indicator-text {
  font-size: 0.75rem;
  color: rgba(0, 0, 0, 0.45);
  background-color: rgba(0, 0, 0, 0.06);
  padding: 2px 8px;
  border-radius: 10px;
}

.announcement-scroll {
  overflow: hidden;
}

.announcement-track {
  display: flex;
  transition: transform 0.5s ease;
}

.announcement-track.no-transition {
  transition: none;
}

.announcement-item {
  flex-shrink: 0;
  min-width: 0;
}

.announcement-action {
  text-align: right;
  margin-top: 8px;
  font-size: 0.8rem;
  color: var(--color-primary, #8e8cd8);
  cursor: pointer;
  transition: opacity 0.2s;
}

.announcement-action:hover {
  opacity: 0.8;
}

.announcement-title {
  font-size: 0.9rem;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.85);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}

.announcement-content {
  font-size: 0.8rem;
  color: rgba(0, 0, 0, 0.6);
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  min-height: calc(1.6em * 4);
}

.announcement-empty {
  color: rgba(0, 0, 0, 0.45);
  font-size: 0.85rem;
  text-align: center;
  padding: 8px 0;
}

.announcement-icon {
  color: #4c4948;
}

/* 弹框样式 */
.announcement-dialog {
  border-radius: 12px !important;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15) !important;
}

.dialog-title {
  font-size: 1.15rem;
  font-weight: 600;
  padding: 24px 24px 16px;
  display: flex;
  align-items: center;
  overflow: hidden;
  color: var(--color-primary, #8e8cd8);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.dialog-title .mr-2 {
  flex-shrink: 0;
}

.dialog-title-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: rgba(0, 0, 0, 0.85);
}

.dialog-content {
  padding: 20px 24px 24px;
  font-size: 0.95rem;
  line-height: 1.85;
  white-space: pre-wrap;
  color: rgba(0, 0, 0, 0.7);
  min-height: 100px;
  max-height: 300px;
  overflow-y: auto;
}

.dialog-actions {
  padding: 16px 24px;
  display: flex;
  align-items: center;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.dialog-nav {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-right: 8px;
}

.dialog-nav-index {
  font-size: 0.8rem;
  color: rgba(0, 0, 0, 0.5);
  min-width: 36px;
  text-align: center;
}

.dialog-time {
  font-size: 0.8rem;
  color: rgba(0, 0, 0, 0.45);
}

/* 暗色主题 */
.v-theme--dark .announcement-icon {
  color: rgba(255, 255, 255, 0.7);
}

.v-theme--dark .announcement-label {
  color: rgba(255, 255, 255, 0.85);
}

.v-theme--dark .indicator-dot {
  background-color: rgba(255, 255, 255, 0.2);
}

.v-theme--dark .indicator-dot:hover {
  background-color: rgba(255, 255, 255, 0.4);
}

.v-theme--dark .indicator-text {
  color: rgba(255, 255, 255, 0.5);
  background-color: rgba(255, 255, 255, 0.1);
}

.v-theme--dark .indicator-arrow {
  color: rgba(255, 255, 255, 0.5);
}

.v-theme--dark .indicator-arrow:hover {
  color: #a5a3e8;
}

.v-theme--dark .announcement-title {
  color: rgba(255, 255, 255, 0.85);
}

.v-theme--dark .announcement-content {
  color: rgba(255, 255, 255, 0.6);
}

.v-theme--dark .announcement-empty {
  color: rgba(255, 255, 255, 0.45);
}

.v-theme--dark .announcement-dialog {
  background-color: #2d2d2d !important;
}

.v-theme--dark .dialog-title {
  color: #a5a3e8;
  border-bottom-color: rgba(255, 255, 255, 0.08);
}

.v-theme--dark .dialog-title-text {
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .dialog-content {
  color: rgba(255, 255, 255, 0.7);
}

.v-theme--dark .dialog-actions {
  border-top-color: rgba(255, 255, 255, 0.08);
}

.v-theme--dark .dialog-time {
  color: rgba(255, 255, 255, 0.45);
}

.v-theme--dark .dialog-nav-index {
  color: rgba(255, 255, 255, 0.5);
}
</style>
