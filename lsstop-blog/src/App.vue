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
  </v-app>
</template>

<script setup lang="ts">
import TopNavBar from '@/components/layout/TopNavBar.vue'
import SideNavBar from '@/components/layout/SideNavBar.vue'
import BlogFooter from './components/layout/BlogFooter.vue'
import BackTop from './components/BackTop.vue'

const isMobile = () => {
  return /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i.test(
    navigator.userAgent,
  )
}

console.log(isMobile())
</script>
