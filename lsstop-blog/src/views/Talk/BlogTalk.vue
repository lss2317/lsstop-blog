<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">说说</h1>
    </div>
    <!-- 说说内容 -->
    <v-card class="blog-container">
      <!-- 骨架屏 -->
      <div v-if="loading" class="skeleton-list">
        <div v-for="i in 3" :key="i" class="talk-item skeleton-item">
          <div class="user-info-wrapper">
            <v-skeleton-loader type="avatar" class="skeleton-avatar" />
            <div class="user-detail-wrapper">
              <v-skeleton-loader type="text" width="120" class="skeleton-name" />
              <v-skeleton-loader type="text" width="80" class="skeleton-time" />
              <v-skeleton-loader type="paragraph" class="skeleton-content" />
              <div class="skeleton-actions">
                <v-skeleton-loader type="button" width="60" />
                <v-skeleton-loader type="button" width="60" />
                <v-skeleton-loader type="button" width="60" />
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 空状态 -->
      <div v-else-if="talkList.length === 0" class="empty-state">
        <v-icon size="48" color="grey">mdi-message-text-outline</v-icon>
        <p>暂无说说</p>
      </div>
      <!-- 说说列表 -->
      <template v-else>
        <div
          v-for="(item, index) of talkList"
          :key="item.id"
          class="talk-item"
          :style="{ '--delay': index * 0.08 + 's' }"
        >
          <!-- 用户信息 -->
          <div class="user-info-wrapper">
            <v-avatar
              size="36"
              class="user-avatar"
              :class="{ deactivated: isUserDeactivated(item) }"
              @click="previewAvatar(item)"
            >
              <v-img :src="getUserAvatar(item)" width="36" height="36" cover />
            </v-avatar>
            <div class="user-detail-wrapper">
              <div class="user-nickname" :class="{ deactivated: isUserDeactivated(item) }">
                {{ getUserNickname(item) }}
                <v-icon v-if="!isUserDeactivated(item)" class="user-sign" size="20" color="#ffa51e">
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
                  @click.stop="previewImg(item, img)"
                >
                  <img :src="img" class="images-items" />
                </div>
              </div>
              <!-- 说说操作 -->
              <div
                class="talk-operation"
                :class="{ 'no-images': !item.imgList || item.imgList.length === 0 }"
              >
                <div
                  :class="[
                    'talk-operation-item',
                    'like-item',
                    likedTalkIds.has(item.id) ? 'liked' : '',
                  ]"
                  @click="like(item)"
                >
                  <v-icon size="16" class="like-btn">
                    {{ likedTalkIds.has(item.id) ? 'mdi-thumb-up' : 'mdi-thumb-up-outline' }}
                  </v-icon>
                  <span class="operation-count">
                    {{ item.likeCount == null ? 0 : item.likeCount }}
                  </span>
                </div>
                <div class="talk-operation-item comment-btn" @click="goToTalkInfo(item.id)">
                  <v-icon size="16">mdi-chat-outline</v-icon>
                  <span class="operation-count">
                    {{ item.commentCount == null ? 0 : item.commentCount }}
                  </span>
                </div>
                <div class="talk-operation-item share-btn" @click="share(item)">
                  <v-icon size="16">mdi-share-variant-outline</v-icon>
                  <span class="operation-count">分享</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </v-card>
    <!-- 分享弹窗 -->
    <v-dialog v-model="shareDialogVisible" max-width="320" transition="dialog-bottom-transition">
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
          <div class="share-item" @click="shareToWeixin">
            <v-icon size="32" color="#07c160">mdi-wechat</v-icon>
            <span>微信</span>
          </div>
          <div class="share-item" @click="shareToQQ">
            <v-icon size="32" color="#12b7f5">mdi-qqchat</v-icon>
            <span>QQ</span>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>
    <!-- 微信二维码弹窗 -->
    <v-dialog v-model="weixinQrcodeVisible" max-width="300" transition="dialog-bottom-transition">
      <v-card class="qrcode-dialog">
        <v-card-title class="qrcode-title">微信扫一扫：分享</v-card-title>
        <v-card-text class="qrcode-content">
          <div class="qrcode-wrapper">
            <v-progress-circular v-if="!weixinQrcodeUrl" indeterminate color="#07c160" size="40" />
            <img v-else :src="weixinQrcodeUrl" alt="微信分享二维码" class="qrcode-img" />
          </div>
          <p class="qrcode-tip">微信里点“发现”，扫一下<br />二维码即可在手机上打开。</p>
        </v-card-text>
      </v-card>
    </v-dialog>
    <!-- QQ二维码弹窗 -->
    <v-dialog v-model="qqQrcodeVisible" max-width="300" transition="dialog-bottom-transition">
      <v-card class="qrcode-dialog">
        <v-card-title class="qrcode-title">QQ扫一扫：分享</v-card-title>
        <v-card-text class="qrcode-content">
          <div class="qrcode-wrapper">
            <v-progress-circular v-if="!qqQrcodeUrl" indeterminate color="#12b7f5" size="40" />
            <img v-else :src="qqQrcodeUrl" alt="QQ分享二维码" class="qrcode-img" />
          </div>
          <p class="qrcode-tip">打开手机QQ，扫一下<br />二维码即可在手机上打开。</p>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import usePageInfoStore from '@/stores/modules/pageInfo.ts'
