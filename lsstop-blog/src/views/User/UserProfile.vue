<template>
  <div class="profile-page">
    <div class="profile-container">
      <!-- 加载状态骨架屏 -->
      <template v-if="loading">
        <div class="profile-sidebar">
          <div class="user-card skeleton-card">
            <div class="user-header">
              <v-skeleton-loader type="avatar" class="skeleton-avatar" />
              <div class="user-info">
                <v-skeleton-loader type="text" width="100" class="skeleton-name" />
                <v-skeleton-loader type="text" width="140" class="skeleton-id" />
              </div>
            </div>
            <v-skeleton-loader type="text" width="200" class="skeleton-intro" />
            <v-skeleton-loader type="text" width="120" class="skeleton-stats" />
            <v-skeleton-loader type="button" class="skeleton-btn" />
            <div class="skeleton-meta">
              <v-skeleton-loader type="text" width="140" />
            </div>
          </div>
        </div>
        <div class="profile-main">
          <div class="content-card skeleton-card">
            <v-skeleton-loader type="text" width="80" class="skeleton-title" />
            <div class="skeleton-content-area">
              <v-skeleton-loader type="text" width="100%" />
              <v-skeleton-loader type="text" width="80%" />
              <v-skeleton-loader type="text" width="60%" />
            </div>
          </div>
        </div>
      </template>

      <!-- 用户信息 -->
      <template v-else-if="profileData">
        <!-- 左侧用户信息卡片 -->
        <div class="profile-sidebar">
          <div class="user-card fade-in-item" style="--delay: 0s">
            <!-- 头像和昵称 -->
            <div class="user-header">
              <div class="avatar-wrapper" @click="isOwner && triggerAvatarUpload()">
                <img
                  :src="profileData.avatar"
                  class="user-avatar"
                  :class="{ editable: isOwner }"
                  alt="头像"
                />
                <div v-if="isOwner" class="avatar-hover-tip">更新头像</div>
                <input
                  v-if="isOwner"
                  ref="avatarInput"
                  type="file"
                  accept="image/*"
                  class="avatar-file-input"
                  @change="handleAvatarChange"
                />
              </div>
              <div class="user-info">
                <h1 class="user-name">{{ profileData.nickname }}</h1>
                <p class="user-id">{{ profileData.userId }}</p>
              </div>
            </div>

            <!-- 简介 -->
            <p v-if="profileData.intro" class="user-intro">{{ profileData.intro }}</p>
            <p v-else class="user-intro text-muted">这个人很懒，什么都没写~</p>

            <!-- 统计 -->
            <div class="user-stats">
              <span class="stat">{{ profileData.commentCount || 0 }} 评论</span>
              <span class="stat-divider">|</span>
              <span class="stat">{{ profileData.likeCount || 0 }} 获赞</span>
            </div>

            <!-- 编辑按钮 -->
            <a v-if="isOwner" class="edit-profile-btn" @click="openEditDialog"> 编辑个人资料 </a>

            <!-- 其他信息 -->
            <div class="user-meta">
              <div class="meta-item">
                <v-icon size="18">mdi-calendar-outline</v-icon>
                <span>{{ dateFormat.date(profileData.createTime) }} 加入</span>
              </div>
              <div v-if="profileData.website" class="meta-item">
                <v-icon size="18">mdi-link-variant</v-icon>
                <a :href="profileData.website" target="_blank" rel="noopener noreferrer" class="meta-link">
                  {{ formatWebsite(profileData.website) }}
                  <v-icon size="14" class="external-icon">mdi-arrow-top-right</v-icon>
                </a>
              </div>
            </div>
          </div>

          <!-- 账号设置卡片（仅自己可见） -->
          <div v-if="isOwner" class="settings-card fade-in-item" style="--delay: 0.1s">
            <h3 class="card-title">账号设置</h3>
            <div class="settings-list">
              <div class="setting-item">
                <v-icon size="18">mdi-email-outline</v-icon>
                <span class="setting-label">邮箱</span>
                <span class="setting-value">{{ ownerProfile?.email || '未绑定' }}</span>
                <button class="setting-action-btn" @click="openEmailDialog">修改</button>
              </div>
              <div class="setting-item">
                <v-icon size="18">mdi-lock-outline</v-icon>
                <span class="setting-label">密码</span>
                <span class="setting-value">******</span>
                <button class="setting-action-btn" @click="openPasswordDialog">修改</button>
              </div>
            </div>
          </div>

          <!-- 社交账号卡片（仅自己可见） -->
          <div v-if="isOwner" class="settings-card fade-in-item" style="--delay: 0.2s">
            <h3 class="card-title">绑定第三方账号</h3>
            <div class="settings-list">
              <div class="setting-item">
                <span class="iconfont iconqq" style="color: #00aaee; font-size: 18px" />
                <span class="setting-label">QQ</span>
                <span class="setting-value" :class="{ bound: ownerProfile?.qqBound }">
                  {{ ownerProfile?.qqBound ? '已绑定' : '未绑定' }}
                </span>
                <button
                  class="setting-action-btn"
                  :class="{ 'setting-action-btn--danger': ownerProfile?.qqBound }"
                  @click="handleQQBind"
                >
                  {{ ownerProfile?.qqBound ? '解绑' : '绑定' }}
                </button>
              </div>
              <div class="setting-item">
                <span class="iconfont iconweibo" style="color: #e05244; font-size: 18px" />
                <span class="setting-label">微博</span>
                <span class="setting-value" :class="{ bound: ownerProfile?.weiboBound }">
                  {{ ownerProfile?.weiboBound ? '已绑定' : '未绑定' }}
                </span>
                <button
                  class="setting-action-btn"
                  :class="{ 'setting-action-btn--danger': ownerProfile?.weiboBound }"
                  @click="handleWeiboBind"
                >
                  {{ ownerProfile?.weiboBound ? '解绑' : '绑定' }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧内容区 -->
        <div class="profile-main">
          <div class="content-card fade-in-item" style="--delay: 0.15s">
            <h3 class="card-title">最近动态</h3>
            <div class="empty-content">
              <v-icon size="48" color="grey-lighten-1">mdi-history</v-icon>
              <p>暂无动态</p>
            </div>
          </div>
        </div>
      </template>

      <!-- 用户不存在 -->
      <template v-else>
        <div class="profile-sidebar">
          <div class="user-card">
            <div class="empty-wrapper">
              <v-icon size="64" color="grey">mdi-account-off</v-icon>
              <p class="empty-text">用户不存在</p>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 编辑个人信息对话框 -->
    <v-dialog v-model="editDialog" max-width="448" :fullscreen="isMobile">
      <div class="edit-dialog">
        <div class="edit-dialog-header">
          <div class="edit-dialog-title">
            <v-icon size="16" color="#3b82f6">mdi-information</v-icon>
            <span>编辑基本信息</span>
          </div>
          <button class="edit-dialog-close" @click="editDialog = false">
            <v-icon size="20">mdi-close</v-icon>
          </button>
        </div>
        <div class="edit-dialog-body">
          <p class="edit-dialog-desc">将展示在个人主页。</p>
          <div class="edit-form-item">
            <input
              v-model="editForm.nickname"
              class="edit-input"
              placeholder="请输入昵称"
              maxlength="30"
            />
          </div>
          <div class="edit-form-item">
            <input
              v-model="editForm.website"
              class="edit-input"
              placeholder="请输入个人网站URL"
              maxlength="200"
              @keydown.space.prevent
            />
          </div>
          <div class="edit-form-item">
            <textarea
              v-model="editForm.intro"
              class="edit-textarea"
              placeholder="介绍一下你自己（兴趣、经历等）"
              maxlength="100"
              rows="4"
            ></textarea>
            <div class="edit-textarea-count">{{ editForm.intro?.length || 0 }}/100</div>
          </div>
        </div>
        <div class="edit-dialog-footer">
          <button class="edit-btn edit-btn-cancel" @click="editDialog = false">取消</button>
          <button class="edit-btn edit-btn-save" :disabled="editLoading" @click="saveUserInfo">
            {{ editLoading ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </v-dialog>

    <!-- 修改邮箱对话框 -->
    <v-dialog v-model="emailDialog" max-width="448" :fullscreen="isMobile">
      <div class="edit-dialog">
        <div class="edit-dialog-header">
          <div class="edit-dialog-title">
            <v-icon size="16" color="#3b82f6">mdi-email-outline</v-icon>
            <span>修改邮箱</span>
          </div>
          <button class="edit-dialog-close" @click="emailDialog = false">
            <v-icon size="20">mdi-close</v-icon>
          </button>
        </div>
        <div class="edit-dialog-body">
          <div class="edit-form-item">
            <input
              v-model="emailForm.newEmail"
              class="edit-input"
              placeholder="请输入新邮箱"
              type="email"
              @keydown.space.prevent
            />
          </div>
          <div class="edit-form-item">
            <div class="edit-code-wrapper">
              <input
                v-model="emailForm.code"
                class="edit-input edit-code-input"
                placeholder="请输入验证码"
                maxlength="6"
                @keydown.space.prevent
              />
              <button
                class="edit-code-btn"
                :disabled="emailForm.countdown > 0"
                @click="sendEmailCode"
              >
                {{ emailForm.countdown > 0 ? `${emailForm.countdown}s` : '发送验证码' }}
              </button>
            </div>
          </div>
        </div>
        <div class="edit-dialog-footer">
          <button class="edit-btn edit-btn-cancel" @click="emailDialog = false">取消</button>
          <button class="edit-btn edit-btn-save" :disabled="emailLoading" @click="saveEmail">
            {{ emailLoading ? '保存中...' : '确认' }}
          </button>
        </div>
      </div>
    </v-dialog>

    <!-- 修改密码对话框 -->
    <v-dialog v-model="passwordDialog" max-width="448" :fullscreen="isMobile">
      <div class="edit-dialog">
        <div class="edit-dialog-header">
          <div class="edit-dialog-title">
            <v-icon size="16" color="#3b82f6">mdi-lock-outline</v-icon>
            <span>修改密码</span>
          </div>
          <button class="edit-dialog-close" @click="passwordDialog = false">
            <v-icon size="20">mdi-close</v-icon>
          </button>
        </div>
        <div class="edit-dialog-body">
          <div class="edit-form-item">
            <div class="edit-password-wrapper">
              <input
                v-model="passwordForm.oldPassword"
                class="edit-input"
                placeholder="请输入旧密码"
                :type="passwordForm.showOld ? 'text' : 'password'"
                @keydown.space.prevent
              />
              <button class="edit-eye-btn" @click="passwordForm.showOld = !passwordForm.showOld">
                <v-icon size="18">{{ passwordForm.showOld ? 'mdi-eye' : 'mdi-eye-off' }}</v-icon>
              </button>
            </div>
          </div>
          <div class="edit-form-item">
            <div class="edit-password-wrapper">
              <input
                v-model="passwordForm.newPassword"
                class="edit-input"
                placeholder="请输入新密码（6-20位）"
                :type="passwordForm.showNew ? 'text' : 'password'"
                maxlength="20"
                @keydown.space.prevent
              />
              <button class="edit-eye-btn" @click="passwordForm.showNew = !passwordForm.showNew">
                <v-icon size="18">{{ passwordForm.showNew ? 'mdi-eye' : 'mdi-eye-off' }}</v-icon>
              </button>
            </div>
          </div>
          <div class="edit-form-item">
            <div class="edit-password-wrapper">
              <input
                v-model="passwordForm.confirmPassword"
                class="edit-input"
                placeholder="请再次输入新密码"
                :type="passwordForm.showConfirm ? 'text' : 'password'"
                maxlength="20"
                @keydown.space.prevent
              />
              <button
                class="edit-eye-btn"
                @click="passwordForm.showConfirm = !passwordForm.showConfirm"
              >
                <v-icon size="18">{{
                  passwordForm.showConfirm ? 'mdi-eye' : 'mdi-eye-off'
                }}</v-icon>
              </button>
            </div>
          </div>
        </div>
        <div class="edit-dialog-footer">
          <button class="edit-btn edit-btn-cancel" @click="passwordDialog = false">取消</button>
          <button class="edit-btn edit-btn-save" :disabled="passwordLoading" @click="savePassword">
            {{ passwordLoading ? '保存中...' : '确认' }}
          </button>
        </div>
      </div>
    </v-dialog>

    <!-- 解绑确认对话框 -->
    <ConfirmDialog
      v-model="unbindDialog"
      :title="`解绑${unbindType === 'qq' ? 'QQ' : '微博'}`"
      :content="`确定要解绑${unbindType === 'qq' ? 'QQ' : '微博'}账号吗？解绑后将无法使用该账号登录。`"
      confirm-text="解绑"
      :loading="unbindLoading"
      @confirm="confirmUnbind"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import useUserInfoStore from '@/stores/modules/userInfo';
import { useSnackbarStore } from '@/stores/modules/snackbar';
import {
  getUserProfile,
  getUserPublicProfile,
  updateUserInfo,
  updateEmail,
  updatePassword,
  unbindSocial,
  sendUpdateEmailCode,
  uploadAvatar,
  type UserProfileInfo,
  type UserPublicProfile,
  type SocialType,
} from '@/apis/user';
import { isValidEmail } from '@/utils/validate';
import { getErrorMessage } from '@/utils/error';
import { formatWebsite } from '@/utils/format';
import { dateFormat } from '@/utils/date';
import ConfirmDialog from '@/components/Dialog/ConfirmDialog.vue';

const route = useRoute();
const userInfoStore = useUserInfoStore();
const snackbar = useSnackbarStore();
const { userInfo } = storeToRefs(userInfoStore);

// 加载状态
const loading = ref(true);

// 用户数据（自己的完整信息 或 他人的公开信息）
const profileData = ref<UserProfileInfo | UserPublicProfile | null>(null);

// 是否是自己的主页
const isOwner = computed(() => {
  return String(userInfo.value?.userId || '') === String(route.params.userId || '');
});

// 完整的用户信息（仅 isOwner 时使用，包含邮箱、绑定状态等敏感字段）
const ownerProfile = computed((): UserProfileInfo | null => {
  if (isOwner.value && profileData.value) {
    return profileData.value as UserProfileInfo;
  }
  return null;
});

// 响应式判断
const isMobile = ref(false);

const handleResize = () => {
  isMobile.value = window.innerWidth <= 960;
};

// 获取用户信息
let requestId = 0;

const fetchUserProfile = async () => {
  const currentId = ++requestId;
  const userId = route.params.userId as string;
  if (!userId) {
    loading.value = false;
    return;
  }

  // 判断是否是自己的主页
  const isSelf = String(userInfo.value?.userId || '') === String(userId);

  loading.value = true;
  try {
    // 自己调用完整信息接口，他人调用公开信息接口
    const res = isSelf ? await getUserProfile() : await getUserPublicProfile(userId);
    if (currentId !== requestId) return;
    profileData.value = res.data;
  } catch {
    if (currentId !== requestId) return;
    profileData.value = null;
  } finally {
    if (currentId === requestId) {
      loading.value = false;
    }
  }
};

// 监听路由变化（immediate 首次也触发，替代 onMounted 中的调用）
watch(
  () => route.params.userId,
  async () => {
    await fetchUserProfile();
  },
  { immediate: true },
);

onMounted(() => {
  handleResize();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  clearEmailTimer();
});

// 上传头像
const avatarInput = ref<HTMLInputElement | null>(null);

const triggerAvatarUpload = () => {
  avatarInput.value?.click();
};

const handleAvatarChange = async (event: Event) => {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;

  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    snackbar.error('请选择图片文件');
    return;
  }

  // 检查文件大小（限制 2MB）
  if (file.size > 2 * 1024 * 1024) {
    snackbar.error('图片大小不能超过 2MB');
    return;
  }

  try {
    const res = await uploadAvatar(file);
    snackbar.success('头像上传成功');
    // 刷新用户信息
    await fetchUserProfile();
    // 同步更新 store
    userInfoStore.setUserInfo({ avatar: res.data });
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  } finally {
    // 清空 input，允许重复选择同一文件
    input.value = '';
  }
};

// 编辑个人信息
const editDialog = ref(false);
const editLoading = ref(false);
const editForm = reactive({
  nickname: '',
  website: '',
  intro: '',
});

const openEditDialog = () => {
  if (profileData.value) {
    editForm.nickname = profileData.value.nickname || '';
    editForm.website = profileData.value.website || '';
    editForm.intro = profileData.value.intro || '';
  }
  editDialog.value = true;
};

const saveUserInfo = async () => {
  const nickname = editForm.nickname.trim();
  if (!nickname) {
    snackbar.info('请输入昵称');
    return;
  }

  editLoading.value = true;
  try {
    await updateUserInfo({
      nickname,
      website: editForm.website.trim() || undefined,
      intro: editForm.intro.trim() || undefined,
    });
    snackbar.success('保存成功');
    editDialog.value = false;
    // 刷新用户信息
    await fetchUserProfile();
    // 同步更新store中的用户信息
    userInfoStore.setUserInfo({
      nickname,
      website: editForm.website.trim() || null,
      intro: editForm.intro.trim() || null,
    });
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  } finally {
    editLoading.value = false;
  }
};

// 修改邮箱
const emailDialog = ref(false);
const emailLoading = ref(false);
const emailForm = reactive({
  newEmail: '',
  code: '',
  countdown: 0,
});
let emailTimer: number | null = null;

const clearEmailTimer = () => {
  if (emailTimer) {
    clearInterval(emailTimer);
    emailTimer = null;
  }
};

const openEmailDialog = () => {
  emailForm.newEmail = '';
  emailForm.code = '';
  emailDialog.value = true;
};

const sendEmailCode = async () => {
  const email = emailForm.newEmail.trim();
  if (!email) {
    snackbar.info('请输入新邮箱');
    return;
  }
  if (!isValidEmail(email)) {
    snackbar.info('邮箱格式不正确');
    return;
  }
  if (emailForm.countdown > 0) return;

  try {
    await sendUpdateEmailCode(email);
    snackbar.success('验证码已发送');
    // 开始倒计时
    clearEmailTimer();
    emailForm.countdown = 60;
    emailTimer = window.setInterval(() => {
      if (emailForm.countdown > 0) {
        emailForm.countdown--;
      } else {
        clearEmailTimer();
      }
    }, 1000);
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  }
};

const saveEmail = async () => {
  const newEmail = emailForm.newEmail.trim();
  const code = emailForm.code.trim();

  if (!newEmail) {
    snackbar.info('请输入新邮箱');
    return;
  }
  if (!isValidEmail(newEmail)) {
    snackbar.info('邮箱格式不正确');
    return;
  }
  if (!code) {
    snackbar.info('请输入验证码');
    return;
  }

  emailLoading.value = true;
  try {
    await updateEmail({ newEmail, code });
    snackbar.success('邮箱修改成功');
    emailDialog.value = false;
    await fetchUserProfile();
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  } finally {
    emailLoading.value = false;
  }
};

// 修改密码
const passwordDialog = ref(false);
const passwordLoading = ref(false);
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
  showOld: false,
  showNew: false,
  showConfirm: false,
});

