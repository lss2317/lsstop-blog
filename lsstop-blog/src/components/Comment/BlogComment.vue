<template>
  <div class="lc-comment-container">
    <!-- 评论输入框 -->
    <div class="lc-input-box">
      <textarea
        ref="commentTextareaRef"
        class="lc-textarea"
        v-model="commentContent"
        placeholder="请输入评论..."
        @input="handleTextareaInput"
        @focus="saveCursor"
        @keyup="saveCursor"
        @mouseup="saveCursor"
      />
      <div class="lc-input-toolbar">
        <div class="lc-toolbar-left">
          <div class="lc-emoji-trigger-wrapper">
            <span
              ref="emojiTriggerRef"
              :class="['lc-tool-icon', showEmoji ? 'active' : '']"
              title="表情"
              @mousedown.prevent
              @click="handleToggleEmoji"
            >
              <v-icon size="20">mdi-emoticon-outline</v-icon>
            </span>
            <!-- 表情框 -->
            <div :class="['lc-emoji-panel', emojiDirection]" v-show="showEmoji" @mousedown.prevent>
              <CommentEmoji @addEmoji="addEmoji" />
            </div>
          </div>
        </div>
        <button
          class="lc-submit-btn"
          :disabled="!commentContent.trim() || submitting"
          @click="insertComment"
        >
          {{ submitting ? '提交中...' : '评论' }}
        </button>
      </div>
    </div>

    <!-- 排序 -->
    <div class="lc-sort-bar" ref="sortBarRef" v-if="count > 0">
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
    <LoadingSpinner v-if="loading" />

    <!-- 评论列表 -->
    <div class="lc-comment-list" v-else-if="count > 0">
      <div class="lc-comment-item" v-for="item of commentList" :key="item.id">
        <!-- 头像 -->
        <div class="lc-avatar">
          <img :src="item.avatar" alt="用户头像" />
        </div>
        <div class="lc-comment-main">
          <!-- 用户信息 -->
          <div class="lc-user-row">
            <span class="lc-nickname">{{ item.nickname }}</span>
            <span class="lc-self-tag" v-if="isSelf(item.userId)">我</span>
          </div>
          <!-- 时间地点 -->
          <div class="lc-meta-row">
            <span>来自 {{ item.ipRegion }}</span>
            <span class="lc-meta-divider"></span>
            <span>{{ formatTime(item.createTime) }}</span>
          </div>
          <!-- 评论内容 -->
          <div class="lc-content-wrapper" :class="{ expanded: expandedCommentMap[item.id] }">
            <div
              :class="['lc-content', !expandedCommentMap[item.id] ? 'collapsed' : '']"
              :ref="(el) => setCommentContentRef(item.id, el as HTMLElement)"
              v-html="parseEmoji(item.content)"
            ></div>
            <div
              class="lc-content-fade"
              v-if="overflowCommentMap[item.id] && !expandedCommentMap[item.id]"
            ></div>
            <span
              class="lc-expand-btn"
              v-if="overflowCommentMap[item.id]"
              @click="toggleExpandComment(item.id)"
            >
              <v-icon size="14">{{
                expandedCommentMap[item.id] ? 'mdi-chevron-up' : 'mdi-chevron-down'
              }}</v-icon>
              <span>{{ expandedCommentMap[item.id] ? '收起' : '查看更多' }}</span>
            </span>
          </div>
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
              @click="toggleReplies(item.id)"
            >
              <v-icon size="16">mdi-comment-text-outline</v-icon>
              <span>{{
                showRepliesMap[item.id] ? '隐藏回复' : '展示 ' + item.replyCount + ' 条回复'
              }}</span>
            </span>
            <span class="lc-action-item reply-btn" @click="replyComment(item.id)">
              <v-icon size="16">mdi-reply</v-icon>
              <span>回复</span>
            </span>
            <span
              class="lc-action-item delete-btn"
              v-if="isSelf(item.userId)"
              @click="handleDeleteComment(item.id)"
            >
              <v-icon size="16">mdi-trash-can-outline</v-icon>
              <span>删除</span>
            </span>
          </div>

          <!-- 回复输入框（回复主评论） -->
          <ReplyInput
            v-if="replyingTo === item.id && replyingToReplyId === null"
            :avatar="currentUserAvatar"
            v-model="replyContent"
            placeholder="请输入回复 ..."
            :submitting="submitting"
            @submit="submitReply(item)"
            @cancel="cancelReply"
          />

          <!-- 回复列表 -->
          <div class="lc-reply-list" v-if="showRepliesMap[item.id]">
            <!-- 骨架屏加载状态 -->
            <div
              class="lc-reply-skeleton"
              v-if="loadingReplyMap[item.id] && (!item.replyList || !item.replyList.length)"
            >
              <div class="lc-skeleton-item" v-for="n in 2" :key="n">
                <div class="lc-skeleton-avatar"></div>
                <div class="lc-skeleton-content">
                  <div class="lc-skeleton-line short"></div>
                  <div class="lc-skeleton-line tiny"></div>
                  <div class="lc-skeleton-line"></div>
                  <div class="lc-skeleton-line medium"></div>
                  <div class="lc-skeleton-actions">
                    <div class="lc-skeleton-action"></div>
                    <div class="lc-skeleton-action"></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 子评论列表 -->
            <template v-if="item.replyList && item.replyList.length">
              <template v-for="reply of item.replyList ?? []" :key="reply.id">
                <div class="lc-reply-item">
                  <div class="lc-avatar small">
                    <img :src="reply.avatar" alt="用户头像" />
                  </div>
                  <div class="lc-reply-main">
                    <div class="lc-user-row">
                      <span class="lc-nickname">{{ reply.nickname }}</span>
                      <span class="lc-self-tag" v-if="isSelf(reply.userId)">我</span>
                    </div>
                    <div class="lc-meta-row">
                      <span>来自 {{ reply.ipRegion }}</span>
                      <span class="lc-meta-divider"></span>
                      <span>{{ formatTime(reply.createTime) }}</span>
                    </div>
                    <div
                      class="lc-content-wrapper"
                      :class="{ expanded: expandedReplyMap[reply.id] }"
                    >
                      <div
                        :class="['lc-content', !expandedReplyMap[reply.id] ? 'collapsed' : '']"
                        :ref="(el) => setReplyContentRef(reply.id, el as HTMLElement)"
                      >
                        <a
                          v-if="reply.replyNickname"
                          class="lc-reply-to"
                          :href="'/user/' + reply.replyUserId"
                          >@{{ reply.replyNickname }}</a
                        ><span v-html="parseEmoji(reply.content)"></span>
                      </div>
                      <div
                        class="lc-content-fade"
                        v-if="overflowReplyMap[reply.id] && !expandedReplyMap[reply.id]"
                      ></div>
                      <span
                        class="lc-expand-btn"
                        v-if="overflowReplyMap[reply.id]"
                        @click="toggleExpandReply(reply.id)"
                      >
                        <v-icon size="14">{{
                          expandedReplyMap[reply.id] ? 'mdi-chevron-up' : 'mdi-chevron-down'
                        }}</v-icon>
                        <span>{{ expandedReplyMap[reply.id] ? '收起' : '查看更多' }}</span>
                      </span>
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
                      <span class="lc-action-item reply-btn" @click="replyToReply(item.id, reply)">
                        <v-icon size="16">mdi-reply</v-icon>
                        <span>回复</span>
                      </span>
                      <span
                        class="lc-action-item delete-btn"
                        v-if="isSelf(reply.userId)"
                        @click="handleDeleteReply(item.id, reply.id)"
                      >
                        <v-icon size="16">mdi-trash-can-outline</v-icon>
                        <span>删除</span>
                      </span>
                    </div>
                  </div>
                </div>
                <!-- 回复子评论的输入框 -->
                <div
                  class="lc-sub-reply-input"
                  v-if="replyingTo === item.id && replyingToReplyId === reply.id"
                >
                  <ReplyInput
                    :avatar="currentUserAvatar"
                    v-model="replyContent"
                    :placeholder="'@' + reply.nickname"
                    :submitting="submitting"
                    @submit="submitReply(item)"
                    @cancel="cancelReply"
                  />
                </div>
              </template>
            </template>

            <!-- 加载更多骨架屏 -->
            <div
              class="lc-reply-skeleton"
              v-if="loadingReplyMap[item.id] && item.replyList && item.replyList.length"
            >
              <div class="lc-skeleton-item">
                <div class="lc-skeleton-avatar"></div>
                <div class="lc-skeleton-content">
                  <div class="lc-skeleton-line short"></div>
                  <div class="lc-skeleton-line tiny"></div>
                  <div class="lc-skeleton-line"></div>
                  <div class="lc-skeleton-line medium"></div>
                  <div class="lc-skeleton-actions">
                    <div class="lc-skeleton-action"></div>
                    <div class="lc-skeleton-action"></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 展示更多/隐藏操作栏 -->
            <div class="lc-reply-actions-bar">
              <span
                class="lc-show-more"
                v-if="hasMoreReplies(item.id, item.replyList)"
                @click="showMoreReplies(item.id)"
                >展示更多</span
              >
              <span class="lc-hide-replies" @click="hideReplies(item.id)">
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
          @update:model-value="handlePageChange"
        />
      </div>
    </div>

    <!-- 空状态 -->
    <div class="lc-empty" v-else-if="!loading">来发评论吧~</div>

    <!-- 删除确认弹框 -->
    <ConfirmDialog
      v-model="deleteDialog"
      type="error"
      content="确定删除该条评论？"
      confirm-text="删除"
      :loading="deleting"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue';
