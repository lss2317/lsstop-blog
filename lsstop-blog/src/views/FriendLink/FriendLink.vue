<template>
  <div>
    <!-- banner -->
    <div class="link-banner banner" :style="cover">
      <h1 class="banner-title">友情链接</h1>
    </div>
    <!-- 链接列表 -->
    <v-card class="blog-container">
      <div class="link-title mb-1">
        <v-icon color="blue">mdi-link-variant</v-icon>
        大佬链接
      </div>
      <!-- 加载骨架屏 -->
      <v-row v-if="loading" class="link-container">
        <v-col md="4" cols="12" v-for="n in 6" :key="n">
          <div class="link-wrapper skeleton-wrapper">
            <v-skeleton-loader type="avatar" class="skeleton-avatar" />
            <div class="skeleton-text">
              <v-skeleton-loader type="text" width="60%" />
              <v-skeleton-loader type="text" width="80%" />
            </div>
          </div>
        </v-col>
      </v-row>
      <!-- 友链列表 -->
      <v-row v-else-if="friendLinkList.length > 0" class="link-container">
        <v-col
          md="4"
          cols="12"
          v-for="(item, index) of friendLinkList"
          :key="item.id"
          class="link-col"
          :style="{ '--delay': index * 0.08 + 's' }"
        >
          <div class="link-wrapper">
            <a :href="item.linkAddress" target="_blank">
              <v-avatar size="65" class="link-avatar">
                <img :src="item.linkAvatar" :alt="item.linkName" @error="handleImageError" />
              </v-avatar>
              <div class="link-text-content">
                <div class="link-name">{{ item.linkName }}</div>
                <div class="link-intro">{{ item.linkIntro }}</div>
              </div>
            </a>
          </div>
        </v-col>
      </v-row>
      <div v-else class="empty-state">
        <v-icon size="48" color="grey">mdi-link-off</v-icon>
        <p>暂无友链数据</p>
      </div>
      <!-- 说明 -->
      <div class="link-title mt-4 mb-4">
        <v-icon color="blue">mdi-dots-horizontal-circle</v-icon>
        添加友链
      </div>
      <blockquote>
        <div>名称：{{ websiteConfig.siteName }}</div>
        <div>简介：{{ websiteConfig.siteIntro }}</div>
        <div>头像：{{ websiteConfig.siteAvatar }}</div>
      </blockquote>
      <div class="mt-5 mb-5">需要交换友链的可在下方留言💖</div>
      <blockquote class="mb-10">
        友链信息展示需要，你的信息格式要包含：名称、介绍、链接、头像
      </blockquote>
      <!-- 评论分隔线 -->
      <div class="comment-divider">
        <v-icon size="18" color="#8a919f">mdi-chat-processing-outline</v-icon>
        <span>评论区</span>
      </div>
      <!-- 评论 -->
      <Comment></Comment>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import Comment from '@/components/Comment/BlogComment.vue'
import { onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { listFriendLink, type FriendLink } from '@/apis/friendLink'
import usePageInfoStore from '@/stores/modules/pageInfo.ts'
import useWebsiteConfigStore from '@/stores/modules/websiteConfig.ts'
import { useSnackbarStore } from '@/stores/modules/snackbar.ts'

const pageInfoStore = usePageInfoStore()
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore)

const websiteConfigStore = useWebsiteConfigStore()
const { config: websiteConfig } = storeToRefs(websiteConfigStore)

const snackbarStore = useSnackbarStore()

const friendLinkList = ref<FriendLink[]>([])
const loading = ref(true)

// 图片加载失败处理
const handleImageError = (e: Event) => {
  const target = e.target as HTMLImageElement
  target.src = websiteConfig.value.defaultUserAvatar
}

onMounted(() => {
  listFriendLink()
    .then((res) => {
      friendLinkList.value = res.data
    })
    .catch(() => {
      snackbarStore.error('获取友链列表失败')
    })
    .finally(() => {
      loading.value = false
    })
})
</script>

<style scoped>
/* 骨架屏样式 */
.skeleton-wrapper {
  display: flex;
  align-items: center;
  padding: 12px;
}

.skeleton-avatar {
  flex-shrink: 0;
  margin-right: 12px;
}

.skeleton-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

blockquote {
  line-height: 2;
  margin: 0;
  font-size: 15px;
  border-left: 0.2rem solid var(--color-primary);
  padding: 10px 1rem !important;
  background-color: #ecf7fe;
  border-radius: var(--radius-sm);
}

