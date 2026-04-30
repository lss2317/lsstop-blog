<template>
  <div class="message-page">
    <!-- banner -->
    <div class="message-banner" :style="cover">
      <!-- 弹幕输入框 -->
      <div class="message-container">
        <h1 class="message-title">留言板</h1>
        <div
          ref="inputWrapperRef"
          class="animate__animated animate__fadeInUp message-input-wrapper"
          :class="{ 'is-active': isActive }"
        >
          <input
            v-model="messageContent"
            @focus="onFocus"
            @keyup.enter="addBlogMessage"
            placeholder="说点什么吧"
          />
          <button
            class="send-btn"
            :class="{ 'is-visible': isActive }"
            :disabled="submitting"
            :tabindex="isActive ? 0 : -1"
            @click="addBlogMessage"
          >
            <span class="send-btn-text">{{ submitting ? '发送中...' : '发送' }}</span>
          </button>
        </div>
      </div>
      <!-- 弹幕列表 -->
      <div class="barrage-container" v-if="isReady">
        <vue-danmaku
          ref="danmakuRef"
          v-model:danmus="barrageList"
          :speeds="100"
          :channels="0"
          :loop="false"
          :randomChannel="true"
          useSlot
          style="width: 100%; height: 100%"
        >
          <template v-slot:dm="{ danmu }">
            <span class="barrage-items">
              <img
                :src="danmu.avatar"
                width="30"
                height="30"
                style="border-radius: 50%"
                alt="用户头像"
              />
              <span class="ml-2">{{ danmu.nickname }} :</span>
              <span class="ml-2">{{ danmu.messageContent }}</span>
            </span>
          </template>
        </vue-danmaku>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import VueDanmaku from 'vue3-danmaku';
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue';
import { storeToRefs } from 'pinia';
import { listMessage, addMessage, type Message, type AddMessageParams } from '@/apis/message';
import { ReviewStatusEnum } from '@/constants/reviewStatus';
import useUserInfoStore from '@/stores/modules/userInfo';
import { useSnackbarStore } from '@/stores/modules/snackbar';
import usePageInfoStore from '@/stores/modules/pageInfo.ts';

const userInfoStore = useUserInfoStore();
const snackbarStore = useSnackbarStore();
const pageInfoStore = usePageInfoStore();
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore);

// 状态
const focused = ref(false);
const submitting = ref(false);
const messageContent = ref('');
const inputWrapperRef = ref<HTMLElement | null>(null);
const danmakuRef = ref<InstanceType<typeof VueDanmaku> | null>(null);
const isReady = ref(false);
const barrageList = ref<Message[]>([]);

// 输入框聚焦或有内容时，保持激活状态（按钮始终显示）
const isActive = computed(() => focused.value || messageContent.value.trim() !== '');

function onFocus() {
  focused.value = true;
}

// 发送留言
function addBlogMessage() {
  // 未登录时提示用户登录
  if (!userInfoStore.checkLogin('留言')) return;

  if (messageContent.value.trim() === '') {
    snackbarStore.error('留言不能为空');
    return;
  }

  if (submitting.value) return;

  submitting.value = true;
  const message: AddMessageParams = {
    avatar: userInfoStore.userInfo.avatar!,
    nickname: userInfoStore.userInfo.nickname!,
    messageContent: messageContent.value,
  };

  addMessage(message)
    .then((res) => {
      const data = res.data;
      // 审核状态：0-正常 1-待审核
      if (data.review === ReviewStatusEnum.NORMAL) {
        // 添加到弹幕列表
        danmakuRef.value?.add(data);
        snackbarStore.success('留言成功');
      } else {
        snackbarStore.success('留言成功，待审核后展示');
      }
      // 清空输入框
      messageContent.value = '';
      focused.value = false;
    })
    .catch((error) => {
      const msg = error.response?.data?.msg || '留言失败，请稍后重试';
      snackbarStore.error(msg);
    })
    .finally(() => {
      submitting.value = false;
    });
}

onMounted(() => {
  listMessage().then((res) => {
    barrageList.value = res.data;
  });
  nextTick(() => {
    isReady.value = true;
  });
  // 监听点击事件，点击输入框外部时隐藏发送按钮
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

// 点击外部收起按钮
function handleClickOutside(event: MouseEvent) {
  if (inputWrapperRef.value && !inputWrapperRef.value.contains(event.target as Node)) {
    focused.value = false;
  }
}
</script>

<style scoped>
.message-page {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.message-banner {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #49b1f5;
  animation: header-effect 1s;
  z-index: 1;
  overflow: hidden;
}

.message-title {
  color: #eee;
  animation: title-scale 1s;
}

.message-container {
  position: absolute;
  width: 360px;
  top: 35%;
  left: 0;
  right: 0;
  text-align: center;
  z-index: 5;
  margin: 0 auto;
  color: #fff;
}

.message-input-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 2.5rem;
  margin-top: 2rem;
  gap: 0.75rem;
}

.message-input-wrapper input {
  outline: none;
  width: 70%;
  border-radius: 20px;
  height: 100%;
  padding: 0 1.25rem;
  color: #eee;
  background: transparent;
  border: rgba(255, 255, 255, 0.6) 1px solid;
  transition:
    width 0.35s cubic-bezier(0.4, 0, 0.2, 1),
    border-color 0.3s ease,
    box-shadow 0.3s ease;
}

/* 激活态：输入框展开 + 发光边框 */
.message-input-wrapper.is-active input {
  width: 100%;
  border-color: rgba(255, 255, 255, 0.9);
  box-shadow: 0 0 12px rgba(255, 255, 255, 0.15);
}

.message-input-wrapper input::placeholder {
  color: rgba(238, 238, 238, 0.7);
  transition: opacity 0.3s ease;
}

.message-input-wrapper.is-active input::placeholder {
  opacity: 0.5;
}

/* 发送按钮 - 纯CSS折叠动画 */
.send-btn {
  outline: none;
  border-radius: 20px;
  height: 100%;
  max-width: 0;
  padding: 0;
  border: 1px solid transparent;
  flex-shrink: 0;
  cursor: pointer;
  color: #fff;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(6px);
  white-space: nowrap;
  overflow: hidden;
  opacity: 0;
  transform: scale(0.85);
  pointer-events: none;
  transition:
    max-width 0.4s cubic-bezier(0.4, 0, 0.2, 1),
    padding 0.4s cubic-bezier(0.4, 0, 0.2, 1),
    opacity 0.3s ease 0.05s,
    transform 0.35s cubic-bezier(0.4, 0, 0.2, 1),
    border-color 0.3s ease,
    background 0.25s ease,
    box-shadow 0.25s ease;
}

.send-btn.is-visible {
  max-width: 100px;
  padding: 0 1.25rem;
  opacity: 1;
  transform: scale(1);
  border-color: rgba(255, 255, 255, 0.8);
  pointer-events: auto;
}

.send-btn-text {
  display: inline-block;
  white-space: nowrap;
}

.send-btn.is-visible:hover {
  background: rgba(255, 255, 255, 0.28);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.15);
  transform: scale(1.04);
}

.send-btn.is-visible:active {
  transform: scale(0.96);
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: scale(1);
}

.barrage-container {
  position: absolute;
  top: 60px;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  overflow: hidden;
}

.barrage-items {
  background: rgb(0, 0, 0, 0.7);
  border-radius: 100px;
  color: #fff;
  padding: 5px 10px 5px 5px;
  align-items: center;
  display: flex;
}
</style>