import CommentEmoji from '@/components/Emoji/CommentEmoji.vue';
import ReplyInput from '@/components/Comment/ReplyInput.vue';
import ConfirmDialog from '@/components/Dialog/ConfirmDialog.vue';
import LoadingSpinner from '@/components/Loading/LoadingSpinner.vue';
import { formatTime } from '@/utils/date';
import { parseEmoji } from '@/utils/emoji';
import useLikeStore from '@/stores/modules/like';
import useUserInfoStore from '@/stores/modules/userInfo';
import { useSnackbarStore } from '@/stores/modules/snackbar';
import { LikeTypeEnum } from '@/constants/likeType';
import type { Comment, Reply } from '@/apis/comment';
import { getComments, addComment, getReplyList, deleteComment } from '@/apis/comment';
import { useEmoji } from '@/composables/useEmoji';
import { useContentOverflow } from '@/composables/useContentOverflow';
import { requiresTypeId } from '@/constants/commentType';
import { ReviewStatusEnum } from '@/constants/reviewStatus';
import { getErrorMessage } from '@/utils/error';

// props
const props = defineProps<{
  type: number;
  typeId?: string;
}>();

// stores
const likeStore = useLikeStore();
const userInfoStore = useUserInfoStore();
const snackbarStore = useSnackbarStore();

