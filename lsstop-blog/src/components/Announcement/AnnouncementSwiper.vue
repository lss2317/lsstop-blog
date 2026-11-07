<template>
  <div class="announcement-swiper">
    <div class="announcement-header">
      <span class="announcement-header-icon">
        <v-icon size="14">mdi-bullhorn-outline</v-icon>
      </span>
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
            <div class="announcement-action" @click="handleClick(item)">
              查看详情
              <v-icon size="14" class="action-arrow">mdi-chevron-right</v-icon>
            </div>
          </div>
          <div
            v-for="(item, index) in list"
            :key="'copy-' + index"
            class="announcement-item"
            :style="{ width: itemWidth + 'px' }"
          >
            <div class="announcement-title">{{ item.title }}</div>
            <div class="announcement-content">{{ stripMarkdown(item.content) }}</div>
            <div class="announcement-action" @click="handleClick(item)">
              查看详情
              <v-icon size="14" class="action-arrow">mdi-chevron-right</v-icon>
            </div>
          </div>
        </template>
        <div v-else class="announcement-item announcement-empty">
          <v-icon size="32" class="empty-icon">mdi-bullhorn-variant-outline</v-icon>
          <span>暂无公告</span>
        </div>
      </div>
    </div>
  </div>

  <AnnouncementDialog
    v-if="currentAnnouncement"
    :key="currentAnnouncement.id"
    v-model="dialogVisible"
    :announcement="currentAnnouncement"
    @close="closeDialog"
    @after-leave="currentAnnouncement = null"
  />
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import type { AnnouncementVo } from '@/apis/announcement';
import { stripMarkdown } from '@/utils/markdown';
import AnnouncementDialog from './AnnouncementDialog.vue';

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

let timer: ReturnType<typeof setInterval> | null = null;

const displayIndex = computed(() => {
  const index = currentIndex.value % props.list.length;
  return index + 1;
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

function handleClick(announcement: AnnouncementVo) {
  currentAnnouncement.value = announcement;
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
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
  padding: 16px 20px;
}

.announcement-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.announcement-header-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 6px;
  background: linear-gradient(135deg, rgba(73, 177, 245, 0.12), rgba(73, 177, 245, 0.06));
  color: var(--color-primary, #49b1f5);
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
  background-color: var(--color-primary, #49b1f5);
  width: 18px;
  border-radius: 4px;
}

.indicator-arrow {
  cursor: pointer;
  color: rgba(0, 0, 0, 0.45);
  transition: color 0.2s;
}

.indicator-arrow:hover {
  color: var(--color-primary, #49b1f5);
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
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 2px;
  margin-top: 12px;
  font-size: 0.8rem;
  color: var(--color-primary, #49b1f5);
  cursor: pointer;
  transition: all 0.2s;
}

.announcement-action:hover {
  opacity: 0.8;
}

.announcement-action:hover .action-arrow {
  transform: translateX(2px);
}

.action-arrow {
  transition: transform 0.2s;
}

.announcement-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.88);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 6px;
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
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  color: rgba(0, 0, 0, 0.35);
  font-size: 0.85rem;
  padding: 16px 0;
  gap: 8px;
}

.empty-icon {
  opacity: 0.6;
}

.v-theme--dark .announcement-header-icon {
  background: linear-gradient(135deg, rgba(73, 177, 245, 0.18), rgba(73, 177, 245, 0.08));
  color: var(--color-primary);
}

/* 暗色主题 */
.v-theme--dark .announcement-label {
  color: rgba(255, 255, 255, 0.85);
}

.v-theme--dark .indicator-dot {
  background-color: rgba(255, 255, 255, 0.2);
}

.v-theme--dark .indicator-dot:hover {
  background-color: rgba(255, 255, 255, 0.4);
}

.v-theme--dark .indicator-dot.active {
  background-color: var(--color-primary, #49b1f5);
}

.v-theme--dark .indicator-text {
  color: rgba(255, 255, 255, 0.5);
  background-color: rgba(255, 255, 255, 0.1);
}

.v-theme--dark .indicator-arrow {
  color: rgba(255, 255, 255, 0.5);
}

.v-theme--dark .indicator-arrow:hover {
  color: var(--color-primary);
}

.v-theme--dark .announcement-title {
  color: rgba(255, 255, 255, 0.85);
}

.v-theme--dark .announcement-content {
  color: rgba(255, 255, 255, 0.6);
}

.v-theme--dark .announcement-empty {
  color: rgba(255, 255, 255, 0.35);
}
</style>