const openPasswordDialog = () => {
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
  passwordForm.showOld = false;
  passwordForm.showNew = false;
  passwordForm.showConfirm = false;
  passwordDialog.value = true;
};

const savePassword = async () => {
  const oldPassword = passwordForm.oldPassword;
  const newPassword = passwordForm.newPassword.trim();
  const confirmPassword = passwordForm.confirmPassword.trim();

  if (!oldPassword) {
    snackbar.info('请输入旧密码');
    return;
  }
  if (!newPassword) {
    snackbar.info('请输入新密码');
    return;
  }
  if (newPassword.length < 6 || newPassword.length > 20) {
    snackbar.info('新密码长度为6-20位');
    return;
  }
  if (newPassword !== confirmPassword) {
    snackbar.info('两次密码输入不一致');
    return;
  }

  passwordLoading.value = true;
  try {
    await updatePassword({ oldPassword, newPassword });
    snackbar.success('密码修改成功');
    passwordDialog.value = false;
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  } finally {
    passwordLoading.value = false;
  }
};

// 社交账号绑定
const unbindDialog = ref(false);
const unbindLoading = ref(false);
const unbindType = ref<SocialType>('qq');

const handleQQBind = () => {
  if (ownerProfile.value?.qqBound) {
    // 已绑定，显示解绑确认
    unbindType.value = 'qq';
    unbindDialog.value = true;
  } else {
    // 未绑定，跳转绑定
    // TODO: 实现QQ绑定
    snackbar.info('QQ绑定功能开发中');
  }
};