// 评论状态
const commentContent = ref('');
const commentTextareaRef = ref<HTMLTextAreaElement | null>(null);
const initialTextareaHeight = ref(0);
const replyContent = ref('');
const count = ref(0);
const commentList = ref<Comment[]>([]);
const replyingTo = ref<number | null>(null);
const replyingToReplyId = ref<number | null>(null);
const showRepliesMap = reactive<Record<number, boolean>>({});
const loadingReplyMap = reactive<Record<number, boolean>>({}); // 子评论加载状态
const replyPageMap = reactive<Record<number, number>>({}); // 已加载的页码
const noMoreRepliesMap = reactive<Record<number, boolean>>({}); // 是否已加载完所有子评论
const expandedCommentMap = reactive<Record<number, boolean>>({}); // 主评论展开状态
const expandedReplyMap = reactive<Record<number, boolean>>({}); // 子评论展开状态

// 删除确认弹框状态
const deleteDialog = ref(false);
const deleting = ref(false);
const deleteTarget = ref<{ type: 'comment' | 'reply'; commentId: number; replyId?: number } | null>(
  null,
);

// 内容溢出检测
const {
  overflowMap: overflowCommentMap,
  observe: observeComment,
  reset: resetCommentOverflow,
} = useContentOverflow();
const {
  overflowMap: overflowReplyMap,
  observe: observeReply,
  reset: resetReplyOverflow,
} = useContentOverflow();
const sortType = ref<'hot' | 'new'>('hot');
const showSortMenu = ref(false);
const current = ref(1);
const loading = ref(false);
const submitting = ref(false);
const sortBarRef = ref<HTMLElement | null>(null);
//每页默认最大条数
const pageSize = 10;

