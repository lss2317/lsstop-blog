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
        >
          <input v-model="messageContent" @focus="show = true" placeholder="说点什么吧" />
          <transition
            enter-active-class="animate__animated animate__bounceInLeft"
            leave-active-class="animate__animated animate__bounceOutRight"
          >
            <button class="ml-3" @click="addBlogMessage" v-show="show">发送</button>
          </transition>
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
import VueDanmaku from 'vue3-danmaku'
import { nextTick, onMounted, onUnmounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { listMessage, addMessage } from '@/apis/message'
import useUserInfoStore from '@/stores/modules/userInfo'
import { useSnackbarStore } from '@/stores/modules/snackbar'
import usePageInfoStore from '@/stores/modules/pageInfo.ts'
import useWebsiteConfigStore from '@/stores/modules/websiteConfig'

interface Barrage {
  avatar: string
  nickname: string
  messageContent: string
  review: number
}

// 默认配置
const DEFAULT_NICKNAME = '游客'

const userInfoStore = useUserInfoStore()
const snackbarStore = useSnackbarStore()
const pageInfoStore = usePageInfoStore()
const websiteConfigStore = useWebsiteConfigStore()
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore)

// 状态
const show = ref(false)
const messageContent = ref('')
const inputWrapperRef = ref<HTMLElement | null>(null)
const danmakuRef = ref<InstanceType<typeof VueDanmaku> | null>(null)
const isReady = ref(false)
const barrageList = ref<Barrage[]>([])

// 发送留言
function addBlogMessage() {
  if (messageContent.value.trim() === '') {
    snackbarStore.error('留言不能为空')
    return
  }

  const message: Barrage = {
    avatar: userInfoStore.userInfo.avatar ?? websiteConfigStore.config.touristAvatar,
    nickname: userInfoStore.userInfo.nickname ?? DEFAULT_NICKNAME,
    messageContent: messageContent.value,
    review: websiteConfigStore.config.commentReview ?? 0,
  }

  addMessage(message)
    .then(() => {
      // 添加到弹幕列表
      danmakuRef.value?.add(message)
      // 清空输入框
      messageContent.value = ''
      show.value = false
      // 提示成功
      snackbarStore.success('留言成功')
    })
    .catch((error) => {
      const msg = error.response?.data?.msg || '留言失败，请稍后重试'
      snackbarStore.error(msg)
    })
}

onMounted(() => {
  listMessage().then((res) => {
    barrageList.value = res.data
  })
  nextTick(() => {
    isReady.value = true
  })
  // 监听点击事件，点击输入框外部时隐藏发送按钮
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// 点击外部隐藏发送按钮
function handleClickOutside(event: MouseEvent) {
  if (inputWrapperRef.value && !inputWrapperRef.value.contains(event.target as Node)) {
    show.value = false
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
  height: 2.5rem;
  margin-top: 2rem;
}

.message-input-wrapper input {
  outline: none;
  width: 70%;
  border-radius: 20px;
  height: 100%;
  padding: 0 1.25rem;
  color: #eee;
  border: #fff 1px solid;
}

.message-input-wrapper input::-webkit-input-placeholder {
  color: #eeee;
}

.message-input-wrapper button {
  outline: none;
  border-radius: 20px;
  height: 100%;
  padding: 0 1.25rem;
  border: #fff 1px solid;
  flex-shrink: 0;
  cursor: pointer;
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
