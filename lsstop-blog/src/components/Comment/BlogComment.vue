<template>
  <div class="lc-comment-container">
    <!-- 评论输入框 -->
    <div class="lc-input-box">
      <textarea class="lc-textarea" v-model="commentContent" placeholder="请输入评论..." />
      <div class="lc-input-toolbar">
        <div class="lc-toolbar-left">
          <div class="lc-emoji-trigger-wrapper">
            <span
              ref="emojiTriggerRef"
              :class="['lc-tool-icon', showEmoji ? 'active' : '']"
              title="表情"
              @click="toggleEmoji"
            >
              <i class="iconfont iconbiaoqing" />
            </span>
            <!-- 表情框 -->
            <div :class="['lc-emoji-panel', emojiDirection]" v-show="showEmoji">
              <CommentEmoji @addEmoji="addEmoji" />
            </div>
          </div>
        </div>
        <button class="lc-submit-btn" :disabled="!commentContent.trim()" @click="insertComment">
          评论
        </button>
      </div>
    </div>

    <!-- 排序 -->
    <div class="lc-sort-bar" v-if="count > 0">
      <span class="lc-sort-label">排序：</span>
      <div class="lc-sort-dropdown-wrapper">
        <span class="lc-sort-dropdown" @click="showSortMenu = !showSortMenu">
          <span class="lc-sort-value">{{ sortType === 'hot' ? '最热' : '最新' }}</span>
          <svg
            :class="['lc-dropdown-icon', showSortMenu ? 'rotate' : '']"
            viewBox="0 0 24 24"
            width="14"
            height="14"
          >
            <path fill="currentColor" d="M7 10l5 5 5-5z" />
          </svg>
        </span>
        <!-- 下拉菜单 -->
        <div class="lc-sort-menu" v-if="showSortMenu">
          <div
            :class="['lc-sort-menu-item', sortType === 'hot' ? 'active' : '']"
            @click="selectSort('hot')"
          >
            <span>最热</span>
            <svg
              v-if="sortType === 'hot'"
              class="lc-check-icon"
              viewBox="0 0 24 24"
              width="16"
              height="16"
            >
              <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
            </svg>
          </div>
          <div
            :class="['lc-sort-menu-item', sortType === 'new' ? 'active' : '']"
            @click="selectSort('new')"
          >
            <span>最新</span>
            <svg
              v-if="sortType === 'new'"
              class="lc-check-icon"
              viewBox="0 0 24 24"
              width="16"
              height="16"
            >
              <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="lc-comment-list" v-if="count > 0">
      <div class="lc-comment-item" v-for="(item, index) of commentList" :key="item.id">
        <!-- 头像 -->
        <div class="lc-avatar">
          <img :src="item.avatar" alt="用户头像" />
        </div>
        <div class="lc-comment-main">
          <!-- 用户信息 -->
          <div class="lc-user-row">
           <span class="lc-nickname">{{ item.nickname }}</span>
            <span class="lc-blogger-tag" v-if="item.userId === postUserId">博主</span>
          </div>
          <!-- 时间地点 -->
          <div class="lc-meta-row">
            <span>来自 {{ item.ipRegion }}</span>
            <span class="lc-meta-divider"></span>
            <span>{{ formatTime(item.createTime) }}</span>
          </div>
          <!-- 评论内容 -->
          <div class="lc-content" v-html="item.content"></div>
          <!-- 操作栏 -->
          <div class="lc-action-row">
            <span class="lc-action-item" @click="like(item)">
              <svg
                :class="['lc-action-icon', isLike(item.id) ? 'liked' : '']"
                viewBox="0 0 24 24"
                width="16"
                height="16"
              >
                <path
                  fill="currentColor"
                  d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z"
                />
              </svg>
              <span v-if="item.likeCount > 0">{{ item.likeCount }}</span>
            </span>
            <span class="lc-action-item" v-if="item.replyCount > 0" @click="toggleReplies(index)">
              <svg class="lc-action-icon" viewBox="0 0 24 24" width="16" height="16">
                <path
                  fill="currentColor"
                  d="M21.99 4c0-1.1-.89-2-1.99-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4-.01-18z"
                />
              </svg>
              <span>{{
                showReplies[index] ? '隐藏回复' : '展示 ' + item.replyCount + ' 条回复'
              }}</span>
            </span>
            <span class="lc-action-item" @click="replyComment(index)">
              <svg class="lc-action-icon" viewBox="0 0 24 24" width="16" height="16">
                <path
                  fill="currentColor"
                  d="M10 9V5l-7 7 7 7v-4.1c5 0 8.5 1.6 11 5.1-1-5-4-10-11-11z"
                />
              </svg>
              <span>回复</span>
            </span>
          </div>

          <!-- 回复输入框（回复主评论） -->
          <div class="lc-reply-input-box" v-if="replyingTo === index && replyingToReplyId === null">
            <div class="lc-reply-avatar">
              <img :src="currentUserAvatar" alt="用户头像" />
            </div>
            <div class="lc-reply-input-wrapper">
              <textarea
                class="lc-reply-input"
                v-model="replyContent"
                placeholder="请输入回复 ..."
                rows="1"
                @input="autoResize"
              />
            </div>
          </div>
          <div class="lc-reply-actions" v-if="replyingTo === index && replyingToReplyId === null">
            <button class="lc-cancel-btn" @click="cancelReply">取消</button>
            <button
              class="lc-reply-submit-btn"
              :disabled="!replyContent.trim()"
              @click="submitReply(item)"
            >
              回复
            </button>
          </div>

          <!-- 回复列表 -->
          <div
            class="lc-reply-list"
            v-if="item.replyList && item.replyList.length && showReplies[index]"
          >
            <template v-for="reply of item.replyList" :key="reply.id">
              <div class="lc-reply-item">
                <div class="lc-avatar small">
                  <img :src="reply.avatar" alt="用户头像" />
                </div>
                <div class="lc-reply-main">
                  <div class="lc-user-row">
                    <span class="lc-nickname">{{ reply.nickname }}</span>
                    <span class="lc-blogger-tag" v-if="reply.userId === postUserId">博主</span>
                  </div>
                  <div class="lc-meta-row">
                    <span>来自 {{ reply.ipRegion }}</span>
                    <span class="lc-meta-divider"></span>
                    <span>{{ formatTime(reply.createTime) }}</span>
                  </div>
                  <div class="lc-content">
                    <template v-if="reply.replyUserId !== item.userId">
                      <span class="lc-reply-to">@{{ reply.replyNickname }}</span>
                    </template>
                    <span v-html="reply.content"></span>
                  </div>
                  <div class="lc-action-row">
                    <span class="lc-action-item" @click="like(reply)">
                      <svg
                        :class="['lc-action-icon', isLike(reply.id) ? 'liked' : '']"
                        viewBox="0 0 24 24"
                        width="16"
                        height="16"
                      >
                        <path
                          fill="currentColor"
                          d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z"
                        />
                      </svg>
                      <span v-if="reply.likeCount > 0">{{ reply.likeCount }}</span>
                    </span>
                    <span class="lc-action-item" @click="replyToReply(index, reply)">
                      <svg class="lc-action-icon" viewBox="0 0 24 24" width="16" height="16">
                        <path
                          fill="currentColor"
                          d="M10 9V5l-7 7 7 7v-4.1c5 0 8.5 1.6 11 5.1-1-5-4-10-11-11z"
                        />
                      </svg>
                      <span>回复</span>
                    </span>
                  </div>
                </div>
              </div>
              <!-- 回复子评论的输入框 -->
              <div
                class="lc-sub-reply-input"
                v-if="replyingTo === index && replyingToReplyId === reply.id"
              >
                <div class="lc-reply-input-box">
                  <div class="lc-reply-avatar">
                    <img :src="currentUserAvatar" alt="用户头像" />
                  </div>
                  <div class="lc-reply-input-wrapper">
                    <textarea
                      class="lc-reply-input"
                      v-model="replyContent"
                      :placeholder="'@' + reply.nickname"
                      rows="1"
                      @input="autoResize"
                    />
                  </div>
                </div>
                <div class="lc-reply-actions">
                  <button class="lc-cancel-btn" @click="cancelReply">取消</button>
                  <button
                    class="lc-reply-submit-btn"
                    :disabled="!replyContent.trim()"
                    @click="submitReply(item)"
                  >
                    回复
                  </button>
                </div>
              </div>
            </template>

            <!-- 隐藏按钮 -->
            <div class="lc-hide-replies" @click="toggleReplies(index)">
              <svg class="lc-hide-icon" viewBox="0 0 24 24" width="14" height="14">
                <path fill="currentColor" d="M7.41 15.41L12 10.83l4.59 4.58L18 14l-6-6-6 6z" />
              </svg>
              <span>隐藏</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="lc-pagination" v-if="totalPages > 1">
        <v-pagination
          v-model="current"
          :length="totalPages"
          :total-visible="5"
          density="comfortable"
          rounded
          @update:model-value="listComments"
        />
      </div>
    </div>

    <!-- 空状态 -->
    <div class="lc-empty" v-else>来发评论吧~</div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import CommentEmoji from '@/components/Emoji/CommentEmoji.vue'