// 计算总页数
const totalPages = computed(() => Math.ceil(count.value / pageSize));

// 当前用户头像
const currentUserAvatar = computed(() => userInfoStore.userInfo.avatar ?? '');

// 主评论表情
const {
  showEmoji,
  emojiDirection,
  emojiTriggerRef,
  toggleEmoji,
  closeEmoji,
  registerClickOutside,
  unregisterClickOutside,
} = useEmoji();

// 保存的光标位置（打开表情框时记录）
const savedCursorPos = ref<{ start: number; end: number } | null>(null);

// 保存光标位置（focus/keyup/mouseup 时调用）
const saveCursor = () => {
  if (!commentTextareaRef.value) return;
  const textarea = commentTextareaRef.value;
  savedCursorPos.value = {
    start: textarea.selectionStart ?? commentContent.value.length,
    end: textarea.selectionEnd ?? commentContent.value.length,
  };
};

// 打开表情框时保存光标位置
const handleToggleEmoji = (event?: MouseEvent) => {
  if (!showEmoji.value && commentTextareaRef.value) {
    saveCursor();
  }
  toggleEmoji(event);
};

// 实际高度计算逻辑（不带节流）
const updateTextareaHeight = () => {
  if (!commentTextareaRef.value) return;
  const textarea = commentTextareaRef.value;
  // 兜底：如果初始高度还未获取，先获取一次
  if (!initialTextareaHeight.value) {
    initialTextareaHeight.value = textarea.offsetHeight;
  }
  // 从 CSS 读取 max-height
  const maxHeight = parseInt(getComputedStyle(textarea).maxHeight) || 200;
  textarea.style.height = 'auto';
  void getComputedStyle(textarea).height; // 强制同步布局计算
  const scrollHeight = textarea.scrollHeight;
  if (scrollHeight > initialTextareaHeight.value) {
    textarea.style.height = Math.min(scrollHeight, maxHeight) + 'px';
    textarea.style.overflowY = scrollHeight > maxHeight ? 'auto' : 'hidden';
  } else {
    textarea.style.height = initialTextareaHeight.value + 'px';
    textarea.style.overflowY = 'hidden';
  }
};

// 调整评论输入框高度（rAF 节流，用于 @input）
let textareaRafId = 0;
const handleTextareaInput = () => {
  if (textareaRafId) cancelAnimationFrame(textareaRafId);
  textareaRafId = requestAnimationFrame(updateTextareaHeight);
};

// 点赞
const like = async (item: Comment | Reply) => {
  const isLiked = await likeStore.toggleLike(LikeTypeEnum.COMMENT, item.id);
  if (isLiked !== null) {
    item.likeCount += isLiked ? 1 : -1;
  }
};

// 判断是否已点赞
const isLike = (id: number): boolean => {
  return likeStore.isLiked(LikeTypeEnum.COMMENT, id);
};

// 判断是否是自己的评论
const isSelf = (userId: string): boolean => {
  return userInfoStore.userInfo.userId === userId;
};

// 删除主评论
const handleDeleteComment = (commentId: number) => {
  deleteTarget.value = { type: 'comment', commentId };
  deleteDialog.value = true;
};

// 删除子评论
const handleDeleteReply = (parentId: number, replyId: number) => {
  deleteTarget.value = { type: 'reply', commentId: parentId, replyId };
  deleteDialog.value = true;
};

