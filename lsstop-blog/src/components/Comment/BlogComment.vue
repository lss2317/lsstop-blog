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
          <v-icon :class="['lc-dropdown-icon', showSortMenu ? 'rotate' : '']" size="14"
            >mdi-chevron-down</v-icon
          >
        </span>
        <!-- 下拉菜单 -->
        <div class="lc-sort-menu" v-if="showSortMenu">
          <div
            :class="['lc-sort-menu-item', sortType === 'hot' ? 'active' : '']"
            @click="selectSort('hot')"
          >
            <span>最热</span>
            <v-icon v-if="sortType === 'hot'" class="lc-check-icon" size="16">mdi-check</v-icon>
          </div>
          <div
            :class="['lc-sort-menu-item', sortType === 'new' ? 'active' : '']"
            @click="selectSort('new')"
          >
            <span>最新</span>
            <v-icon v-if="sortType === 'new'" class="lc-check-icon" size="16">mdi-check</v-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div class="lc-loading" v-if="loading">
      <v-progress-circular indeterminate color="green" size="32" />
      <span>加载中...</span>
    </div>

    <!-- 评论列表 -->
    <div class="lc-comment-list" v-else-if="count > 0">
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
          <div class="lc-content" v-html="parseEmoji(item.content)"></div>
          <!-- 操作栏 -->
          <div class="lc-action-row">
            <span
              :class="['lc-action-item', 'like-item', isLike(item.id) ? 'liked' : '']"
              @click="like(item)"
            >
              <v-icon size="16" class="like-btn">
                {{ isLike(item.id) ? 'mdi-thumb-up' : 'mdi-thumb-up-outline' }}
              </v-icon>
              <span>{{ item.likeCount }}</span>
            </span>
            <span
              class="lc-action-item comment-btn"
              v-if="item.replyCount > 0"
              @click="toggleReplies(index)"
            >
              <v-icon size="16">mdi-comment-text-outline</v-icon>
              <span>{{
                showReplies[index] ? '隐藏回复' : '展示 ' + item.replyCount + ' 条回复'
              }}</span>
            </span>
            <span class="lc-action-item reply-btn" @click="replyComment(index)">
              <v-icon size="16">mdi-reply</v-icon>
              <span>回复</span>
            </span>
          </div>

          <!-- 回复输入框（回复主评论） -->
          <ReplyInput
            v-if="replyingTo === index && replyingToReplyId === null"
            :avatar="currentUserAvatar"
            v-model="replyContent"
            placeholder="请输入回复 ..."
            @submit="submitReply(item)"
            @cancel="cancelReply"
            @add-emoji="addReplyEmoji"
          />

          <!-- 回复列表 -->
          <div
            class="lc-reply-list"
            v-if="item.replyList && item.replyList.length && showReplies[index]"
          >
            <template v-for="reply of getVisibleReplies(index, item.replyList)" :key="reply.id">
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
                    <a
                      v-if="reply.replyNickname"
                      class="lc-reply-to"
                      :href="'/user/' + reply.replyUserId"
                      >@{{ reply.replyNickname }}</a
                    ><span v-html="parseEmoji(reply.content)"></span>
                  </div>
                  <div class="lc-action-row">
                    <span
                      :class="['lc-action-item', 'like-item', isLike(reply.id) ? 'liked' : '']"
                      @click="like(reply)"
                    >
                      <v-icon size="16" class="like-btn">
                        {{ isLike(reply.id) ? 'mdi-thumb-up' : 'mdi-thumb-up-outline' }}
                      </v-icon>
                      <span>{{ reply.likeCount }}</span>
                    </span>
                    <span class="lc-action-item reply-btn" @click="replyToReply(index, reply)">
                      <v-icon size="16">mdi-reply</v-icon>
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
                <ReplyInput
                  :avatar="currentUserAvatar"
                  v-model="replyContent"
                  :placeholder="'@' + reply.nickname"
                  @submit="submitReply(item)"
                  @cancel="cancelReply"
                  @add-emoji="addReplyEmoji"
                />
              </div>
            </template>

            <!-- 展示更多/隐藏操作栏 -->
            <div class="lc-reply-actions-bar">
              <span
                class="lc-show-more"
                v-if="hasMoreReplies(index, item.replyList)"
                @click="showMoreReplies(index)"
                >展示更多</span
              >
              <span class="lc-hide-replies" @click="hideReplies(index)">
                <v-icon class="lc-hide-icon" size="14">mdi-chevron-up</v-icon>
                <span>隐藏</span>
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="lc-pagination" v-if="totalPages > 1">
        <v-pagination
          v-model="current"
          :length="totalPages"
          :total-visible="7"
          density="comfortable"
          variant="flat"
          @update:model-value="listComments"
        />
      </div>
    </div>

    <!-- 空状态 -->
    <div class="lc-empty" v-else-if="!loading">来发评论吧~</div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import CommentEmoji from '@/components/Emoji/CommentEmoji.vue'
