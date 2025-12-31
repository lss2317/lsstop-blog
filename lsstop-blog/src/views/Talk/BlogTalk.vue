<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">说说</h1>
    </div>
    <!-- 说说内容 -->
    <v-card class="blog-container">
      <!-- 空状态 -->
      <div v-if="talkList.length === 0 && !loading" class="empty-state">
        <v-icon size="48" color="grey">mdi-message-text-outline</v-icon>
        <p>暂无说说</p>
      </div>
      <!-- 说说列表 -->
      <div v-for="item of talkList" :key="item.id" class="talk-item">
        <!-- 用户信息 -->
        <div class="user-info-wrapper">
          <v-avatar size="36" class="user-avatar">
            <v-img :src="item.avatar" width="36" height="36" cover />
          </v-avatar>
          <div class="user-detail-wrapper">
            <div class="user-nickname">
              {{ item.nickname }}
              <v-icon class="user-sign" size="20" color="#ffa51e">
                mdi-check-decagram
              </v-icon>
            </div>
            <!-- 发表时间 -->
            <div class="time">
              {{ dateFormat.datetime(item.createTime) }}
              <span class="top" v-if="item.isTop === 1">
                <v-icon size="14" color="#ff7242">mdi-pin</v-icon> 置顶
              </span>
            </div>
            <!-- 说说信息 -->
            <div class="talk-content" v-html="item.content" />
            <!-- 图片列表 -->
            <div class="talk-images" v-if="item.imgList">
              <div
                class="image-wrapper"
                v-for="(img, index) of item.imgList"
                :key="index"
                @click.stop="previewImg(img)"
              >
                <img :src="img" class="images-items" />
              </div>
            </div>
            <!-- 说说操作 -->
            <div class="talk-operation" :class="{ 'no-images': !item.imgList || item.imgList.length === 0 }">
              <div
                :class="['talk-operation-item', 'like-item', likedTalkSet.has(item.id) ? 'liked' : '']"
                @click="like(item)"
              >
                <v-icon size="16" class="like-btn">
                  {{ likedTalkSet.has(item.id) ? 'mdi-thumb-up' : 'mdi-thumb-up-outline' }}
                </v-icon>
                <span class="operation-count">
                  {{ item.likeCount == null ? 0 : item.likeCount }}
                </span>
              </div>
              <router-link :to="'/talk/' + item.id" class="talk-operation-item comment-btn">
                <v-icon size="16">mdi-chat-outline</v-icon>
                <span class="operation-count">
                  {{ item.commentCount == null ? 0 : item.commentCount }}
                </span>
              </router-link>
              <div class="talk-operation-item share-btn" @click="share(item)">
                <v-icon size="16">mdi-share-variant-outline</v-icon>
                <span class="operation-count">分享</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </v-card>

    <!-- 图片灯箱预览 -->
    <v-dialog v-model="lightboxVisible" max-width="90vw" content-class="lightbox-dialog">
      <div class="lightbox" @click.self="lightboxVisible = false">
        <img :src="lightboxImg" alt="预览图片" @click.stop />
        <v-btn icon class="lightbox-close" size="small" @click="lightboxVisible = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </div>
    </v-dialog>
    <!-- 分享弹窗 -->
    <v-dialog v-model="shareDialogVisible" max-width="320">
      <v-card class="share-dialog">
        <v-card-title class="share-title">分享到</v-card-title>
        <v-card-text class="share-options">
          <div class="share-item" @click="copyLink">
            <v-icon size="32" color="#8a919f">mdi-link-variant</v-icon>
            <span>复制链接</span>
          </div>
          <div class="share-item" @click="shareToWeibo">
            <v-icon size="32" color="#e6162d">mdi-sina-weibo</v-icon>
            <span>微博</span>
          </div>
          <div class="share-item" @click="shareToQzone">
            <v-icon size="32" color="#efbe1b">mdi-qqchat</v-icon>
            <span>QQ空间</span>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import usePageInfoStore from '@/stores/modules/pageInfo.ts'
import { listTalk } from '@/apis/talk'
import { dateFormat } from '@/utils/date'

// 说说数据接口
interface TalkItem {
  id: number
  avatar: string
  nickname: string
  createTime: string
  isTop: number
  content: string
  imgList: string[] | null
  likeCount: number | null
  commentCount: number | null
}

// 获取封面样式
const pageInfoStore = usePageInfoStore()
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore)

// 加载状态
const loading = ref(true)

// 说说列表数据
const talkList = ref<TalkItem[]>([])

// 用户点赞的说说ID集合
const likedTalkSet = ref<Set<number>>(new Set())

// 图片灯箱
const lightboxVisible = ref(false)
const lightboxImg = ref('')

// 分享弹窗
const shareDialogVisible = ref(false)
const shareUrl = ref('')
const shareTitle = ref('')

// 点赞操作
function like(item: TalkItem) {
  if (likedTalkSet.value.has(item.id)) {
    likedTalkSet.value.delete(item.id)
    item.likeCount = (item.likeCount || 1) - 1
  } else {
    likedTalkSet.value.add(item.id)
    item.likeCount = (item.likeCount || 0) + 1
  }
}

// 预览图片（灯箱）
function previewImg(img: string) {
  lightboxImg.value = img
  lightboxVisible.value = true
}