// 确认删除
const confirmDelete = async () => {
  if (!deleteTarget.value || deleting.value) return;

  deleting.value = true;
  try {
    const { type, commentId, replyId } = deleteTarget.value;
    const targetId = type === 'reply' ? replyId! : commentId;

    await deleteComment(targetId);

    if (type === 'comment') {
      // 从列表中移除主评论
      const index = commentList.value.findIndex((c) => c.id === commentId);
      if (index !== -1) {
        commentList.value.splice(index, 1);
        count.value--;
      }
    } else {
      // 从回复列表中移除子评论
      const comment = commentList.value.find((c) => c.id === commentId);
      if (comment && comment.replyList) {
        const index = comment.replyList.findIndex((r) => r.id === replyId);
        if (index !== -1) {
          comment.replyList.splice(index, 1);
          comment.replyCount--;
          // 如果正在回复被删的这条，自动取消回复
          if (replyingToReplyId.value === replyId) {
            cancelReply();
          }
          // 如果子评论全部删除，自动隐藏回复列表
          if (comment.replyCount === 0) {
            showRepliesMap[commentId] = false;
          }
        }
      }
    }

    snackbarStore.success('删除成功');
    deleteDialog.value = false;
    deleteTarget.value = null;
  } catch (error) {
    snackbarStore.error(getErrorMessage(error, '删除失败'));
  } finally {
    deleting.value = false;
  }
};

// 提交评论
const insertComment = async () => {
  if (!userInfoStore.checkLogin('评论')) return;
  if (!commentContent.value.trim()) {
    snackbarStore.error('评论内容不能为空');
    return;
  }
  if (submitting.value) return;

  submitting.value = true;
  try {
    const params = {
      targetType: props.type,
      targetId: Number(props.typeId),
      content: commentContent.value,
    };

    const res = await addComment(params);

    if (res.data.review === ReviewStatusEnum.PENDING) {
      snackbarStore.success('评论成功，等待审核');
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
      };
      commentList.value.unshift(newComment);
      count.value++;
      snackbarStore.success('评论成功');
    }

    // 清空评论内容并重置高度
    commentContent.value = '';
    if (commentTextareaRef.value) {
      commentTextareaRef.value.style.height = initialTextareaHeight.value + 'px';
      commentTextareaRef.value.style.overflowY = 'hidden';
    }
    closeEmoji();
  } catch (error) {
    snackbarStore.error(getErrorMessage(error, '评论提交失败'));
  } finally {
    submitting.value = false;
  }
};

// 添加表情（插入到保存的光标位置）
const addEmoji = (key: string) => {
  const textarea = commentTextareaRef.value;
  if (!textarea) {
    commentContent.value += key;
    return;
  }
  // 使用保存的光标位置，如果没有则使用当前位置或末尾
  const start =
    savedCursorPos.value?.start ?? textarea.selectionStart ?? commentContent.value.length;
  const end = savedCursorPos.value?.end ?? textarea.selectionEnd ?? commentContent.value.length;
  const text = commentContent.value;
  commentContent.value = text.slice(0, start) + key + text.slice(end);
  // 更新保存的光标位置（表情后面），以便连续插入
  const newPos = start + key.length;
  savedCursorPos.value = { start: newPos, end: newPos };
  // 恢复光标位置
  nextTick(() => {
    textarea.setSelectionRange(newPos, newPos);
    textarea.focus();
    updateTextareaHeight(); // 直接调用，不走 rAF 节流
  });
};

// 回复评论
const replyComment = (commentId: number) => {
  if (!userInfoStore.checkLogin('回复')) return;
  if (replyingTo.value === commentId && replyingToReplyId.value === null) {
    cancelReply();
  } else {
    replyingTo.value = commentId;
    replyingToReplyId.value = null;
    replyContent.value = '';
  }
};

// 回复子评论
const replyToReply = (commentId: number, reply: Reply) => {
  if (!userInfoStore.checkLogin('回复')) return;
  replyingTo.value = commentId;
  replyingToReplyId.value = reply.id;
  replyContent.value = '';
};

// 取消回复
const cancelReply = () => {
  replyingTo.value = null;
  replyingToReplyId.value = null;
  replyContent.value = '';
};