import { formatTime } from '@/utils/date'
import useLikeStore from '@/stores/modules/like'
import useUserInfoStore from '@/stores/modules/userInfo'
import { useSnackbarStore } from '@/stores/modules/snackbar'
import { LikeTypeEnum } from '@/constants/likeType'
import type { Comment, Reply } from '@/apis/comment'
import { getComments } from '@/apis/comment'
import { useEmoji } from '@/composables/useEmoji'
import { CommentTypeEnum } from '@/constants/commentType'

// props
const props = defineProps<{
  type: number
  typeId?: string
}>()

// stores
const likeStore = useLikeStore()
const userInfoStore = useUserInfoStore()
const snackbarStore = useSnackbarStore()

// 评论状态
const commentContent = ref('')
const replyContent = ref('')
const count = ref(0)
const commentList = ref<Comment[]>([])
const postUserId = ref('')
const replyingTo = ref<number | null>(null)
const replyingToReplyId = ref<number | null>(null)
const showReplies = reactive<Record<number, boolean>>({})
const sortType = ref<'hot' | 'new'>('hot')
const showSortMenu = ref(false)
const current = ref(1)
const loading = ref(false)
const pageSize = 10

// 计算总页数
const totalPages = computed(() => Math.ceil(count.value / pageSize))

// 当前用户头像
const currentUserAvatar = computed(() => userInfoStore.userInfo.avatar ?? '')