// 分享功能
function share(item: TalkItem) {
  shareUrl.value = `${window.location.origin}/talk/${item.id}`
  shareTitle.value = item.content.replace(/<[^>]+>/g, '').substring(0, 50)
  shareDialogVisible.value = true
}

// 复制链接
function copyLink() {
  navigator.clipboard.writeText(shareUrl.value).then(() => {
    alert('链接已复制到剪贴板')
    shareDialogVisible.value = false
  })
}

// 分享到微博
function shareToWeibo() {
  const url = `https://service.weibo.com/share/share.php?url=${encodeURIComponent(shareUrl.value)}&title=${encodeURIComponent(shareTitle.value)}`
  window.open(url, '_blank')
  shareDialogVisible.value = false
}

// 分享到QQ空间
function shareToQzone() {
  const url = `https://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?url=${encodeURIComponent(shareUrl.value)}&title=${encodeURIComponent(shareTitle.value)}`
  window.open(url, '_blank')
  shareDialogVisible.value = false
}

// 加载说说列表
function listTalks() {
  loading.value = true
  listTalk()
    .then(res => {
      talkList.value = res.data
    })
    .finally(() => {
      loading.value = false
    })
}

onMounted(() => {
  listTalks()
})
</script>

<style scoped>
/* 精简列宽样式 */
.talk-images :deep([class*='v-col']) {
  padding: 2px !important;
}

.talk-item:not(:first-child) {
  margin-bottom: 20px;
}

.talk-item {
  padding: 12px 20px 16px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 3px 8px 6px rgb(7 17 27 / 6%);
  transition: all 0.3s ease 0s;
}

.talk-item:hover {
  box-shadow: 0 5px 10px 8px rgb(7 17 27 / 16%);
  transform: translateY(-3px);
}

.user-info-wrapper {
  width: 100%;
  display: flex;
}

.user-avatar {
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.5s;
}

.user-avatar:hover {
  transform: rotate(360deg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

.user-detail-wrapper {
  flex: 1;
  margin-left: 10px;
  width: 0;
}

.user-nickname {
  font-size: 15px;
  font-weight: bold;
  vertical-align: middle;
}

.user-sign {
  margin-left: 4px;
}

.time {
  color: #999;
  margin-top: 2px;
  font-size: 12px;
}

.top {
  color: #ff7242;
  margin-left: 10px;
}

.talk-content {
  margin-top: 8px;
  font-size: 15px;
  line-height: 1.8;
  white-space: pre-line;
  word-wrap: break-word;
  word-break: break-all;
}

.talk-content :deep(img) {
  display: inline-block;
  height: 1.25em;
  width: auto;
  vertical-align: -0.15em;
  margin: 0 1px;
}

.talk-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
  margin-bottom: 10px;
}

.image-wrapper {
  cursor: pointer;
  transition: all 0.3s ease;
}

.image-wrapper:hover {
  transform: scale(1.02);
}

.image-wrapper:hover .images-items {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.images-items {
  display: block;
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  object-fit: cover;
  background-color: #f5f5f5;
  cursor: pointer;
}

.talk-operation {
  margin-top: 10px;
  display: flex;
  align-items: center;
}

.talk-operation.no-images {
  margin-top: 16px;
}

.talk-operation-item {
  display: flex;
  align-items: center;
  margin-right: 24px;
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 20px;
  transition: all 0.2s ease;
  cursor: pointer;
  color: #8a919f;
}

.talk-operation-item :deep(.v-icon) {
  color: #8a919f;
}

.talk-operation-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.like-item:hover {
  color: #eb5055;
}

.like-item:hover :deep(.v-icon) {
  color: #eb5055 !important;
}

.like-item:hover .like-btn {
  transform: scale(1.2);
}

.operation-count {
  margin-left: 6px;
  font-weight: 500;
}

.like-btn {
  transition: transform 0.2s ease;
}

.talk-operation-item.liked {
  color: #eb5055;
}

.talk-operation-item.liked :deep(.v-icon) {
  color: #eb5055 !important;
}

.comment-btn,
.comment-btn .operation-count {
  text-decoration: none;
  color: #8a919f !important;
}

.comment-btn:hover {
  color: #1e80ff;
}

.comment-btn:hover :deep(.v-icon) {
  color: #1e80ff !important;
  transform: scale(1.2);
}

.share-btn:hover {
  color: #07c160;
}

.share-btn:hover :deep(.v-icon) {
  color: #07c160 !important;
  transform: scale(1.2);
}

/* 灯箱样式 */
:global(.lightbox-dialog) {
  background: transparent !important;
  box-shadow: none !important;
  overflow: visible !important;
}

.lightbox {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  min-height: 200px;
}

.lightbox img {
  max-width: 90vw;
  max-height: 85vh;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
}

.lightbox-close {
  position: absolute;
  top: -40px;
  right: 0;
  color: #fff !important;
  background: rgba(0, 0, 0, 0.5) !important;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #999;
}

.empty-state p {
  margin-top: 12px;
  font-size: 14px;
}

/* 分享弹窗样式 */
.share-dialog {
  border-radius: 12px !important;
}

.share-title {
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  padding-bottom: 8px;
}

.share-options {
  display: flex;
  justify-content: space-around;
  padding: 16px 8px 24px;
}

.share-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 12px;
  border-radius: 8px;
  transition: background 0.2s;
}

.share-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.share-item span {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}
</style>