import ReplyInput from '@/components/Comment/ReplyInput.vue'
import { formatTime } from '@/utils/date'
import { parseEmoji } from '@/utils/emoji'
import useLikeStore from '@/stores/modules/like'
import useUserInfoStore from '@/stores/modules/userInfo'
import { useSnackbarStore } from '@/stores/modules/snackbar'
import { LikeTypeEnum } from '@/constants/likeType'
import type { Comment, Reply } from '@/apis/comment'
import { getComments, addComment } from '@/apis/comment'
import { useEmoji } from '@/composables/useEmoji'
import { CommentTypeEnum } from '@/constants/commentType'
import { ReviewStatusEnum } from '@/constants/reviewStatus'

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
const visibleReplyCount = reactive<Record<number, number>>({})
const replyPageSize = 5 // 每次加载的子评论数量
const sortType = ref<'hot' | 'new'>('hot')
const showSortMenu = ref(false)
const current = ref(1)
const loading = ref(false)
//每页默认最大条数
const pageSize = 10

// 计算总页数
const totalPages = computed(() => Math.ceil(count.value / pageSize))

// 当前用户头像
const currentUserAvatar = computed(() => userInfoStore.userInfo.avatar ?? '')

// 主评论表情
const {
  showEmoji,
  emojiDirection,
  emojiTriggerRef,
  toggleEmoji,
  closeEmoji,
  registerClickOutside,
  unregisterClickOutside,
} = useEmoji()

// 点赞
const like = async (item: Comment | Reply) => {
  const isLiked = await likeStore.toggleLike(LikeTypeEnum.COMMENT, item.id)
  if (isLiked !== null) {
    item.likeCount += isLiked ? 1 : -1
  }
}

// 判断是否已点赞
const isLike = (id: number): boolean => {
  return likeStore.isLiked(LikeTypeEnum.COMMENT, id)
}

// 提交评论
const insertComment = async () => {
  if (!userInfoStore.checkLogin('评论')) return
  if (!commentContent.value.trim()) {
    snackbarStore.error('评论内容不能为空')
    return
  }

  try {
    const params = {
      targetType: props.type,
      targetId: Number(props.typeId),
      content: commentContent.value,
    }

    const res = await addComment(params)

    if (res.data.review === ReviewStatusEnum.PENDING) {
      snackbarStore.success('评论提交成功，等待审核')
    } else {
      // 不需要审核，本地插入新评论
      const newComment: Comment = {
        id: res.data.id,
        avatar: res.data.avatar,
        nickname: res.data.nickname,
        userId: res.data.userId,
        ipRegion: res.data.ipRegion,
        createTime: res.data.createTime,
        content: res.data.content,
        likeCount: 0,
        replyCount: 0,
        replyList: [],
      }
      commentList.value.unshift(newComment)
      count.value++
      snackbarStore.success('评论提交成功')
    }

    // 清空评论内容
    commentContent.value = ''
    closeEmoji()
  } catch (error: unknown) {
    const err = error as { response?: { data?: { msg?: string } } }
    snackbarStore.error(err.response?.data?.msg || '评论提交失败')
  }
}

// 添加表情
const addEmoji = (key: string) => {
  commentContent.value += key
}