const handleWeiboBind = () => {
  if (ownerProfile.value?.weiboBound) {
    // 已绑定，显示解绑确认
    unbindType.value = 'weibo';
    unbindDialog.value = true;
  } else {
    // 未绑定，跳转绑定
    // TODO: 实现微博绑定
    snackbar.info('微博绑定功能开发中');
  }
};

const confirmUnbind = async () => {
  unbindLoading.value = true;
  try {
    await unbindSocial(unbindType.value);
    snackbar.success('解绑成功');
    unbindDialog.value = false;
    await fetchUserProfile();
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  } finally {
    unbindLoading.value = false;
  }
};
</script>

<style scoped>
/* 页面布局 */
.profile-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 78px 20px 40px; /* 58px导航栏 + 20px间距 */
  display: flex;
  gap: 20px;
}

.profile-sidebar {
  width: 300px;
  flex-shrink: 0;
}

.profile-main {
  flex: 1;
  min-width: 0;
}

/* 通用卡片样式 */
.user-card,
.settings-card,
.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
}

/* 用户卡片 */
.user-header {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 16px;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
  width: 80px;
  height: 80px;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.user-avatar.editable {
  cursor: pointer;
}

.avatar-file-input {
  display: none;
}

.avatar-hover-tip {
  position: absolute;
  left: 50%;
  top: 100%;
  transform: translateX(-50%);
  margin-top: 8px;
  padding: 4px 10px;
  background: #fff;
  color: #262626;
  font-size: 12px;
  white-space: nowrap;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  opacity: 0;
  transition: opacity 0.2s;
  pointer-events: none;
  z-index: 10;
}

.avatar-wrapper:hover .avatar-hover-tip {
  opacity: 1;
}

.user-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 4px;
  line-height: 1.4;
  word-break: break-all;
}

