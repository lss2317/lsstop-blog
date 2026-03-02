import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

/** 弹窗类型 */
export type DialogType = 'login' | 'codeLogin' | 'register' | 'resetPassword' | null;

/**
 * 登录相关弹窗状态管理
 */
export const useLoginStore = defineStore('login', () => {
  /** 当前显示的弹窗类型 */
  const currentDialog = ref<DialogType>(null);

  /** 弹窗是否显示（独立状态，避免关闭时动画冲突） */
  const dialogVisible = ref(false);

  /** 密码登录弹窗 */
  const loginDialog = computed(() => currentDialog.value === 'login');
  /** 邮箱验证码登录弹窗 */
  const codeLoginDialog = computed(() => currentDialog.value === 'codeLogin');
  /** 注册弹窗 */
  const registerDialog = computed(() => currentDialog.value === 'register');
  /** 重置密码弹窗 */
  const resetPasswordDialog = computed(() => currentDialog.value === 'resetPassword');

  /** 关闭所有弹窗 */
  const closeAllDialogs = () => {
    dialogVisible.value = false;
  };

  /** v-dialog 关闭动画结束后重置内部状态 */
  const onDialogClosed = () => {
    currentDialog.value = null;
  };

  /** 打开密码登录弹窗 */
  const openLoginDialog = () => {
    currentDialog.value = 'login';
    dialogVisible.value = true;
  };

  /** 关闭密码登录弹窗 */
  const closeLoginDialog = () => {
    currentDialog.value = null;
  };

  /** 打开邮箱验证码登录弹窗 */
  const openCodeLoginDialog = () => {
    currentDialog.value = 'codeLogin';
    dialogVisible.value = true;
  };

  /** 关闭邮箱验证码登录弹窗 */
  const closeCodeLoginDialog = () => {
    currentDialog.value = null;
  };

  /** 打开注册弹窗 */
  const openRegisterDialog = () => {
    currentDialog.value = 'register';
    dialogVisible.value = true;
  };

  /** 关闭注册弹窗 */
  const closeRegisterDialog = () => {
    currentDialog.value = null;
  };

  /** 打开重置密码弹窗 */
  const openResetPasswordDialog = () => {
    currentDialog.value = 'resetPassword';
    dialogVisible.value = true;
  };

  /** 关闭重置密码弹窗 */
  const closeResetPasswordDialog = () => {
    currentDialog.value = null;
  };

  return {
    currentDialog,
    dialogVisible,
    loginDialog,
    codeLoginDialog,
    registerDialog,
    resetPasswordDialog,
    openLoginDialog,
    closeLoginDialog,
    openCodeLoginDialog,
    closeCodeLoginDialog,
    openRegisterDialog,
    closeRegisterDialog,
    openResetPasswordDialog,
    closeResetPasswordDialog,
    closeAllDialogs,
    onDialogClosed,
  };
});