// 提交回复
const submitReply = async (item: Comment) => {
  if (!userInfoStore.checkLogin('回复')) return;
  if (!replyContent.value.trim()) {
    snackbarStore.error('回复内容不能为空');
    return;
  }
  if (submitting.value) return;

  submitting.value = true;
  try {
    // 获取被回复的用户信息（如果是回复子评论）
    const replyToUser = replyingToReplyId.value
      ? item.replyList?.find((r) => r.id === replyingToReplyId.value)
      : null;

    const params = {
      targetType: props.type,
      targetId: Number(props.typeId),
      content: replyContent.value,
      parentId: item.id, // 回复目标评论
      replyUserId: replyToUser?.userId, // 只有回复子评论时才有值
    };

    const res = await addComment(params);

    if (res.data.review === ReviewStatusEnum.PENDING) {
      snackbarStore.success('回复成功，等待审核');
    } else {
      // 不需要审核，构建新回复对象
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
      };

      const index = commentList.value.findIndex((c) => c.id === item.id);
      const comment = commentList.value[index];
      if (comment) {
        comment.replyCount++;
        showRepliesMap[comment.id] = true;

        // 检查是否已加载过子评论（>=1 就表示加载过）
        const hasLoadedReplies = !!replyPageMap[comment.id];

        // 只有原本就有子评论（replyCount > 1，因为已经+1了）且未加载过时，才需要调用接口
        if (!hasLoadedReplies && comment.replyCount > 1) {
          // 未加载过，先调用接口初始化子评论列表
          loadingReplyMap[comment.id] = true;
          try {
            const replyRes = await getReplyList({
              parentId: comment.id,
              current: 1,
              sortType: sortType.value,
            });
            comment.replyList = replyRes.data || [];
            replyPageMap[comment.id] = 1;
          } finally {
            loadingReplyMap[comment.id] = false;
          }
        } else if (!comment.replyList) {
          // 第一条子评论，初始化空数组
          comment.replyList = [];
        }

        // 添加新回复（先删除可能存在的重复项，再添加到正确位置）
        const existingIndex = comment.replyList!.findIndex((r) => r.id === newReply.id);
        if (existingIndex !== -1) {
          comment.replyList!.splice(existingIndex, 1);
        }
        // 清除“没有更多”标记，因为新增了回复
        delete noMoreRepliesMap[comment.id];
        if (sortType.value === 'new') {
          // 最新排序：添加到顶部
          comment.replyList!.unshift(newReply);
        } else {
          // 最热排序：添加到末尾
          comment.replyList!.push(newReply);
        }
      }

      snackbarStore.success('回复成功');
    }

    // 清空回复内容
    cancelReply();
  } catch (error) {
    console.error('提交回复失败:', error);
    snackbarStore.error(getErrorMessage(error, '回复提交失败'));
  } finally {
    submitting.value = false;
  }
};

// 切换回复列表显示
const toggleReplies = async (commentId: number) => {
  // 如果已展开，则收起
  if (showRepliesMap[commentId]) {
    showRepliesMap[commentId] = false;
    return;
  }

  // 展开并加载第1页数据
  showRepliesMap[commentId] = true;

  const comment = commentList.value.find((c) => c.id === commentId);
  if (!comment) return;

  // 如果已加载过，不重复加载
  if (replyPageMap[commentId]) return;

  // 加载第1页
  if (loadingReplyMap[commentId]) return;
  loadingReplyMap[commentId] = true;

  try {
    const res = await getReplyList({
      parentId: commentId,
      current: 1,
      sortType: sortType.value,
    });
    comment.replyList = res.data || [];
    replyPageMap[commentId] = 1; // 记录已加载第1页
  } finally {
    loadingReplyMap[commentId] = false;
  }
};