// 添加回复表情
const addReplyEmoji = (key: string) => {
  replyContent.value += key
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
const submitReply = async (item: Comment) => {
  if (!userInfoStore.checkLogin('回复')) return
  if (!replyContent.value.trim()) {
    snackbarStore.error('回复内容不能为空')
    return
  }

  try {
    // 获取被回复的用户信息（如果是回复子评论）
    const replyToUser = replyingToReplyId.value
      ? item.replyList?.find((r) => r.id === replyingToReplyId.value)
      : null

    const params = {
      targetType: props.type,
      targetId: Number(props.typeId),
      content: replyContent.value,
      parentId: item.id, // 回复目标评论
      replyUserId: replyToUser?.userId, // 只有回复子评论时才有值
    }

    const res = await addComment(params)

    if (res.data.review === ReviewStatusEnum.PENDING) {
      snackbarStore.success('回复提交成功，等待审核')
    } else {
      // 不需要审核，本地插入新回复
      const newReply: Reply = {
        id: res.data.id,
        avatar: res.data.avatar,
        nickname: res.data.nickname,
        userId: res.data.userId,
        ipRegion: res.data.ipRegion,
        createTime: res.data.createTime,
        content: res.data.content,
        likeCount: 0,
        replyUserId: replyToUser?.userId,
        replyNickname: replyToUser?.nickname,
      }

      // 插入到回复列表（通过索引更新确保响应式）
      const index = commentList.value.findIndex((c) => c.id === item.id)
      const comment = commentList.value[index]
      if (comment) {
        if (!comment.replyList) {
          comment.replyList = []
        }
        comment.replyList.push(newReply)
        comment.replyCount++
        // 自动展开回复列表
        showReplies[index] = true
        // 确保新回复可见（更新显示数量）
        const currentVisible = visibleReplyCount[index] || replyPageSize
        if (comment.replyList.length > currentVisible) {
          visibleReplyCount[index] = comment.replyList.length
        }
      }

      snackbarStore.success('回复提交成功')
    }

    // 清空回复内容
    cancelReply()
  } catch (error: unknown) {
    console.error('提交回复失败:', error)
    const err = error as { response?: { data?: { msg?: string } } }
    snackbarStore.error(err.response?.data?.msg || '回复提交失败')
  }
}

// 切换回复列表显示
const toggleReplies = (index: number) => {
  showReplies[index] = !showReplies[index]
  // 初次展开时设置默认显示数量
  if (showReplies[index] && !visibleReplyCount[index]) {
    visibleReplyCount[index] = replyPageSize
  }
}

// 展示更多子评论
const showMoreReplies = (index: number) => {
  visibleReplyCount[index] = (visibleReplyCount[index] || replyPageSize) + replyPageSize
}

// 隐藏子评论列表
const hideReplies = (index: number) => {
  showReplies[index] = false
}

// 获取显示的子评论列表
const getVisibleReplies = (index: number, replyList: Reply[] | undefined) => {
  if (!replyList) return []
  const count = visibleReplyCount[index] || replyPageSize
  return replyList.slice(0, count)
}

// 是否还有更多子评论
const hasMoreReplies = (index: number, replyList: Reply[] | undefined) => {
  if (!replyList) return false
  const count = visibleReplyCount[index] || replyPageSize
  return replyList.length > count
}

// 加载评论列表
const listComments = async () => {
  // 对于需要typeId的类型，如果typeId为空或不是数字，则不发起请求
  if (
    ([CommentTypeEnum.ARTICLE, CommentTypeEnum.TALK] as number[]).includes(props.type) &&
    (!props.typeId || isNaN(Number(props.typeId)))
  ) {
    commentList.value = []
    count.value = 0
    return
  }

  if (loading.value) return
  loading.value = true
  try {
    const res = await getComments({
      type: props.type,
      typeId: props.typeId ? Number(props.typeId) : undefined,
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

/* 加载状态 */
.lc-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  gap: 12px;
  color: #8c8c8c;
  font-size: 14px;
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

/* 回复表情面板位置调整 */
.lc-emoji-panel.reply-panel {
  left: 0;
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

.lc-content :deep(.comment-emoji) {
  width: 20px;
  height: 20px;
  vertical-align: text-bottom;
  margin: 0 1px;
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
  padding: 4px 8px;
  border-radius: 16px;
  transition:
    background 0.2s ease,
    transform 0.2s ease;
}

.lc-action-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.lc-action-item :deep(.v-icon) {
  color: #8c8c8c;
}

/* 点赞按钮样式 */
.like-item:hover {
  color: #eb5055;
}

.like-item:hover :deep(.v-icon) {
  color: #eb5055 !important;
}

.like-item:hover .like-btn {
  transform: scale(1.2);
}

.like-btn {
  transition: transform 0.2s ease;
}

.like-item.liked {
  color: #eb5055;
}

.like-item.liked :deep(.v-icon) {
  color: #eb5055 !important;
}

/* 评论按钮样式 */
.comment-btn:hover {
  color: #1e80ff;
}

.comment-btn:hover :deep(.v-icon) {
  color: #1e80ff !important;
  transform: scale(1.2);
}

/* 回复按钮样式 */
.reply-btn:hover {
  color: #07c160;
}

.reply-btn:hover :deep(.v-icon) {
  color: #07c160 !important;
  transform: scale(1.2);
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
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  margin-left: calc(32px + 12px); /* 和评论框对齐（32px头像 + 12px gap） */
}

.lc-reply-btns {
  display: flex;
  gap: 12px;
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
  color: #007aff;
  font-weight: 500;
  margin-right: 4px;
  text-decoration: none;
}

.lc-reply-to:visited {
  color: #007aff;
}

.lc-reply-to:hover {
  text-decoration: underline;
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

/* 展示更多/隐藏操作栏 */
.lc-reply-actions-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
}

.lc-show-more {
  font-size: 13px;
  font-weight: 500;
  color: #007aff;
  cursor: pointer;
}

/* 隐藏按钮 */
.lc-hide-replies {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #8c8c8c;
  cursor: pointer;
  margin-left: auto;
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

.v-theme--dark .lc-show-more {
  color: #007aff;
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

/* 加载状态 */
.v-theme--dark .lc-loading {
  color: var(--color-text-tertiary);
}

/* 更多回复链接 */
.v-theme--dark .lc-more-reply {
  color: var(--color-text-tertiary);
}

.v-theme--dark .lc-paging {
  color: var(--color-text-secondary);
}

/* 分页样式 */
.lc-pagination {
  display: flex;
  justify-content: center;
  padding: 24px 0 8px;
}

.lc-pagination .v-pagination__list {
  gap: 4px;
}

/* 普通按钮 - bg-fill-3 */
.lc-pagination .v-pagination .v-btn {
  min-width: 32px !important;
  height: 32px !important;
  border-radius: 5px !important;
  background-color: rgba(0, 0, 0, 0.04) !important;
  box-shadow: none !important;
  color: rgba(0, 0, 0, 0.55) !important;
  font-size: 14px !important;
  font-weight: 400 !important;
  transition: background-color 0.2s ease !important;
}

.lc-pagination .v-pagination .v-btn .v-btn__overlay,
.lc-pagination .v-pagination .v-btn .v-btn__underlay {
  display: none !important;
}

/* hover - bg-fill-2 */
.lc-pagination .v-pagination .v-btn:hover {
  background-color: rgba(0, 0, 0, 0.08) !important;
}

/* 当前选中页 - bg-paper + shadow-level1 */
.lc-pagination .v-pagination__item--is-active .v-btn {
  background-color: #fff !important;
  color: rgba(0, 0, 0, 0.85) !important;
  box-shadow:
    0px 0px 1px 0px rgba(0, 0, 0, 0.1),
    0px 0.5px 5px 0px rgba(0, 0, 0, 0.1) !important;
  pointer-events: none !important;
}

/* 禁用状态 - disabled:opacity-40 */
.lc-pagination .v-pagination .v-btn--disabled {
  background-color: rgba(0, 0, 0, 0.04) !important;
  box-shadow: none !important;
  color: rgba(0, 0, 0, 0.55) !important;
  opacity: 0.4 !important;
}

/* 省略号 - disabled + bg-fill-3 */
.lc-pagination .v-pagination__more .v-btn {
  background-color: rgba(0, 0, 0, 0.04) !important;
  box-shadow: none !important;
  color: rgba(0, 0, 0, 0.55) !important;
  opacity: 0.4 !important;
}

/* 分页夜间模式 - dark-fill-3, dark-label-2, dark-gray-5 */
.v-theme--dark .lc-pagination .v-pagination .v-btn {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.6) !important;
}

.v-theme--dark .lc-pagination .v-pagination .v-btn:hover {
  background-color: rgba(255, 255, 255, 0.15) !important;
}

.v-theme--dark .lc-pagination .v-pagination__item--is-active .v-btn {
  background-color: rgba(255, 255, 255, 0.9) !important;
  color: rgba(0, 0, 0, 0.85) !important;
  box-shadow:
    0px 0px 1px 0px rgba(0, 0, 0, 0.3),
    0px 0.5px 5px 0px rgba(0, 0, 0, 0.3) !important;
}

.v-theme--dark .lc-pagination .v-pagination .v-btn--disabled {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.6) !important;
}

.v-theme--dark .lc-pagination .v-pagination__more .v-btn {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.6) !important;
}
</style>
