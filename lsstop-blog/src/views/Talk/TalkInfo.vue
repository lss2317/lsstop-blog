<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">说说详情</h1>
    </div>
    <!-- 说说内容 -->
    <v-card class="blog-container">
      <!-- 骨架屏 -->
      <div v-if="loading" class="talk-item skeleton-item">
        <div class="user-info-wrapper">
          <v-skeleton-loader type="avatar" class="skeleton-avatar" />
          <div class="user-detail-wrapper">
            <v-skeleton-loader type="text" width="120" class="skeleton-name" />
            <v-skeleton-loader type="text" width="80" class="skeleton-time" />
            <v-skeleton-loader type="paragraph" class="skeleton-content" />
            <div class="skeleton-actions">
              <v-skeleton-loader type="button" width="60" />
              <v-skeleton-loader type="button" width="60" />
            </div>
          </div>
        </div>
      </div>
      <!-- 空状态 -->
      <div v-else-if="!talk" class="empty-state">
        <v-icon size="48" color="grey">mdi-message-text-outline</v-icon>
        <p>说说不存在</p>
      </div>
      <!-- 说说详情 -->
      <template v-else>
        <div class="talk-item">
          <!-- 用户信息 -->
          <div class="user-info-wrapper">
            <v-avatar
              size="36"
              class="user-avatar"
              :class="{ deactivated: isUserDeactivated(talk) }"
              @click="previewAvatar"
            >
              <v-img :src="getUserAvatar(talk)" width="36" height="36" cover />
            </v-avatar>
            <div class="user-detail-wrapper">
              <div class="user-nickname" :class="{ deactivated: isUserDeactivated(talk) }">
                <span
                  :class="['nickname-text', { clickable: !isUserDeactivated(talk) }]"
                  @click="!isUserDeactivated(talk) && navigateTo(`/user/${talk.userId}`)"
                >
                  {{ getUserNickname(talk) }}
                </span>
                <v-icon v-if="!isUserDeactivated(talk)" class="user-sign" size="20" color="#ffa51e">
                  mdi-check-decagram
                </v-icon>
              </div>
              <!-- 发表时间 -->
              <div class="time">
                {{ dateFormat.datetime(talk.createTime) }}
              </div>
              <!-- 说说信息 -->
              <div class="talk-content" v-html="talk.content" />
              <!-- 图片列表 -->
              <div class="talk-images" v-if="talk.imgList">
                <div
                  class="image-wrapper"
                  v-for="(img, index) of talk.imgList"
                  :key="index"
                  @click.stop="previewImg(img)"
                >
                  <img :src="img" class="images-items" />
                </div>
              </div>
              <!-- 说说操作 -->
              <div
                class="talk-operation"
                :class="{ 'no-images': !talk.imgList || talk.imgList.length === 0 }"
              >
                <div
                  :class="['talk-operation-item', 'like-item', isLiked ? 'liked' : '']"
                  @click="like"
                >
                  <v-icon size="16" class="like-btn">
                    {{ isLiked ? 'mdi-thumb-up' : 'mdi-thumb-up-outline' }}
                  </v-icon>
                  <span class="operation-count">
                    {{ talk.likeCount == null ? 0 : talk.likeCount }}
                  </span>
                </div>
                <div class="talk-operation-item share-btn" @click="share">
                  <v-icon size="16">mdi-share-variant-outline</v-icon>
                  <span class="operation-count">分享</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 评论分隔线 -->
        <div class="comment-divider">
          <v-icon size="18" color="#8a919f">mdi-chat-processing-outline</v-icon>
          <span>评论区</span>
        </div>
        <!-- 评论 -->
        <div class="comment-wrapper">
          <Comment :type="CommentTypeEnum.TALK" :typeId="String(talkId)" />
        </div>
      </template>
    </v-card>
    <!-- 分享弹窗 -->
    <ShareDialog ref="shareDialogRef" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import usePageInfoStore from '@/stores/modules/pageInfo.ts';
import useLikeStore from '@/stores/modules/like.ts';
import { getTalk } from '@/apis/talk';
import { dateFormat } from '@/utils/date';
import { type TalkItem, isUserDeactivated, getUserAvatar, getUserNickname } from '@/utils/talk';
import { parseEmoji } from '@/utils/emoji';
import ShareDialog from '@/components/Share/ShareDialog.vue';
import { LikeTypeEnum } from '@/constants/likeType';
import Comment from '@/components/Comment/BlogComment.vue';
import { CommentTypeEnum } from '@/constants/commentType';
import { previewImages } from '@/utils/photoPreview';
import { useNavigate } from '@/composables/useNavigate';

// 获取路由参数
const route = useRoute();
const talkId = Number(route.params.talkId);
const { navigateTo } = useNavigate();

// 获取封面样式
const pageInfoStore = usePageInfoStore();
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore);

// 点赞状态管理
const likeStore = useLikeStore();

// 加载状态
const loading = ref(true);

// 说说详情数据
const talk = ref<TalkItem | null>(null);
const isLiked = computed(() => {
  if (!talk.value) return false;
  return likeStore.isLiked(LikeTypeEnum.TALK, talk.value.id);
});

// 分享弹窗引用
const shareDialogRef = ref<InstanceType<typeof ShareDialog> | null>(null);

// 点赞操作
async function like() {
  if (!talk.value) return;
  const liked = await likeStore.toggleLike(LikeTypeEnum.TALK, talk.value.id);
  // 请求失败时不更新点赞数
  if (liked === null) return;
  if (liked) {
    talk.value.likeCount = (talk.value.likeCount || 0) + 1;
  } else {
    talk.value.likeCount = (talk.value.likeCount || 1) - 1;
  }
}

// 预览图片
function previewImg(img: string) {
  if (!talk.value) return;
  const images = talk.value.imgList || [];
  const index = images.indexOf(img);
  previewImages(images, index >= 0 ? index : 0);
}

// 预览头像
function previewAvatar() {
  if (!talk.value) return;
  previewImages([getUserAvatar(talk.value)], 0);
}

// 分享功能
function share() {
  if (!talk.value) return;
  const url = `${window.location.origin}/talk/${talk.value.id}`;
  const title = talk.value.content.replace(/<[^>]+>/g, '').substring(0, 50);
  shareDialogRef.value?.openShareDialog(url, title);
}

// 加载说说详情
function loadTalkInfo() {
  if (!talkId) {
    loading.value = false;
    return;
  }
  getTalk(talkId)
    .then((res) => {
      talk.value = {
        ...res.data,
        content: parseEmoji(res.data.content),
      };
    })
    .finally(() => {
      loading.value = false;
    });
}

onMounted(() => {
  loadTalkInfo();
});
</script>

<style scoped>
/* 精简列宽样式 */
.talk-images :deep([class*='v-col']) {
  padding: 2px !important;
}

.talk-item {
  padding: 12px 20px 16px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 3px 8px 6px rgb(7 17 27 / 6%);
  transition:
    box-shadow 0.3s ease,
    transform 0.3s ease;
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
  transition:
    transform 0.5s,
    box-shadow 0.5s;
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

.nickname-text.clickable {
  cursor: pointer;
}

.nickname-text.clickable:hover {
  color: #007aff;
}

.user-sign {
  margin-left: 4px;
}

.time {
  color: #999;
  margin-top: 2px;
  font-size: 12px;
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
  transition: transform 0.3s ease;
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
  grid-template-columns: 100px auto;
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
  transition:
    background 0.2s ease,
    transform 0.2s ease;
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
.skeleton-item {
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

/* 评论区包装器 */
.comment-wrapper {
  padding-top: 10px;
}
</style>