.link-banner {
  background: var(--color-primary);
}

.link-title {
  color: var(--color-text-title);
  font-size: 21px;
  font-weight: bold;
  line-height: 2;
}

.link-container {
  margin: 10px 0 0;
}

.link-col {
  --delay: 0s;
  animation: fadeInUp 0.5s ease forwards;
  animation-delay: var(--delay);
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.link-wrapper {
  position: relative;
  transition: box-shadow var(--transition-normal), transform var(--transition-normal);
  border-radius: var(--radius-lg);
  background: rgba(255, 255, 255, 0.9);
  box-shadow: var(--shadow-card);
  padding: 8px;
  margin-bottom: 16px;
}

.link-wrapper:hover {
  box-shadow: var(--shadow-card-hover);
  transform: translateY(-3px);
}

.link-avatar {
  margin-top: 5px;
  margin-left: 10px;
  transition: opacity 0.5s cubic-bezier(0.4, 0, 0.2, 1), width 0.5s cubic-bezier(0.4, 0, 0.2, 1), transform 0.5s cubic-bezier(0.4, 0, 0.2, 1), margin 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  flex-shrink: 0;
  opacity: 1;
  width: 65px;
  overflow: hidden;
  transform: scale(1);
}

.link-avatar :deep(img) {
  cursor: pointer;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 759px) {
  .link-avatar {
    margin-left: 30px;
  }
}

.link-name {
  text-align: center;
  font-size: 1.25rem;
  font-weight: bold;
  z-index: 1;
  transition: font-size var(--transition-slow);
}

.link-wrapper:hover .link-name {
  font-size: 1.4rem;
}

.link-intro {
  text-align: center;
  padding: 8px 10px;
  font-size: 13px;
  color: var(--color-text-primary);
  width: 100%;
  transition: height var(--transition-slow), max-height var(--transition-slow);
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.5;
  height: 28px;
}

.link-text-content {
  width: 100%;
  z-index: 10;
  transition: transform var(--transition-slow);
  transform: translateX(0);
}

.link-wrapper:hover .link-text-content {
  transform: translateX(-30px) scale(1.05);
}

.link-wrapper:hover .link-intro {
  -webkit-line-clamp: 2;
  height: auto;
  max-height: 56px;
}

.link-wrapper:hover a {
  color: #fff;
}

.link-wrapper:hover .link-avatar {
  opacity: 0;
  width: 0;
  margin-left: 0;
  transform: scale(0);
}

.link-wrapper a {
  color: var(--color-text-primary);
  text-decoration: none;
  display: flex;
  height: 80px;
  width: 100%;
  cursor: pointer;
  align-items: center;
  overflow: hidden;
}

.link-wrapper:hover:before {
  transform: scale(1);
}

.link-wrapper:before {
  position: absolute;
  border-radius: var(--radius-md);
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background: var(--color-primary) !important;
  content: '';
  transition-timing-function: ease-out;
  transition-duration: 0.3s;
  transition-property: transform;
  transform: scale(0);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: var(--color-text-tertiary);
}

.empty-state p {
  margin-top: 12px;
  font-size: 14px;
}

/* 评论区分隔线 */
.comment-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin: 30px 0 10px;
  padding: 16px 0;
  position: relative;
  color: #8a919f;
  font-size: 14px;
  font-weight: 500;
}

.comment-divider::before,
.comment-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(to right, transparent, #e4e6eb, transparent);
}

.comment-divider::before {
  background: linear-gradient(to left, #e4e6eb, transparent);
}

.comment-divider::after {
  background: linear-gradient(to right, #e4e6eb, transparent);
}
</style>

<style>
/* 夜间模式样式 */
.v-theme--dark .blog-container blockquote {
  background-color: rgba(73, 177, 245, 0.15);
  border-left-color: var(--color-primary);
  color: var(--color-text-secondary);
}

.v-theme--dark .blog-container .link-title {
  color: var(--color-text-title);
}

.v-theme--dark .blog-container .link-intro {
  color: var(--color-text-secondary);
}

.v-theme--dark .blog-container .link-wrapper a {
  color: var(--color-text-primary);
}

.v-theme--dark .blog-container .link-wrapper:hover a {
  color: #fff;
}

.v-theme--dark .blog-container .link-wrapper {
  background: var(--color-bg-light);
  box-shadow: var(--shadow-card);
}

.v-theme--dark .blog-container .link-wrapper:hover {
  box-shadow: var(--shadow-card-hover);
}
</style>