// 展示更多子评论
const showMoreReplies = async (commentId: number) => {
  if (loadingReplyMap[commentId]) return;
  loadingReplyMap[commentId] = true;

  try {
    const comment = commentList.value.find((c) => c.id === commentId);
    if (!comment || !comment.replyList) return;

    // 下一页
    const nextPage = (replyPageMap[commentId] || 1) + 1;

    const res = await getReplyList({
      parentId: commentId,
      current: nextPage,
      sortType: sortType.value,
    });

    const responseData = res.data || [];
    // 如果返回数据为空，标记已加载完毕
    if (responseData.length === 0) {
      noMoreRepliesMap[commentId] = true;
      return;
    }

    // 获取已存在的ID集合
    const existingIds = new Set(comment.replyList.map((r) => r.id));
    // 过滤去重后追加
    const newReplies = responseData.filter((r) => !existingIds.has(r.id));
    comment.replyList.push(...newReplies);
    replyPageMap[commentId] = nextPage; // 更新已加载页码
  } finally {
    loadingReplyMap[commentId] = false;
  }
};

// 隐藏子评论列表
const hideReplies = (commentId: number) => {
  showRepliesMap[commentId] = false;
};

// 是否还有更多子评论
const hasMoreReplies = (commentId: number, replyList: Reply[] | undefined) => {
  if (!replyList) return false;
  // 已标记加载完毕
  if (noMoreRepliesMap[commentId]) return false;
  const comment = commentList.value.find((c) => c.id === commentId);
  if (!comment) return false;
  return replyList.length < comment.replyCount;
};

// 重置所有展开状态
const resetRepliesState = () => {
  Object.keys(showRepliesMap).forEach((key) => delete showRepliesMap[Number(key)]);
  Object.keys(replyPageMap).forEach((key) => delete replyPageMap[Number(key)]);
  Object.keys(noMoreRepliesMap).forEach((key) => delete noMoreRepliesMap[Number(key)]);
  Object.keys(expandedCommentMap).forEach((key) => delete expandedCommentMap[Number(key)]);
  Object.keys(expandedReplyMap).forEach((key) => delete expandedReplyMap[Number(key)]);
  resetCommentOverflow();
  resetReplyOverflow();
  cancelReply();
};

// 设置主评论内容ref
const setCommentContentRef = (id: number, el: HTMLElement | null) => {
  observeComment(id, el);
};

// 设置子评论内容ref
const setReplyContentRef = (id: number, el: HTMLElement | null) => {
  observeReply(id, el);
};

// 切换主评论展开状态
const toggleExpandComment = (id: number) => {
  expandedCommentMap[id] = !expandedCommentMap[id];
};

// 切换子评论展开状态
const toggleExpandReply = (id: number) => {
  expandedReplyMap[id] = !expandedReplyMap[id];
};

// 加载评论列表
const listComments = async () => {
  // 对于需要typeId的类型，如果typeId为空或不是数字，则不发起请求
  if (requiresTypeId(props.type) && (!props.typeId || isNaN(Number(props.typeId)))) {
    commentList.value = [];
    count.value = 0;
    return;
  }

  if (loading.value) return;
  loading.value = true;
  // 重置展开状态
  resetRepliesState();
  try {
    const res = await getComments({
      type: props.type,
      typeId: props.typeId ? Number(props.typeId) : undefined,
      current: current.value,
      sortType: sortType.value,
    });
    commentList.value = res.data.list;
    count.value = res.data.total;
  } finally {
    loading.value = false;
  }
};

// 切换分页
const handlePageChange = () => {
  // 滚动到排序栏位置
  if (sortBarRef.value) {
    sortBarRef.value.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
  listComments();
};

// 选择排序方式
const selectSort = (type: 'hot' | 'new') => {
  if (sortType.value === type) {
    showSortMenu.value = false;
    return;
  }
  sortType.value = type;
  showSortMenu.value = false;
  current.value = 1;
  listComments();
};

// 点击外部关闭下拉菜单
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  if (!target.closest('.lc-sort-dropdown-wrapper')) {
    showSortMenu.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  registerClickOutside();
  listComments();
  // 获取初始高度
  nextTick(() => {
    if (commentTextareaRef.value) {
      initialTextareaHeight.value = commentTextareaRef.value.offsetHeight;
    }
  });
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
  unregisterClickOutside();
  if (textareaRafId) cancelAnimationFrame(textareaRafId);
});
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
  min-height: 54px;
  max-height: 200px;
  padding: 16px;
  border: none;
  outline: none;
  resize: none;
  font-size: 14px;
  line-height: 22px;
  color: #262626;
  box-sizing: border-box;
  background: transparent;
  overflow-y: hidden;
  word-break: break-all;
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
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.lc-nickname:hover {
  color: #5cb85c;
}

