<template>
  <v-app id="app">
    <!-- 导航栏 -->
    <TopNavBar></TopNavBar>
    <!-- 侧边导航栏 -->
    <SideNavBar></SideNavBar>
    <!-- 内容 -->
    <v-main>
      <router-view :key="$route.fullPath"></router-view>
    </v-main>
    <!-- 页脚 -->
    <BlogFooter style="z-index: 1"></BlogFooter>
    <!-- 返回顶部 -->
    <BackTop></BackTop>
    <!-- 全局消息提示 -->
    <SnackbarMessage></SnackbarMessage>
    <!-- 登录相关弹窗 -->
    <AuthDialog></AuthDialog>
  </v-app>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import TopNavBar from '@/components/layout/TopNavBar.vue'
import SideNavBar from '@/components/layout/SideNavBar.vue'
import BlogFooter from './components/layout/BlogFooter.vue'
import BackTop from './components/BackTop/BackTop.vue'
import SnackbarMessage from './components/Snackbar/SnackbarMessage.vue'
import AuthDialog from './components/Login/AuthDialog.vue'
import useWebsiteConfigStore from '@/stores/modules/websiteConfig'
import usePageInfoStore from '@/stores/modules/pageInfo'
import useLikeStore from '@/stores/modules/like'
import useUserInfoStore from '@/stores/modules/userInfo'
import { tokenManager } from '@/utils/token'
import { useScrollRestore } from '@/composables/useScrollRestore'

const websiteConfigStore = useWebsiteConfigStore()
const pageInfoStore = usePageInfoStore()
const likeStore = useLikeStore()
const userInfoStore = useUserInfoStore()

// 页面刷新时保持滚动位置
useScrollRestore()

onMounted(async () => {
  void websiteConfigStore.fetchWebsiteConfig()
  void pageInfoStore.fetchPageList()
  // 有token时先获取用户信息，再获取点赞数据
  if (tokenManager.hasToken()) {
    await userInfoStore.fetchUserInfo()
    void likeStore.fetchUserLike()
  }
})

const isMobile = (): boolean => {
  return /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i.test(
    navigator.userAgent,
  )
}

console.log(isMobile())
</script>
