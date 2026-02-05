import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

/** 消息提示类型 */
type SnackbarType = 'success' | 'error' | 'warning' | 'info';

/**
 * 消息提示框状态管理
 */
export const useSnackbarStore = defineStore('snackbar', () => {
  /** 显示状态 */
  const show = ref(false);
  /** 提示文本 */
  const text = ref('');
  /** 提示类型 */
  const type = ref<SnackbarType>('info');
  /** 显示时长（毫秒） */
  const timeout = ref(2000);

  /** 根据类型计算对应的图标 */
  const icon = computed(() => {
    const icons: Record<SnackbarType, string> = {
      error: 'mdi-close-circle-outline',
      success: 'mdi-checkbox-marked-circle-outline',
      warning: 'mdi-alert-outline',
      info: 'mdi-information-outline',
    };
    return icons[type.value];
  });

  /**
   * 显示消息提示
   * @param message 提示文本
   * @param messageType 提示类型
   * @param duration 显示时长
   */
  function showMessage(message: string, messageType: SnackbarType = 'info', duration = 2000) {
    text.value = message;
    type.value = messageType;
    timeout.value = duration;
    show.value = true;
  }

  /** 显示成功提示 */
  function success(message: string, duration = 2000) {
    showMessage(message, 'success', duration);
  }

  /** 显示错误提示 */
  function error(message: string, duration = 2000) {
    showMessage(message, 'error', duration);
  }

  /** 显示警告提示 */
  function warning(message: string, duration = 2000) {
    showMessage(message, 'warning', duration);
  }

  /** 显示信息提示 */
  function info(message: string, duration = 2000) {
    showMessage(message, 'info', duration);
  }

  return {
    show,
    text,
    type,
    timeout,
    icon,
    showMessage,
    success,
    error,
    warning,
    info,
  };
});
