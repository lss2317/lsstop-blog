import { defineStore } from 'pinia';
import { logout } from '@/apis/auth';
import { tokenManager } from '@/utils/token';
import useUserInfoStore from '@/stores/modules/userInfo';
import useLikeStore from '@/stores/modules/like';
import useChatStore from '@/stores/modules/chat';
import { useSnackbarStore } from '@/stores/modules/snackbar';

/**
 * 清除认证状态（token、用户信息、点赞数据）
 * 可在 store 外部直接调用
 */
export function clearAuthState() {
  tokenManager.clearTokens();
  useUserInfoStore().clearUserInfo();
  useLikeStore().clearAll();
}

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
    // 关闭并清空聊天室
    const chatStore = useChatStore();
    chatStore.close();
    chatStore.clear();

    clearAuthState();
    useSnackbarStore().success('退出成功');
  }

  return {
    handleLogout,
  };
});
