import { ref } from 'vue';
import QRCode from 'qrcode';
import { useSnackbarStore } from '@/stores/modules/snackbar';
import useWebsiteConfigStore from '@/stores/modules/websiteConfig';
import type { TalkItem } from '@/apis/talk/types';

export type { TalkItem };

// 已注销用户默认昵称
const DEACTIVATED_NICKNAME = '该用户已注销';

// 判断用户是否已注销
export function isUserDeactivated(item: TalkItem): boolean {
  return !item.nickname || !item.avatar;
}

// 获取用户头像
export function getUserAvatar(item: TalkItem): string {
  return item.avatar || useWebsiteConfigStore().config.defaultUserAvatar;
}

// 获取用户昵称
export function getUserNickname(item: TalkItem): string {
  return item.nickname || DEACTIVATED_NICKNAME;
}

/**
 * 分享功能组合式API
 */
export function useShare() {
  // 分享弹窗
  const shareDialogVisible = ref(false);
  const shareUrl = ref('');
  const shareTitle = ref('');

  // 微信二维码弹窗
  const weixinQrcodeVisible = ref(false);
  const weixinQrcodeUrl = ref('');

  // QQ二维码弹窗
  const qqQrcodeVisible = ref(false);
  const qqQrcodeUrl = ref('');

  // 打开分享弹窗
  function openShareDialog(url: string, title: string) {
    shareUrl.value = url;
    shareTitle.value = title;
    shareDialogVisible.value = true;
  }

  // 复制链接
  function copyLink() {
    navigator.clipboard.writeText(shareUrl.value).then(() => {
      useSnackbarStore().success('链接已复制到剪贴板');
      shareDialogVisible.value = false;
    });
  }

  // 分享到微博
  function shareToWeibo() {
    const url = `https://service.weibo.com/share/share.php?url=${encodeURIComponent(shareUrl.value)}&title=${encodeURIComponent(shareTitle.value)}`;
    window.open(url, '_blank');
    shareDialogVisible.value = false;
  }

  // 分享到微信
  async function shareToWeixin() {
    weixinQrcodeUrl.value = '';
    shareDialogVisible.value = false;
    // 等待分享弹窗关闭动画完成后再打开二维码弹窗，避免闪烁
    setTimeout(async () => {
      weixinQrcodeVisible.value = true;
      try {
        weixinQrcodeUrl.value = await QRCode.toDataURL(shareUrl.value, {
          width: 180,
          margin: 2,
          color: { dark: '#000000', light: '#ffffff' },
        });
      } catch (err) {
        console.error('生成二维码失败', err);
      }
    }, 300);
  }

  // 分享到QQ
  async function shareToQQ() {
    qqQrcodeUrl.value = '';
    shareDialogVisible.value = false;
    // 等待分享弹窗关闭动画完成后再打开二维码弹窗，避免闪烁
    setTimeout(async () => {
      qqQrcodeVisible.value = true;
      try {
        qqQrcodeUrl.value = await QRCode.toDataURL(shareUrl.value, {
          width: 180,
          margin: 2,
          color: { dark: '#000000', light: '#ffffff' },
        });
      } catch (err) {
        console.error('生成二维码失败', err);
      }
    }, 300);
  }

  return {
    // 状态
    shareDialogVisible,
    shareUrl,
    shareTitle,
    weixinQrcodeVisible,
    weixinQrcodeUrl,
    qqQrcodeVisible,
    qqQrcodeUrl,
    // 方法
    openShareDialog,
    copyLink,
    shareToWeibo,
    shareToWeixin,
    shareToQQ,
  };
}
