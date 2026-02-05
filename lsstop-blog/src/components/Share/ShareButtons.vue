<template>
  <div class="share-container">
    <div class="share-btn weibo" @click="handleShareToWeibo">
      <v-icon size="18" color="#e6162d">mdi-sina-weibo</v-icon>
    </div>
    <div class="share-btn wechat" @click="handleShareToWeixin">
      <v-icon size="18" color="#07c160">mdi-wechat</v-icon>
    </div>
    <div class="share-btn qq" @click="handleShareToQQ">
      <v-icon size="18" color="#12b7f5">mdi-qqchat</v-icon>
    </div>
  </div>

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
import { ref } from 'vue';
import QRCode from 'qrcode';

const props = defineProps<{
  url: string;
  title?: string;
}>();

// 二维码相关
const weixinQrcodeVisible = ref(false);
const weixinQrcodeUrl = ref('');
const qqQrcodeVisible = ref(false);
const qqQrcodeUrl = ref('');

// 分享到微博
const handleShareToWeibo = () => {
  const url = `https://service.weibo.com/share/share.php?url=${encodeURIComponent(props.url)}&title=${encodeURIComponent(props.title || '')}`;
  window.open(url, '_blank');
};

// 分享到微信
const handleShareToWeixin = async () => {
  weixinQrcodeUrl.value = '';
  weixinQrcodeVisible.value = true;
  try {
    weixinQrcodeUrl.value = await QRCode.toDataURL(props.url, {
      width: 180,
      margin: 2,
      color: { dark: '#000000', light: '#ffffff' },
    });
  } catch (err) {
    console.error('生成二维码失败', err);
  }
};

// 分享到QQ
const handleShareToQQ = async () => {
  qqQrcodeUrl.value = '';
  qqQrcodeVisible.value = true;
  try {
    qqQrcodeUrl.value = await QRCode.toDataURL(props.url, {
      width: 180,
      margin: 2,
      color: { dark: '#000000', light: '#ffffff' },
    });
  } catch (err) {
    console.error('生成二维码失败', err);
  }
};
</script>

<style scoped>
.share-container {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.share-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition:
    transform 0.3s,
    box-shadow 0.3s,
    background 0.3s;
  background: #f5f5f5;
}

.share-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.share-btn.weibo {
  background: rgba(230, 22, 45, 0.1);
}

.share-btn.weibo:hover {
  background: rgba(230, 22, 45, 0.2);
}

.share-btn.wechat {
  background: rgba(7, 193, 96, 0.1);
}

.share-btn.wechat:hover {
  background: rgba(7, 193, 96, 0.2);
}

.share-btn.qq {
  background: rgba(18, 183, 245, 0.1);
}

.share-btn.qq:hover {
  background: rgba(18, 183, 245, 0.2);
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
  animation: qrcodeIn 0.3s ease;
}

@keyframes qrcodeIn {
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