// 表情相关
const {
  showEmoji,
  emojiDirection,
  emojiTriggerRef,
  toggleEmoji,
  closeEmoji,
  registerClickOutside,
  unregisterClickOutside,
} = useEmoji()

// 提交评论
const insertComment = () => {
  if (!userInfoStore.checkLogin('评论')) return
  if (!commentContent.value.trim()) {
    snackbarStore.error('评论内容不能为空')
    return
  }
  // TODO: 实现提交评论逻辑
  console.log('提交评论:', commentContent.value)
  commentContent.value = ''
  closeEmoji()
}

// 添加表情
const addEmoji = (key: string) => {
  commentContent.value += key
}

// 点赞
const like = async (item: Comment | Reply) => {
  const isLiked = await likeStore.toggleLike(LikeTypeEnum.COMMENT, item.id)
  if (isLiked === null) return
  item.likeCount += isLiked ? 1 : -1
}

// 判断是否已点赞
const isLike = (id: number): boolean => {
  return likeStore.isLiked(LikeTypeEnum.COMMENT, id)
}

// 回复评论
const replyComment = (index: number) => {
  if (!userInfoStore.checkLogin('回复')) return
  if (replyingTo.value === index && replyingToReplyId.value === null) {
    cancelReply()
  } else {
    replyingTo.value = index
    replyingToReplyId.value = null
    replyContent.value = ''
  }
}