import useLikeStore from '@/stores/modules/like.ts'
import { listTalk } from '@/apis/talk'
import { dateFormat } from '@/utils/date'
import {
  type TalkItem,
  isUserDeactivated,
  getUserAvatar,
  getUserNickname,
  useShare,
} from '@/utils/talk'
import { LikeTypeEnum } from '@/constants/likeType'
import { previewImages } from '@/utils/photoPreview'

// 获取路由实例
const router = useRouter()

// 获取封面样式
const pageInfoStore = usePageInfoStore()
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore)

// 点赞状态管理
const likeStore = useLikeStore()
const { likedTalkIds } = storeToRefs(likeStore)

// 加载状态
const loading = ref(true)

// 说说列表数据
const talkList = ref<TalkItem[]>([])

// 跳转到说说详情页
function goToTalkInfo(id: number) {
  router.push('/talk/' + id).then(() => {
    window.scrollTo(0, 0)
  })
}

// 分享功能
const {
  shareDialogVisible,
  weixinQrcodeVisible,
  weixinQrcodeUrl,
  qqQrcodeVisible,
  qqQrcodeUrl,
  openShareDialog,
  copyLink,
  shareToWeibo,
  shareToWeixin,
  shareToQQ,
} = useShare()

// 点赞操作
async function like(item: TalkItem) {
  const isLiked = await likeStore.toggleLike(LikeTypeEnum.TALK, item.id)
  // 请求失败时不更新点赞数
  if (isLiked === null) return
  if (isLiked) {
    item.likeCount = (item.likeCount || 0) + 1
  } else {
    item.likeCount = (item.likeCount || 1) - 1
  }
}

// 预览图片
function previewImg(item: TalkItem, img: string) {
  const images = item.imgList || []
  const index = images.indexOf(img)
  previewImages(images, index >= 0 ? index : 0)
}

// 预览头像
function previewAvatar(item: TalkItem) {
  previewImages([getUserAvatar(item)], 0)
}

// 分享功能
function share(item: TalkItem) {
  const url = `${window.location.origin}/talk/${item.id}`
  const title = item.content.replace(/<[^>]+>/g, '').substring(0, 50)
  openShareDialog(url, title)
}

// 加载说说列表
function listTalks() {
  loading.value = true
  listTalk()
    .then((res) => {
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
  --delay: 0s;
  padding: 12px 20px 16px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 3px 8px 6px rgb(7 17 27 / 6%);
  transition: all 0.3s ease 0s;
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
  cursor: pointer !important;
}

.user-avatar :deep(*) {
  cursor: pointer !important;
}

.user-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

.user-avatar.deactivated {
  filter: grayscale(100%);
  opacity: 0.7;
}

.user-nickname.deactivated {
  color: #999;
  font-style: italic;
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
  display: grid;
  grid-template-columns: 100px 100px auto;
  align-items: center;
}

.talk-operation.no-images {
  margin-top: 16px;
}

.talk-operation-item {
  display: inline-flex;
  align-items: center;
  width: fit-content;
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

/* 骨架屏样式 */
.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.skeleton-item {
  opacity: 1 !important;
  animation: none !important;
}

.skeleton-avatar {
  flex-shrink: 0;
}

.skeleton-name {
  margin-bottom: 4px;
}

.skeleton-time {
  margin-bottom: 12px;
}

.skeleton-content {
  margin-bottom: 16px;
}

.skeleton-actions {
  display: flex;
  gap: 16px;
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

/* 二维码弹窗样式 */
.qrcode-dialog {
  border-radius: 16px !important;
  overflow: hidden;
}

.qrcode-title {
  text-align: center;
  font-size: 15px;
  font-weight: 500;
  color: #333;
  padding: 20px 20px 8px;
}

.qrcode-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 24px 24px;
}

.qrcode-wrapper {
  width: 180px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qrcode-img {
  width: 180px;
  height: 180px;
  border: 1px solid #eee;
  border-radius: 8px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.qrcode-tip {
  margin-top: 16px;
  font-size: 12px;
  color: #999;
  text-align: center;
  line-height: 1.8;
}
</style>