.lc-self-tag {
  padding: 0 6px;
  font-size: 11px;
  color: #2db55d;
  background: rgba(45, 181, 93, 0.1);
  border-radius: 4px;
  font-weight: 500;
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
.lc-content-wrapper {
  position: relative;
}

.lc-content-wrapper.expanded {
  display: flex;
  flex-direction: column;
}

.lc-content {
  font-size: 14px;
  line-height: 1.8;
  color: #262626;
  margin: 12px 0;
  word-break: break-all;
}

.lc-content.collapsed {
  max-height: 162px;
  overflow: hidden;
}

.lc-content-fade {
  position: absolute;
  bottom: 32px;
  left: 0;
  right: 0;
  height: 60px;
  background: linear-gradient(transparent, #fff);
  pointer-events: none;
}

.lc-reply-item .lc-content-fade {
  background: linear-gradient(transparent, #f5f5f5);
}

.lc-expand-btn {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
  font-size: 14px;
  color: #8c8c8c;
  cursor: pointer;
  padding: 4px 0;
}

.lc-expand-btn:hover {
  color: #262626;
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

/* 删除按钮样式 */
.delete-btn:hover {
  color: #f56c6c;
}

.delete-btn:hover :deep(.v-icon) {
  color: #f56c6c !important;
  transform: scale(1.2);
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

/* 骨架屏加载 */
.lc-reply-skeleton {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.lc-skeleton-item {
  display: flex;
  padding: 16px;
  background: hsl(0, 0%, 0%, 0.04);
  border-radius: 8px;
}

.lc-skeleton-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  flex-shrink: 0;
  margin-right: 12px;
}

.lc-skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.lc-skeleton-line {
  height: 14px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
}

.lc-skeleton-line.short {
  width: 80px;
}

.lc-skeleton-line.tiny {
  width: 120px;
  height: 12px;
}

.lc-skeleton-line.medium {
  width: 60%;
}

.lc-skeleton-actions {
  display: flex;
  gap: 24px;
  margin-top: 4px;
}

.lc-skeleton-action {
  width: 50px;
  height: 14px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* 空状态 */
.lc-empty {
  padding: 80px 0;
  text-align: center;
  color: #8c8c8c;
  font-size: 14px;
}
</style>

<!-- 夜间模式样式 -->
<style>
.v-theme--dark .lc-comment-container {
  background: transparent;
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

.v-theme--dark .lc-content-fade {
  background: linear-gradient(transparent, #2a2a2a);
}

.v-theme--dark .lc-reply-item .lc-content-fade {
  background: linear-gradient(transparent, #373737);
}

.v-theme--dark .lc-expand-btn {
  color: var(--color-text-tertiary);
}

.v-theme--dark .lc-expand-btn:hover {
  color: var(--color-text-primary);
}

/* 操作栏 */
.v-theme--dark .lc-action-row {
  color: var(--color-text-tertiary);
}

.v-theme--dark .lc-action-item:hover {
  color: var(--color-text-primary);
}

/* 回复列表 */
.v-theme--dark .lc-reply-item {
  background: rgba(255, 255, 255, 0.06);
}

/* 骨架屏夜间模式 */
.v-theme--dark .lc-skeleton-item {
  background: rgba(255, 255, 255, 0.06);
}

.v-theme--dark .lc-skeleton-avatar,
.v-theme--dark .lc-skeleton-line,
.v-theme--dark .lc-skeleton-action {
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0.1) 25%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.1) 75%
  );
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
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

/* 空状态 */
.v-theme--dark .lc-empty {
  color: var(--color-text-tertiary);
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
    0 0 1px 0 rgba(0, 0, 0, 0.1),
    0 0.5px 5px 0 rgba(0, 0, 0, 0.1) !important;
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
    0 0 1px 0 rgba(0, 0, 0, 0.3),
    0 0.5px 5px 0 rgba(0, 0, 0, 0.3) !important;
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