// 回复子评论
const replyToReply = (index: number, reply: Reply) => {
  if (!userInfoStore.checkLogin('回复')) return
  replyingTo.value = index
  replyingToReplyId.value = reply.id
  replyContent.value = ''
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  replyingToReplyId.value = null
  replyContent.value = ''
}

// 提交回复
const submitReply = (item: Comment) => {
  if (!userInfoStore.checkLogin('回复')) return
  if (!replyContent.value.trim()) {
    snackbarStore.error('回复内容不能为空')
    return
  }
  console.log('回复:', replyContent.value, '到:', item)
  // TODO: 实现提交回复逻辑
  cancelReply()
}

// 切换回复列表显示
const toggleReplies = (index: number) => {
  showReplies[index] = !showReplies[index]
}

// 加载评论列表
const listComments = async () => {
  // 对于需要typeId的类型，如果typeId为空或不是数字，则不发起请求
  if (([CommentTypeEnum.ARTICLE, CommentTypeEnum.TALK] as number[]).includes(props.type) && (!props.typeId || isNaN(Number(props.typeId)))) {
    commentList.value = []
    count.value = 0
    return
  }
  
  if (loading.value) return
  loading.value = true
  try {
    const res = await getComments({
      type: props.type,
      typeId: props.typeId,
      current: current.value,
      sortType: sortType.value,
    })
    commentList.value = res.data.list
    count.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 选择排序方式
const selectSort = (type: 'hot' | 'new') => {
  if (sortType.value === type) {
    showSortMenu.value = false
    return
  }
  sortType.value = type
  showSortMenu.value = false
  current.value = 1
  listComments()
}

// 自动调整textarea高度
const autoResize = (event: Event) => {
  const textarea = event.target as HTMLTextAreaElement
  textarea.style.height = 'auto'
  textarea.style.height = textarea.scrollHeight + 'px'
}

// 点击外部关闭下拉菜单
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (!target.closest('.lc-sort-dropdown-wrapper')) {
    showSortMenu.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  registerClickOutside()
  listComments()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  unregisterClickOutside()
})
</script>

<style scoped>
.lc-comment-container {
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: #fff;
}

/* 输入框 */
.lc-input-box {
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  margin: 16px 0;
  background: #fff;
}

.lc-input-box:focus-within {
  border-color: #d9d9d9;
}

.lc-textarea {
  width: 100%;
  min-height: 80px;
  padding: 16px;
  border: none;
  outline: none;
  resize: none;
  font-size: 14px;
  line-height: 1.6;
  color: #262626;
  box-sizing: border-box;
  background: transparent;
}

.lc-textarea::placeholder {
  color: #bfbfbf;
}

.lc-input-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
}

.lc-toolbar-left {
  display: flex;
  gap: 20px;
}

.lc-emoji-trigger-wrapper {
  position: relative;
}

.lc-tool-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #3c3c3c;
  font-size: 1.25rem;
}

.lc-tool-icon:hover,
.lc-tool-icon.active {
  color: #5cb85c;
}

.lc-submit-btn {
  padding: 8px 24px;
  background: #2db55d;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.lc-submit-btn:hover:not(:disabled) {
  background: #26a452;
}

.lc-submit-btn:disabled {
  background: #88d4a0;
  cursor: not-allowed;
}

.lc-emoji-panel {
  position: absolute;
  left: -5px;
  z-index: 1000;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 10px;
}

/* 向下展开（默认） */
.lc-emoji-panel.down {
  top: calc(100% + 8px);
  bottom: auto;
}

