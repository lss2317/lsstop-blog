<template>
  <v-app id="app">
    <Transition name="initial-loading">
      <div v-if="initialLoading" class="initial-loading-screen">
        <LoadingSpinner />
      </div>
    </Transition>
    <!-- 导航栏 -->
    <TopNavBar v-if="!isHideLayout"></TopNavBar>
    <!-- 侧边导航栏 -->
    <SideNavBar v-if="!isHideLayout"></SideNavBar>
    <!-- 内容 -->
    <v-main>
      <router-view :key="$route.fullPath"></router-view>
    </v-main>
    <!-- 页脚 -->
    <BlogFooter v-if="!isHideLayout && !hideFooter" style="z-index: 1"></BlogFooter>
    <!-- 返回顶部 -->
    <BackTop v-if="!isHideLayout"></BackTop>
    <!-- 全局消息提示 -->
    <SnackbarMessage></SnackbarMessage>
    <!-- 登录相关弹窗 -->
    <AuthDialog></AuthDialog>
    <!-- 弹窗公告 -->
    <AnnouncementPopup v-if="!isHideLayout" />
    <!-- 聊天室 -->
    <ChatDrawer v-if="!isHideLayout"></ChatDrawer>
  </v-app>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import TopNavBar from '@/components/layout/TopNavBar.vue';
import SideNavBar from '@/components/layout/SideNavBar.vue';
import BlogFooter from './components/layout/BlogFooter.vue';
import BackTop from './components/BackTop/BackTop.vue';
import SnackbarMessage from './components/Snackbar/SnackbarMessage.vue';
import AuthDialog from '@/components/Auth/AuthDialog.vue';
import AnnouncementPopup from './components/Announcement/AnnouncementPopup.vue';
import ChatDrawer from '@/components/Chat/ChatDrawer.vue';
import useWebsiteConfigStore from '@/stores/modules/websiteConfig';
import usePageInfoStore from '@/stores/modules/pageInfo';
import useLikeStore from '@/stores/modules/like';
import useUserInfoStore from '@/stores/modules/userInfo';
import useAnnouncementStore from '@/stores/modules/announcement';
import { tokenManager } from '@/utils/token';
import { useScrollRestore } from '@/composables/useScrollRestore';
import LoadingSpinner from '@/components/Loading/LoadingSpinner.vue';

const route = useRoute();
const isHideLayout = computed(() => !!route.meta.hideLayout);
const hideFooter = computed(() => !!route.meta.hideFooter);
const initialLoading = ref(true);

const websiteConfigStore = useWebsiteConfigStore();
const pageInfoStore = usePageInfoStore();
const likeStore = useLikeStore();
const userInfoStore = useUserInfoStore();
const announcementStore = useAnnouncementStore();

// 页面刷新时保持滚动位置
useScrollRestore();

const INITIAL_LOADING_MIN_DURATION = 350;
const INITIAL_LOADING_MAX_DURATION = 8000;
let initialLoadingFallbackTimer: number | null = null;

onMounted(async () => {
  const startTime = performance.now();
  initialLoadingFallbackTimer = window.setTimeout(() => {
    initialLoading.value = false;
    initialLoadingFallbackTimer = null;
  }, INITIAL_LOADING_MAX_DURATION);

  const coreTasks: Promise<unknown>[] = [
    websiteConfigStore.fetchWebsiteConfig(),
    pageInfoStore.fetchPageList(),
    announcementStore.fetchAnnouncementList(),
  ];

  void websiteConfigStore.fetchViewCount();
  // 有token时先获取用户信息，再获取点赞数据
  if (tokenManager.hasToken()) {
    coreTasks.push(
      userInfoStore.fetchUserInfo().then(() => {
        void likeStore.fetchUserLike();
      }),
    );
  }

  await Promise.allSettled(coreTasks);

  const elapsed = performance.now() - startTime;
  const remaining = Math.max(0, INITIAL_LOADING_MIN_DURATION - elapsed);
  if (remaining > 0) {
    await new Promise<void>((resolve) => window.setTimeout(resolve, remaining));
  }

  initialLoading.value = false;
  if (initialLoadingFallbackTimer) {
    clearTimeout(initialLoadingFallbackTimer);
    initialLoadingFallbackTimer = null;
  }
});

onBeforeUnmount(() => {
  if (initialLoadingFallbackTimer) {
    clearTimeout(initialLoadingFallbackTimer);
  }
});
</script>

<style scoped>
.initial-loading-screen {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-white);
}

.initial-loading-screen :deep(.loading-spinner) {
  padding: 0;
}

.initial-loading-leave-active {
  transition: opacity 0.3s ease;
}

.initial-loading-leave-to {
  opacity: 0;
}
</style>
