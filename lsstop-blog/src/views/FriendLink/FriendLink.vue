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
      <v-row v-if="friendLinkList.length > 0" class="link-container">
        <v-col
          class="link-wrapper"
          md="4"
          cols="12"
          v-for="item of friendLinkList"
          :key="item.linkAddress"
        >
          <a :href="item.linkAddress" target="_blank">
            <v-avatar size="65" class="link-avatar">
              <img :src="item.linkAvatar" :alt="item.linkName" />
            </v-avatar>
            <div class="link-text-content">
              <div class="link-name">{{ item.linkName }}</div>
              <div class="link-intro">{{ item.linkIntro }}</div>
            </div>
          </a>
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
        <div>名称：{{ websiteConfig.websiteName }}</div>
        <div>简介：{{ websiteConfig.websiteIntro }}</div>
        <div>头像：{{ websiteConfig.websiteAvatar }}</div>
      </blockquote>
      <div class="mt-5 mb-5">需要交换友链的可在下方留言💖</div>
      <blockquote class="mb-10">
        友链信息展示需要，你的信息格式要包含：名称、介绍、链接、头像
      </blockquote>
      <!-- 评论 -->
      <Comment></Comment>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import Comment from '@/components/Comment/BlogComment.vue'
import { onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { listFriendLink, type FriendLinkVo } from '@/apis/friendLink'
import usePageInfoStore from '@/stores/modules/pageInfo.ts'
import useWebsiteConfigStore from '@/stores/modules/websiteConfig.ts'
import { useSnackbarStore } from '@/stores/modules/snackbar.ts'

const pageInfoStore = usePageInfoStore()
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore)

const websiteConfigStore = useWebsiteConfigStore()
const { config: websiteConfig } = storeToRefs(websiteConfigStore)

const snackbarStore = useSnackbarStore()

const friendLinkList = ref<FriendLinkVo[]>([])

onMounted(() => {
  listFriendLink()
    .then((res) => {
      friendLinkList.value = res.data
    })
    .catch(() => {
      snackbarStore.error('获取友链列表失败')
    })
})
</script>

<style scoped>
blockquote {
  line-height: 2;
  margin: 0;
  font-size: 15px;
  border-left: 0.2rem solid #49b1f5;
  padding: 10px 1rem !important;
  background-color: #ecf7fe;
  border-radius: 4px;
}

.link-banner {
  background: #49b1f5;
}

.link-title {
  color: #344c67;
  font-size: 21px;
  font-weight: bold;
  line-height: 2;
}

.link-container {
  margin: 10px 10px 0;
}

.link-wrapper {
  position: relative;
  transition: all 0.3s;
  border-radius: 8px;
}

.link-avatar {
  margin-top: 5px;
  margin-left: 10px;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  flex-shrink: 0;
  opacity: 1;
  width: 65px;
  overflow: hidden;
  transform: scale(1);
}

.link-avatar :deep(img) {
  cursor: pointer;
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
  z-index: 1000;
  transition: all 0.4s ease;
}

.link-wrapper:hover .link-name {
  font-size: 1.4rem;
}

.link-intro {
  text-align: center;
  padding: 8px 10px;
  font-size: 13px;
  color: #1f2d3d;
  width: 100%;
  transition: all 0.4s ease;
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
  transition: all 0.4s ease;
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
  color: #333;
  text-decoration: none;
  display: flex;
  height: 80px;
  width: 100%;
  cursor: pointer;
  align-items: center;
  overflow: hidden;
}

.link-wrapper:hover {
  box-shadow: 0 2px 20px #49b1f5;
}

.link-wrapper:hover:before {
  transform: scale(1);
}

.link-wrapper:before {
  position: absolute;
  border-radius: 8px;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background: #49b1f5 !important;
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
  color: #999;
}

.empty-state p {
  margin-top: 12px;
  font-size: 14px;
}
</style>