.lc-emoji-panel.down::before {
  content: '';
  position: absolute;
  top: -7px;
  left: 10px;
  width: 12px;
  height: 12px;
  background: #fff;
  border-left: 1px solid #e5e5e5;
  border-top: 1px solid #e5e5e5;
  transform: rotate(45deg);
  box-shadow: -2px -2px 4px rgba(0, 0, 0, 0.03);
}

/* 向上展开 */
.lc-emoji-panel.up {
  bottom: calc(100% + 8px);
  top: auto;
}

.lc-emoji-panel.up::before {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 10px;
  width: 12px;
  height: 12px;
  background: #fff;
  border-right: 1px solid #e5e5e5;
  border-bottom: 1px solid #e5e5e5;
  transform: rotate(45deg);
  box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.03);
}

/* 排序 */
.lc-sort-bar {
  padding: 16px 0;
  font-size: 14px;
  color: #3c3c3c;
  display: flex;
  align-items: center;
}

.lc-sort-label {
  color: #8c8c8c;
}

.lc-sort-dropdown-wrapper {
  position: relative;
}

.lc-sort-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.lc-sort-value {
  font-weight: 600;
  color: #262626;
}

.lc-dropdown-icon {
  color: #8c8c8c;
  margin-left: 2px;
  transition: transform 0.2s;
}

.lc-dropdown-icon.rotate {
  transform: rotate(180deg);
}

.lc-sort-menu {
  position: absolute;
  top: 100%;
  left: -12px;
  margin-top: 8px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 120px;
  padding: 8px;
  z-index: 100;
}

.lc-sort-menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  cursor: pointer;
  font-size: 14px;
  color: #262626;
  border-radius: 6px;
}

.lc-sort-menu-item:hover {
  background: #f5f5f5;
}

.lc-sort-menu-item.active {
  color: #007aff;
}

.lc-check-icon {
  color: #007aff;
  width: 16px;
  height: 16px;
}

/* 评论列表 */
.lc-comment-list {
  padding-top: 8px;
}

.lc-comment-item {
  display: flex;
  padding: 20px 0;
  border-bottom: 1px solid #ebebeb;
}

.lc-comment-item:last-child {
  border-bottom: none;
}

/* 头像 */
.lc-avatar {
  position: relative;
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  margin-right: 12px;
}

.lc-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.lc-avatar.small {
  width: 32px;
  height: 32px;
}

.lc-online-dot {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 10px;
  height: 10px;
  background: #ffc107;
  border: 2px solid #fff;
  border-radius: 50%;
}

/* 评论主体 */
.lc-comment-main {
  flex: 1;
  min-width: 0;
}

/* 用户名行 */
.lc-user-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.lc-nickname {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  text-decoration: none;
}

.lc-nickname:hover {
  color: #5cb85c;
}

.lc-badge {
  width: 18px;
  height: 18px;
}

.lc-blogger-tag {
  display: inline-block;
  padding: 1px 6px;
  font-size: 12px;
  color: #fff;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
}

/* 元信息 */
.lc-meta-row {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 4px;
}

.lc-meta-divider {
  width: 3px;
  height: 3px;
  background: #8c8c8c;
  border-radius: 50%;
  margin: 0 4px;
}

/* 内容 */
.lc-content {
  font-size: 14px;
  line-height: 1.8;
  color: #262626;
  margin: 12px 0;
  word-break: break-word;
}

.lc-content :deep(a) {
  color: #40a9ff;
  text-decoration: none;
}

.lc-content :deep(a:hover) {
  text-decoration: underline;
}

/* 操作栏 */
.lc-action-row {
  display: flex;
  align-items: center;
  gap: 24px;
  font-size: 13px;
  color: #8c8c8c;
}

.lc-action-item {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}

.lc-action-item:hover {
  color: #262626;
}

.lc-action-icon {
  width: 16px;
  height: 16px;
}

