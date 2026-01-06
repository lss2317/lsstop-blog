<template>
  <div>
    <!-- banner -->
    <div class="about-banner banner" :style="cover">
      <h1 class="banner-title">关于我</h1>
    </div>
    <!-- 关于我内容 -->
    <v-card class="blog-container">
      <!-- 博主头像 -->
      <div class="my-wrapper">
        <v-avatar size="110" @click="showAvatar = true" class="avatar-clickable">
          <v-img class="author-avatar" :src="avatar" width="110" height="110" cover />
        </v-avatar>
      </div>
      <!-- 头像放大弹窗 -->
      <v-dialog v-model="showAvatar" max-width="90vw" content-class="lightbox-dialog">
        <div class="lightbox" @click.self="showAvatar = false">
          <img :src="avatar" alt="头像预览" @click.stop />
          <v-btn icon class="lightbox-close" size="small" @click="showAvatar = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </div>
      </v-dialog>
      <!-- 介绍 -->
      <div class="about-content markdown-body" v-html="aboutContent" />
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { storeToRefs } from 'pinia'
import usePageInfoStore from '@/stores/modules/pageInfo'
import useWebsiteConfigStore from '@/stores/modules/websiteConfig'
import { markdownToHtml } from '@/utils/markdown'

const pageInfoStore = usePageInfoStore()
const websiteConfigStore = useWebsiteConfigStore()
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore)
const { config } = storeToRefs(websiteConfigStore)

const avatar = computed(() => config.value.websiteAvatar)
const showAvatar = ref(false)
const aboutContent = computed(() => {
  return config.value.about ? markdownToHtml(config.value.about) : ''
})
</script>

<style scoped>
.about-banner {
  background: #49b1f5 no-repeat center center;
}

.about-content {
  word-break: break-word;
  line-height: 1.8;
}

.my-wrapper {
  text-align: center;
  margin-bottom: 24px;
}

.author-avatar {
  transition: all 0.3s;
}

.avatar-clickable {
  cursor: pointer !important;
}

.avatar-clickable :deep(*) {
  cursor: pointer !important;
}

.avatar-clickable:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.avatar-preview {
  border-radius: 8px;
}

/* 灯箱样式 */
:global(.lightbox-dialog) {
  background: transparent !important;
  box-shadow: none !important;
  overflow: visible !important;
}

.lightbox {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  min-height: 200px;
}

.lightbox img {
  max-width: 90vw;
  max-height: 85vh;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
}

.lightbox-close {
  position: absolute;
  top: -40px;
  right: 0;
  color: #fff !important;
  background: rgba(0, 0, 0, 0.5) !important;
}

.lightbox-close:hover {
  background: rgba(0, 0, 0, 0.7) !important;
}
</style>

<style lang="scss">
pre.hljs {
  padding: 12px 2px 12px 40px !important;
  border-radius: 5px !important;
  position: relative;
  font-size: 14px !important;
  line-height: 22px !important;
  overflow: hidden !important;

  code {
    display: block !important;
    margin: 0 10px !important;
    overflow-x: auto !important;

    &::-webkit-scrollbar {
      z-index: 11;
      width: 6px;
    }

    &::-webkit-scrollbar:horizontal {
      height: 6px;
    }

    &::-webkit-scrollbar-thumb {
      border-radius: 5px;
      width: 6px;
      background: #666;
    }

    &::-webkit-scrollbar-corner,
    &::-webkit-scrollbar-track {
      background: #1e1e1e;
    }

    &::-webkit-scrollbar-track-piece {
      background: #1e1e1e;
      width: 6px;
    }
  }

  .line-numbers-rows {
    position: absolute;
    pointer-events: none;
    top: 12px;
    bottom: 12px;
    left: 0;
    font-size: 100%;
    width: 40px;
    text-align: center;
    letter-spacing: -1px;
    border-right: 1px solid rgba(0, 0, 0, 0.66);
    user-select: none;
    counter-reset: linenumber;

    span {
      pointer-events: none;
      display: block;
      counter-increment: linenumber;

      &:before {
        content: counter(linenumber);
        color: #999;
        display: block;
        text-align: center;
      }
    }
  }

  b.name {
    position: absolute;
    top: 7px;
    right: 12px;
    z-index: 1;
    color: #999;
    pointer-events: none;
  }
}
</style>
