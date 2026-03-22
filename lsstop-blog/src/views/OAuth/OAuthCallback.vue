<template>
  <div class="oauth-callback">
    <v-progress-circular indeterminate color="primary" size="48" />
    <p class="mt-4">{{ message }}</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { qqLogin, weiboLogin } from '@/apis/auth';
import type { UserInfo } from '@/stores/modules/userInfo';
import useUserInfoStore from '@/stores/modules/userInfo';
import useLikeStore from '@/stores/modules/like';
import { tokenManager } from '@/utils/token';
import { useSnackbarStore } from '@/stores/modules/snackbar';
import { getErrorMessage } from '@/utils/error';

const route = useRoute();
const router = useRouter();
const userInfoStore = useUserInfoStore();
const likeStore = useLikeStore();
const snackbar = useSnackbarStore();

const message = ref('正在登录...');

// 跳转首页定时器
let redirectTimer: number | null = null;

const redirectHome = (delay = 2000) => {
  if (redirectTimer) {
    clearTimeout(redirectTimer);
  }
  redirectTimer = window.setTimeout(() => {
    router.push('/');
  }, delay);
};

onBeforeUnmount(() => {
  if (redirectTimer) clearTimeout(redirectTimer);
});

// QQ登录回调处理
const handleQQCallback = async () => {
  const code = route.query.code as string;

  if (!code) {
    message.value = '授权失败，未获取到授权码';
    redirectHome();
    return;
  }

  try {
    const res = await qqLogin({ code });
    handleLoginSuccess(res.data);
  } catch (error) {
    message.value = '登录失败';
    snackbar.error(getErrorMessage(error));
    redirectHome();
  }
};

// 微博登录回调处理
const handleWeiboCallback = async () => {
  const code = route.query.code as string;

  if (!code) {
    message.value = '授权失败，未获取到授权码';
    redirectHome();
    return;
  }

  try {
    const res = await weiboLogin({ code });
    handleLoginSuccess(res.data);
  } catch (error) {
    message.value = '登录失败';
    snackbar.error(getErrorMessage(error));
    redirectHome();
  }
};

// 登录成功处理
const handleLoginSuccess = (data: UserInfo) => {
  message.value = '登录成功，正在跳转...';
  userInfoStore.setUserInfo(data);
  if (data.accessToken && data.refreshToken) {
    tokenManager.setTokens(data.accessToken, data.refreshToken);
  }
  void likeStore.fetchUserLike();
  snackbar.success('登录成功');
  router.push('/');
};

onMounted(() => {
  const type = route.meta.oauthType as string;

  if (type === 'qq') {
    void handleQQCallback();
  } else if (type === 'weibo') {
    void handleWeiboCallback();
  } else {
    message.value = '未知的登录类型';
    redirectHome();
  }
});
</script>

<style scoped>
.oauth-callback {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: #f5f5f5;
}

.oauth-callback p {
  color: #666;
  font-size: 14px;
}
</style>
