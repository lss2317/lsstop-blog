<template>
  <div class="upc-trigger" @mouseenter="handleMouseEnter" @mouseleave="handleMouseLeave">
    <slot />
    <Teleport to="body">
      <Transition :name="cardPosition === 'top' ? 'upc-slide-up' : 'upc-slide-down'">
        <div
          v-if="visible"
          ref="cardRef"
          class="upc-card"
          :style="cardStyle"
          @mouseenter="handleCardEnter"
          @mouseleave="handleCardLeave"
        >
          <!-- 加载骨架屏 -->
          <div v-if="loading" class="upc-skeleton">
            <div class="upc-skeleton-header">
              <div class="upc-skeleton-avatar"></div>
              <div class="upc-skeleton-info">
                <div class="upc-skeleton-line short"></div>
                <div class="upc-skeleton-line tiny"></div>
              </div>
            </div>
            <div class="upc-skeleton-stats">
              <div class="upc-skeleton-stat" v-for="n in 2" :key="n"></div>
            </div>
          </div>

          <!-- 卡片内容 -->
          <template v-else-if="userProfile">
            <div class="upc-header">
              <img class="upc-avatar" :src="userProfile.avatar" alt="头像" />
              <div class="upc-info">
                <div class="upc-name-row">
                  <span class="upc-nickname" @click="goToProfile">{{ userProfile.nickname }}</span>
                  <div class="upc-region">
                    <v-icon size="12">mdi-web</v-icon>
                    <span>{{ userProfile.ipRegion }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 个人简介 -->
            <div class="upc-intro" v-if="userProfile.intro">{{ userProfile.intro }}</div>
            <div class="upc-intro text-muted" v-else>这个人很懒，什么都没写~</div>

            <!-- 统计数据 -->
            <div class="upc-stats">
              <div class="upc-stat-item">
                <div class="upc-stat-label">
                  <v-icon size="14">mdi-comment-text-outline</v-icon>
                  <span>评论</span>
                </div>
                <span class="upc-stat-value">{{ userProfile.commentCount }}</span>
              </div>
              <div class="upc-stat-item">
                <div class="upc-stat-label">
                  <v-icon size="14">mdi-thumb-up-outline</v-icon>
                  <span>获赞</span>
                </div>
                <span class="upc-stat-value">{{ userProfile.likeCount }}</span>
              </div>
            </div>

            <!-- 查看主页按钮 -->
            <button class="upc-profile-btn" @click="goToProfile">查看主页</button>
          </template>

          <!-- 用户不存在 -->
          <div v-else-if="notFound" class="upc-not-found">
            <v-icon size="48" class="upc-not-found-icon">mdi-account-off-outline</v-icon>
            <span>用户不存在</span>
          </div>

          <!-- 加载失败 -->
          <div v-else class="upc-error">加载失败</div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue';
import { getUserPublicProfile, type UserPublicProfile } from '@/apis/user';
import { useNavigate } from '@/composables/useNavigate';

const props = defineProps<{
  userId: string;
}>();

const { navigateTo } = useNavigate();

// 状态
const visible = ref(false);
const loading = ref(false);
const notFound = ref(false);
const userProfile = ref<UserPublicProfile | null>(null);
const cardRef = ref<HTMLElement | null>(null);
const triggerRect = ref<DOMRect | null>(null);

// 延迟控制
let showTimer: number | null = null;
let hideTimer: number | null = null;
const SHOW_DELAY = 300;
const HIDE_DELAY = 150;

// 缓存已加载的用户信息
const profileCache = new Map<string, UserPublicProfile>();

// 卡片位置
const cardPosition = ref<'bottom' | 'top'>('bottom');
const CARD_HEIGHT = 280; // 卡片预估高度

// 卡片位置样式
const cardStyle = computed(() => {
  if (!triggerRect.value) return {};
  const rect = triggerRect.value;
  const left = `${rect.left + window.scrollX}px`;

  if (cardPosition.value === 'top') {
    return {
      bottom: `${window.innerHeight - rect.top - window.scrollY + 4}px`,
      left,
    };
  }
  return {
    top: `${rect.bottom + window.scrollY + 4}px`,
    left,
  };
});

// 清除定时器
const clearTimers = () => {
  if (showTimer) {
    clearTimeout(showTimer);
    showTimer = null;
  }
  if (hideTimer) {
    clearTimeout(hideTimer);
    hideTimer = null;
  }
};

// 鼠标进入触发区域
const handleMouseEnter = async (e: MouseEvent) => {
  clearTimers();
  const target = e.currentTarget as HTMLElement;
  triggerRect.value = target.getBoundingClientRect();

  showTimer = window.setTimeout(async () => {
    // 检测下方空间是否足够
    const rect = triggerRect.value!;
    const spaceBelow = window.innerHeight - rect.bottom;
    cardPosition.value = spaceBelow < CARD_HEIGHT ? 'top' : 'bottom';

    visible.value = true;

    // 检查缓存
    if (profileCache.has(props.userId)) {
      userProfile.value = profileCache.get(props.userId)!;
      return;
    }

    // 加载数据
    loading.value = true;
    notFound.value = false;
    try {
      const res = await getUserPublicProfile(props.userId, false);
      if (res.data) {
        userProfile.value = res.data;
        profileCache.set(props.userId, res.data);
      } else {
        notFound.value = true;
      }
    } catch {
      notFound.value = true;
    } finally {
      loading.value = false;
    }
  }, SHOW_DELAY);
};

// 鼠标离开触发区域
const handleMouseLeave = () => {
  clearTimers();
  hideTimer = window.setTimeout(() => {
    visible.value = false;
  }, HIDE_DELAY);
};

// 鼠标进入卡片
const handleCardEnter = () => {
  clearTimers();
};

// 鼠标离开卡片
const handleCardLeave = () => {
  clearTimers();
  hideTimer = window.setTimeout(() => {
    visible.value = false;
  }, HIDE_DELAY);
};

// 跳转用户主页
const goToProfile = () => {
  visible.value = false;
  navigateTo(`/user/${props.userId}`);
};

onUnmounted(() => {
  clearTimers();
});
</script>

<style scoped>
.upc-trigger {
  display: inline-block;
  align-self: flex-start;
}

.upc-card {
  position: absolute;
  z-index: 9999;
  width: 280px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  box-shadow:
    0 4px 20px rgba(0, 0, 0, 0.12),
    0 0 1px rgba(0, 0, 0, 0.08);
}

/* 头部 */
.upc-header {
  display: flex;
  gap: 12px;
}

.upc-avatar {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}

.upc-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.upc-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.upc-nickname {
  font-size: 15px;
  font-weight: 600;
  color: #262626;
  cursor: pointer;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.upc-nickname:hover {
  color: #007aff;
}

.upc-region {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  color: #8c8c8c;
  flex-shrink: 0;
}

/* 个人简介 */
.upc-intro {
  margin-top: 12px;
  font-size: 13px;
  color: #595959;
  line-height: 1.5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.upc-intro.text-muted {
  color: #bfbfbf;
}

/* 统计数据 */
.upc-stats {
  display: flex;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid #f0f0f0;
}

.upc-stat-item {
  flex: 1;
  text-align: center;
}

.upc-stat-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 12px;
  color: #8c8c8c;
}

.upc-stat-value {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-top: 2px;
}

/* 查看主页按钮 */
.upc-profile-btn {
  width: 100%;
  margin-top: 14px;
  padding: 8px 0;
  background: #2db55d;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.upc-profile-btn:hover {
  background: #26a452;
}

/* 骨架屏 */
.upc-skeleton-header {
  display: flex;
  gap: 12px;
}

.upc-skeleton-avatar {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: upc-shimmer 1.5s infinite;
}

.upc-skeleton-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.upc-skeleton-line {
  height: 14px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: upc-shimmer 1.5s infinite;
}

.upc-skeleton-line.short {
  width: 80px;
}

.upc-skeleton-line.tiny {
  width: 60px;
  height: 12px;
}

.upc-skeleton-stats {
  display: flex;
  gap: 16px;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid #f0f0f0;
}

.upc-skeleton-stat {
  flex: 1;
  height: 40px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: upc-shimmer 1.5s infinite;
}

@keyframes upc-shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* 加载失败 */
.upc-error {
  text-align: center;
  color: #8c8c8c;
  font-size: 14px;
  padding: 20px 0;
}

/* 用户不存在 */
.upc-not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 24px 0;
  color: #8c8c8c;
  font-size: 14px;
}

.upc-not-found-icon {
  color: #d9d9d9;
}

/* 过渡动画 - 从下方滑入 */
.upc-slide-down-enter-active,
.upc-slide-down-leave-active {
  transition:
    opacity 0.2s ease,
    transform 0.2s ease;
}

.upc-slide-down-enter-from,
.upc-slide-down-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* 过渡动画 - 从上方滑入 */
.upc-slide-up-enter-active,
.upc-slide-up-leave-active {
  transition:
    opacity 0.2s ease,
    transform 0.2s ease;
}

.upc-slide-up-enter-from,
.upc-slide-up-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
</style>

<!-- 夜间模式 -->
<style>
.v-theme--dark .upc-card {
  background: #2a2a2a;
  box-shadow:
    0 4px 20px rgba(0, 0, 0, 0.4),
    0 0 1px rgba(255, 255, 255, 0.1);
}

.v-theme--dark .upc-nickname {
  color: #e6e6e6;
}

.v-theme--dark .upc-nickname:hover {
  color: #007aff;
}

.v-theme--dark .upc-region {
  color: rgba(255, 255, 255, 0.5);
}

.v-theme--dark .upc-intro {
  color: rgba(255, 255, 255, 0.7);
}

.v-theme--dark .upc-intro.text-muted {
  color: rgba(255, 255, 255, 0.35);
}

.v-theme--dark .upc-stats {
  border-top-color: rgba(255, 255, 255, 0.1);
}

.v-theme--dark .upc-stat-label {
  color: rgba(255, 255, 255, 0.5);
}

.v-theme--dark .upc-stat-value {
  color: #e6e6e6;
}

.v-theme--dark .upc-skeleton-avatar,
.v-theme--dark .upc-skeleton-line,
.v-theme--dark .upc-skeleton-stat {
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0.08) 25%,
    rgba(255, 255, 255, 0.12) 50%,
    rgba(255, 255, 255, 0.08) 75%
  );
  background-size: 200% 100%;
}

.v-theme--dark .upc-skeleton-stats {
  border-top-color: rgba(255, 255, 255, 0.1);
}

.v-theme--dark .upc-error {
  color: rgba(255, 255, 255, 0.5);
}

.v-theme--dark .upc-not-found {
  color: rgba(255, 255, 255, 0.5);
}

.v-theme--dark .upc-not-found-icon {
  color: rgba(255, 255, 255, 0.2);
}
</style>