.lc-action-icon.liked {
  color: #5cb85c;
}

.lc-more-actions {
  margin-left: auto;
}

/* 回复输入框 */
.lc-reply-input-box {
  display: flex;
  align-items: center;
  margin-top: 16px;
  gap: 12px;
}

.lc-reply-avatar {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.lc-reply-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.lc-reply-input-wrapper {
  flex: 1;
}

.lc-reply-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  background: #fff;
  resize: none;
  overflow-y: hidden;
  min-height: 42px;
  line-height: 1.5;
  font-family: inherit;
  box-sizing: border-box;
}

.lc-reply-input::placeholder {
  color: #bfbfbf;
}

.lc-reply-input:focus {
  border-color: #d9d9d9;
}

.lc-reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}

.lc-cancel-btn {
  padding: 8px 20px;
  background: #000a200d;
  color: #262626;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.lc-cancel-btn:hover {
  background: #000a201a;
}

.lc-reply-submit-btn {
  padding: 8px 20px;
  background: #2db55d;
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.lc-reply-submit-btn:hover:not(:disabled) {
  background: #26a452;
}

.lc-reply-submit-btn:disabled {
  background: #88d4a0;
  cursor: not-allowed;
}

/* 回复列表 */
.lc-reply-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.lc-reply-item {
  display: flex;
  padding: 16px;
  background: hsl(0, 0%, 0%, 0.04);
  border-radius: 8px;
}

.lc-reply-item:first-child {
  padding-top: 16px;
}

.lc-reply-main {
  flex: 1;
  min-width: 0;
}

.lc-reply-to {
  color: #40a9ff;
  margin-right: 4px;
}

/* 子评论回复输入框 */
.lc-sub-reply-input {
  border-radius: 8px;
  padding: 16px;
}

.lc-sub-reply-input .lc-reply-input-box {
  margin-top: 0;
}

.lc-sub-reply-input .lc-reply-actions {
  margin-top: 12px;
}

/* 隐藏按钮 */
.lc-hide-replies {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
  padding-top: 12px;
  font-size: 13px;
  color: #8c8c8c;
  cursor: pointer;
}

.lc-hide-replies:hover {
  color: #262626;
}

.lc-hide-icon {
  width: 14px;
  height: 14px;
}

/* 更多回复 */
.lc-more-reply {
  font-size: 13px;
  color: #8c8c8c;
  margin-top: 12px;
}

.lc-link {
  color: #40a9ff;
  cursor: pointer;
}

.lc-link:hover {
  text-decoration: underline;
}

/* 分页 */
.lc-paging {
  font-size: 13px;
  color: #595959;
  margin-top: 10px;
}

/* 加载更多 */
.lc-load-more {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

.lc-load-btn {
  padding: 10px 32px;
  background: #fff;
  color: #5cb85c;
  border: 1px solid #5cb85c;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.lc-load-btn:hover {
  background: #5cb85c;
  color: #fff;
}

/* 空状态 */
.lc-empty {
  padding: 80px 0;
  text-align: center;
  color: #8c8c8c;
  font-size: 14px;
}

/* 点赞样式 */
.like {
  cursor: pointer;
}

.like-active {
  color: #5cb85c !important;
}
</style>

<!-- 夜间模式样式 -->
<style>
.v-theme--dark .lc-comment-container {
  background: transparent;
}

.v-theme--dark .lc-icon {
  color: var(--color-text-primary);
}

.v-theme--dark .lc-comment-title,
.v-theme--dark .lc-comment-count {
  color: var(--color-text-title);
}

/* 输入框 */
.v-theme--dark .lc-input-box {
  background: var(--color-bg-light);
  border-color: var(--color-border);
}

.v-theme--dark .lc-input-box:focus-within {
  border-color: var(--color-border-focus);
}

.v-theme--dark .lc-textarea {
  color: var(--color-text-primary);
}

.v-theme--dark .lc-textarea::placeholder {
  color: var(--color-text-placeholder);
}

/* 表情面板 */
.v-theme--dark .lc-emoji-panel {
  background: #2a2a2a;
  box-shadow: var(--shadow-dropdown);
}

.v-theme--dark .lc-emoji-panel.down::before {
  background: #2a2a2a;
  border-left-color: #3d3d4a;
  border-top-color: #3d3d4a;
}

.v-theme--dark .lc-emoji-panel.up::before {
  background: #2a2a2a;
  border-right-color: #3d3d4a;
  border-bottom-color: #3d3d4a;
}

.v-theme--dark .lc-tool-icon {
  color: var(--color-text-secondary);
}

.v-theme--dark .lc-tool-icon:hover,
.v-theme--dark .lc-tool-icon.active {
  color: var(--color-success);
}

/* 排序 */
.v-theme--dark .lc-sort-bar {
  color: var(--color-text-secondary);
}

.v-theme--dark .lc-sort-label {
  color: var(--color-text-tertiary);
}

.v-theme--dark .lc-sort-value {
  color: var(--color-text-primary);
}

.v-theme--dark .lc-dropdown-icon {
  color: var(--color-text-tertiary);
}

.v-theme--dark .lc-sort-menu {
  background: #2a2a2a;
  box-shadow: var(--shadow-dropdown);
}

.v-theme--dark .lc-sort-menu-item {
  color: var(--color-text-primary);
}

.v-theme--dark .lc-sort-menu-item:hover {
  background: var(--color-bg-hover);
}

/* 评论列表 */
.v-theme--dark .lc-comment-item {
  border-bottom-color: var(--color-border-light);
}

.v-theme--dark .lc-nickname {
  color: var(--color-text-primary);
}

.v-theme--dark .lc-nickname:hover {
  color: var(--color-success);
}

.v-theme--dark .lc-meta-row {
  color: var(--color-text-tertiary);
}

.v-theme--dark .lc-meta-divider {
  background: rgba(255, 255, 255, 0.3);
}

.v-theme--dark .lc-content {
  color: var(--color-text-primary);
}

/* 操作栏 */
.v-theme--dark .lc-action-row {
  color: var(--color-text-tertiary);
}

.v-theme--dark .lc-action-item:hover {
  color: var(--color-text-primary);
}

/* 回复输入框 */
.v-theme--dark .lc-reply-input {
  background: var(--color-bg-light);
  border-color: var(--color-border);
  color: var(--color-text-primary);
}

.v-theme--dark .lc-reply-input::placeholder {
  color: var(--color-text-placeholder);
}

.v-theme--dark .lc-reply-input:focus {
  border-color: var(--color-border-focus);
}

.v-theme--dark .lc-cancel-btn {
  background: var(--color-bg-light);
  color: var(--color-text-primary);
}

.v-theme--dark .lc-cancel-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}

/* 回复列表 */
.v-theme--dark .lc-reply-item {
  background: rgba(255, 255, 255, 0.06);
}

.v-theme--dark .lc-sub-reply-input {
  background: rgba(255, 255, 255, 0.04);
}

/* 隐藏按钮 */
.v-theme--dark .lc-hide-replies {
  color: var(--color-text-tertiary);
}

.v-theme--dark .lc-hide-replies:hover {
  color: var(--color-text-primary);
}

/* 加载更多 */
.v-theme--dark .lc-load-btn {
  background: transparent;
  border-color: var(--color-success);
  color: var(--color-success);
}

.v-theme--dark .lc-load-btn:hover {
  background: var(--color-success);
  color: #fff;
}

/* 空状态 */
.v-theme--dark .lc-empty {
  color: var(--color-text-tertiary);
}

/* 更多回复链接 */
.v-theme--dark .lc-more-reply {
  color: var(--color-text-tertiary);
}

.v-theme--dark .lc-paging {
  color: var(--color-text-secondary);
}
</style>
