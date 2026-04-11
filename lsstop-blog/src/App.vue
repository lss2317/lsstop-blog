<template>
  <v-app id="app">
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
import { onMounted, computed } from 'vue';
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

const route = useRoute();
const isHideLayout = computed(() => !!route.meta.hideLayout);
const hideFooter = computed(() => !!route.meta.hideFooter);

const websiteConfigStore = useWebsiteConfigStore();
const pageInfoStore = usePageInfoStore();
const likeStore = useLikeStore();
const userInfoStore = useUserInfoStore();
const announcementStore = useAnnouncementStore();

// 页面刷新时保持滚动位置
useScrollRestore();

onMounted(async () => {
  void websiteConfigStore.fetchWebsiteConfig();
  void websiteConfigStore.fetchViewCount();
  void pageInfoStore.fetchPageList();
  void announcementStore.fetchAnnouncementList();
  // 有token时先获取用户信息，再获取点赞数据
  if (tokenManager.hasToken()) {
    await userInfoStore.fetchUserInfo();
    void likeStore.fetchUserLike();
  }
});
</script>
