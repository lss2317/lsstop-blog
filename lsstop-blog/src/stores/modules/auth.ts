import { defineStore } from 'pinia';
import { logout } from '@/apis/auth';
import { tokenManager } from '@/utils/token';
import useUserInfoStore from '@/stores/modules/userInfo';
import useLikeStore from '@/stores/modules/like';
import { useSnackbarStore } from '@/stores/modules/snackbar';

/**
 * 认证业务逻辑状态管理
 */
export const useAuthStore = defineStore('auth', () => {
  // 退出登录
  async function handleLogout() {
    const refreshToken = tokenManager.getRefreshToken();
    if (refreshToken) {
      await logout({ refreshToken });
    }
    tokenManager.clearTokens();
    useUserInfoStore().clearUserInfo();
    useLikeStore().clearAll();
    useSnackbarStore().success('退出成功');
  }

  return {
    handleLogout,
  };
});