.user-id {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.user-intro {
  word-break: break-all;
  text-align: justify;
  font-size: 0.875rem;
  font-weight: 500;
  line-height: 1.625;
  color: hsl(0, 0%, 45%);
  margin: 0 0 16px;
}

.user-intro.text-muted {
  color: #bfbfbf;
}

.user-stats {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #595959;
}

.stat-divider {
  color: #d9d9d9;
  margin: 0 8px;
}

.edit-profile-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 36px;
  margin-bottom: 16px;
  background: rgba(45, 181, 93, 0.08);
  color: #26a352 !important;
  font-size: 14px;
  font-weight: 500;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
  text-decoration: none;
}

.edit-profile-btn:hover {
  background: rgba(45, 181, 93, 0.12);
  color: #26a352 !important;
}

.user-meta {
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #8c8c8c;
  margin-bottom: 12px;
}

.meta-item:last-child {
  margin-bottom: 0;
}

.meta-item .v-icon {
  color: #bfbfbf;
}

.meta-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #2db55d;
  text-decoration: none;
  transition: color 0.2s;
}

.meta-link:hover {
  color: #1a8a42;
  text-decoration: underline;
}

.meta-link .external-icon {
  color: inherit;
}

/* 设置卡片 */
.card-title {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 12px;
}

