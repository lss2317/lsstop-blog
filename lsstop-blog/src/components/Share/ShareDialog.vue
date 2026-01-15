<template>
  <!-- 分享弹窗 -->
  <v-dialog
    v-model="shareDialogVisible"
    max-width="320"
    transition="dialog-bottom-transition"
    scroll-strategy="none"
  >
    <v-card class="share-dialog">
      <v-card-title class="share-title">分享到</v-card-title>
      <v-card-text class="share-options">
        <div class="share-item" @click="copyLink">
          <v-icon size="32" color="#8a919f">mdi-link-variant</v-icon>
          <span>复制链接</span>
        </div>
        <div class="share-item" @click="shareToWeibo">
          <v-icon size="32" color="#e6162d">mdi-sina-weibo</v-icon>
          <span>微博</span>
        </div>
        <div class="share-item" @click="shareToWeixin">
          <v-icon size="32" color="#07c160">mdi-wechat</v-icon>
          <span>微信</span>
        </div>
        <div class="share-item" @click="shareToQQ">
          <v-icon size="32" color="#12b7f5">mdi-qqchat</v-icon>
          <span>QQ</span>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
  <!-- 微信二维码弹窗 -->
  <v-dialog
    v-model="weixinQrcodeVisible"
    max-width="300"
    transition="dialog-bottom-transition"
    scroll-strategy="none"
  >
    <v-card class="qrcode-dialog">
      <v-card-title class="qrcode-title">微信扫一扫：分享</v-card-title>
      <v-card-text class="qrcode-content">
        <div class="qrcode-wrapper">
          <v-progress-circular v-if="!weixinQrcodeUrl" indeterminate color="#07c160" size="40" />
          <img v-else :src="weixinQrcodeUrl" alt="微信分享二维码" class="qrcode-img" />
        </div>
        <p class="qrcode-tip">微信里点"发现"，扫一下<br />二维码即可在手机上打开。</p>
      </v-card-text>
    </v-card>
  </v-dialog>
  <!-- QQ二维码弹窗 -->
  <v-dialog
    v-model="qqQrcodeVisible"
    max-width="300"
    transition="dialog-bottom-transition"
    scroll-strategy="none"
  >
    <v-card class="qrcode-dialog">
      <v-card-title class="qrcode-title">QQ扫一扫：分享</v-card-title>
      <v-card-text class="qrcode-content">
        <div class="qrcode-wrapper">
          <v-progress-circular v-if="!qqQrcodeUrl" indeterminate color="#12b7f5" size="40" />
          <img v-else :src="qqQrcodeUrl" alt="QQ分享二维码" class="qrcode-img" />
        </div>
        <p class="qrcode-tip">打开手机QQ，扫一下<br />二维码即可在手机上打开。</p>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { useShare } from '@/utils/talk'

const {
  shareDialogVisible,
  weixinQrcodeVisible,
  weixinQrcodeUrl,
  qqQrcodeVisible,
  qqQrcodeUrl,
  openShareDialog,
  copyLink,
  shareToWeibo,
  shareToWeixin,
  shareToQQ,
} = useShare()

// 暴露 openShareDialog 方法供父组件调用
defineExpose({
  openShareDialog,
})
</script>

<style scoped>
/* 分享弹窗样式 */
.share-dialog {
  border-radius: 12px !important;
}

.share-title {
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  padding-bottom: 8px;
}

.share-options {
  display: flex;
  justify-content: space-around;
  padding: 16px 8px 24px;
}

.share-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 12px;
  border-radius: 8px;
  transition: background 0.2s;
}

.share-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.share-item span {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

/* 二维码弹窗样式 */
.qrcode-dialog {
  border-radius: 16px !important;
  overflow: hidden;
}

.qrcode-title {
  text-align: center;
  font-size: 15px;
  font-weight: 500;
  color: #333;
  padding: 20px 20px 8px;
}

.qrcode-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 24px 24px;
}

.qrcode-wrapper {
  width: 180px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qrcode-img {
  width: 180px;
  height: 180px;
  border: 1px solid #eee;
  border-radius: 8px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.qrcode-tip {
  margin-top: 16px;
  font-size: 12px;
  color: #999;
  text-align: center;
  line-height: 1.8;
}
</style>