.settings-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.setting-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.setting-item .v-icon,
.setting-item .iconfont {
  color: #8c8c8c;
  margin-right: 12px;
}

.setting-label {
  font-size: 14px;
  color: #595959;
  flex: 1;
}

.setting-value {
  font-size: 13px;
  color: #bfbfbf;
  margin-right: 12px;
}

.setting-value.bound {
  color: #52c41a;
}

.setting-action-btn {
  padding: 4px 12px;
  font-size: 13px;
  color: #8c8c8c;
  background: transparent;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.setting-action-btn:hover {
  color: #595959;
  background: rgba(0, 0, 0, 0.04);
}

.setting-action-btn--danger {
  color: #ff4d4f;
}

.setting-action-btn--danger:hover {
  color: #ff7875;
  background: rgba(255, 77, 79, 0.08);
}

/* 内容卡片 */
.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #bfbfbf;
}

.empty-content p {
  margin-top: 12px;
  font-size: 14px;
}

/* 加载和空状态 */
.loading-wrapper,
.empty-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
}

.empty-text {
  margin-top: 16px;
  color: #bfbfbf;
  font-size: 14px;
}

/* 骨架屏样式 */
.skeleton-card {
  animation: skeleton-fade-in 0.3s ease-out;
}

@keyframes skeleton-fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 内容淡入动画 */
.fade-in-item {
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

.skeleton-avatar {
  width: 80px !important;
  height: 80px !important;
  border-radius: 8px !important;
  flex-shrink: 0;
}

.skeleton-avatar :deep(.v-skeleton-loader__avatar) {
  width: 80px !important;
  height: 80px !important;
  border-radius: 8px !important;
}

.skeleton-name {
  margin-bottom: 4px;
}

.skeleton-id {
  margin-top: 4px;
}

.skeleton-intro {
  margin-top: 16px;
}

.skeleton-stats {
  margin-top: 12px;
}

.skeleton-btn {
  margin-top: 16px;
  height: 36px;
}

.skeleton-btn :deep(.v-skeleton-loader__button) {
  width: 100% !important;
  height: 36px !important;
  border-radius: 4px !important;
}

.skeleton-meta {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.skeleton-title {
  margin-bottom: 24px;
}

.skeleton-content-area {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 40px 0;
}

/* 编辑弹框样式 - 力扣风格 */
.edit-dialog {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
}

.edit-dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.edit-dialog-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}

.edit-dialog-close {
  background: none;
  border: none;
  cursor: pointer;
  opacity: 0.7;
  transition: opacity 0.2s;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-dialog-close:hover {
  opacity: 1;
}

.edit-dialog-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.edit-dialog-desc {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.edit-form-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.edit-input {
  width: 100%;
  height: 36px;
  padding: 0 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  color: #262626;
  background: transparent;
  outline: none;
  transition: border-color 0.2s;
}

.edit-input:focus {
  border-color: #3b82f6;
}

.edit-input::placeholder {
  color: #bfbfbf;
}

.edit-textarea {
  width: 100%;
  min-height: 100px;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  color: #262626;
  background: transparent;
  outline: none;
  resize: none;
  font-family: inherit;
  transition: border-color 0.2s;
}

.edit-textarea:focus {
  border-color: #3b82f6;
}

.edit-textarea::placeholder {
  color: #bfbfbf;
}

.edit-textarea-count {
  text-align: right;
  font-size: 14px;
  color: #8c8c8c;
}

.edit-code-wrapper {
  display: flex;
  gap: 8px;
}

.edit-code-input {
  flex: 1;
}

.edit-code-btn {
  flex-shrink: 0;
  min-width: 80px;
  height: 36px;
  padding: 0 10px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
  color: #475569;
  font-size: 13px;
  font-weight: 400;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.edit-code-btn:hover {
  border-color: #d1d5db;
  background: #f1f5f9;
  color: #374151;
}

.edit-code-btn:disabled {
  background: #f8fafc;
  border-color: #e5e7eb;
  color: #94a3b8;
  cursor: not-allowed;
}

.edit-password-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.edit-password-wrapper .edit-input {
  padding-right: 40px;
}

.edit-eye-btn {
  position: absolute;
  right: 8px;
  padding: 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: #bfbfbf;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-eye-btn:hover {
  color: #595959;
}

.edit-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 16px;
}

.edit-btn {
  height: 36px;
  padding: 0 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.edit-btn-cancel {
  background: #f5f5f5;
  color: #595959;
}

.edit-btn-cancel:hover {
  background: #e8e8e8;
}

.edit-btn-save {
  background: #3b82f6;
  color: #fff;
}

.edit-btn-save:hover {
  background: #2563eb;
}

.edit-btn-save:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 响应式 */
@media (max-width: 768px) {
  .profile-container {
    flex-direction: column;
    padding: 68px 12px 20px;
  }

  .profile-sidebar {
    width: 100%;
  }
}
</style>

<!-- 夜间模式样式（类名已足够特定，污染风险低） -->
<style>
.v-theme--dark .profile-page {
  background: #141414;
}

.v-theme--dark .user-card,
.v-theme--dark .settings-card,
.v-theme--dark .content-card {
  background: #1f1f1f;
}

.v-theme--dark .user-name {
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .user-id,
.v-theme--dark .meta-item {
  color: rgba(255, 255, 255, 0.45);
}

.v-theme--dark .user-intro {
  color: rgba(255, 255, 255, 0.65);
}

.v-theme--dark .user-intro.text-muted {
  color: rgba(255, 255, 255, 0.3);
}

.v-theme--dark .user-stats {
  color: rgba(255, 255, 255, 0.65);
}

.v-theme--dark .stat-divider {
  color: rgba(255, 255, 255, 0.2);
}

.v-theme--dark .user-meta {
  border-top-color: rgba(255, 255, 255, 0.1);
}

.v-theme--dark .card-title {
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .setting-label {
  color: rgba(255, 255, 255, 0.65);
}

.v-theme--dark .setting-value {
  color: rgba(255, 255, 255, 0.3);
}

.v-theme--dark .setting-value.bound {
  color: #52c41a;
}

.v-theme--dark .setting-action-btn {
  color: rgba(255, 255, 255, 0.45);
}

.v-theme--dark .setting-action-btn:hover {
  color: rgba(255, 255, 255, 0.65);
  background: rgba(255, 255, 255, 0.08);
}

.v-theme--dark .setting-action-btn--danger {
  color: #ff7875;
}

.v-theme--dark .setting-action-btn--danger:hover {
  color: #ffa39e;
  background: rgba(255, 120, 117, 0.12);
}

.v-theme--dark .edit-profile-btn {
  background: rgba(45, 181, 93, 0.15);
  color: #5ec269;
}

.v-theme--dark .edit-profile-btn:hover {
  background: rgba(45, 181, 93, 0.2);
}

/* 编辑弹框暗色模式 */
.v-theme--dark .edit-dialog {
  background: #262626;
  border: 1px solid #404040;
}

.v-theme--dark .edit-dialog-title {
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .edit-dialog-desc {
  color: rgba(255, 255, 255, 0.45);
}

.v-theme--dark .edit-input,
.v-theme--dark .edit-textarea {
  border-color: #404040;
  color: rgba(255, 255, 255, 0.9);
}

.v-theme--dark .edit-input:focus,
.v-theme--dark .edit-textarea:focus {
  border-color: #3b82f6;
}

.v-theme--dark .edit-input::placeholder,
.v-theme--dark .edit-textarea::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.v-theme--dark .edit-textarea-count {
  color: rgba(255, 255, 255, 0.45);
}

.v-theme--dark .edit-btn-cancel {
  background: #404040;
  color: rgba(255, 255, 255, 0.65);
}

.v-theme--dark .edit-btn-cancel:hover {
  background: #525252;
}

.v-theme--dark .edit-code-btn {
  border-color: #404040;
  background: #2a2a2a;
  color: rgba(255, 255, 255, 0.65);
}

.v-theme--dark .edit-code-btn:hover {
  border-color: #525252;
  background: #333333;
  color: rgba(255, 255, 255, 0.85);
}

.v-theme--dark .edit-code-btn:disabled {
  background: #2a2a2a;
  border-color: #404040;
  color: rgba(255, 255, 255, 0.3);
}

.v-theme--dark .edit-eye-btn {
  color: rgba(255, 255, 255, 0.3);
}

.v-theme--dark .edit-eye-btn:hover {
  color: rgba(255, 255, 255, 0.65);
}

.v-theme--dark .skeleton-meta {
  border-top-color: rgba(255, 255, 255, 0.1);
}

.v-theme--dark .avatar-hover-tip {
  background: #3a3a3a;
  color: rgba(255, 255, 255, 0.9);
  border-color: #525252;
}
</style>
