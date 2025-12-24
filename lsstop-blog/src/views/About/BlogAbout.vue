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
        <v-avatar size="110">
          <v-img class="author-avatar" :src="avatar" width="110" height="110" cover />
        </v-avatar>
      </div>
      <!-- 介绍 -->
      <div class="about-content markdown-body" v-html="aboutContent" />
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import Clipboard from 'clipboard'

const cover = ref(
  'background: url(https://blog-1307541812.cos.ap-shanghai.myqcloud.com/37e6f80a-a325-4afc-a564-00e163e1b473.jpg) center center / cover no-repeat rgb(73, 177, 245)',
)
const avatar = ref(
  'https://blog-1307541812.cos.ap-shanghai.myqcloud.com/47495c0d-55eb-474b-93a1-5ce59bcc7dee.jpg',
)
const aboutContent = ref(
  '> 欢迎来到我的小站呀，很高兴遇见你！🤝\n\n**🍀个人简介**\n\n一个想进步的普通人\n\n渴望进步，不断学习中...\n',
)

const generateCodeIndex = (): string => {
  let d = new Date().getTime()
  if (window.performance && typeof window.performance.now === 'function') {
    d += performance.now()
  }
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (d + Math.random() * 16) % 16 | 0
    d = Math.floor(d / 16)
    return (c === 'x' ? r : (r & 0x3) | 0x8).toString(16)
  })
}

const escapeHtml = (str: string): string => {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

const markdownToHtml = (content: string): string => {
  const md = new MarkdownIt({
    html: true,
    linkify: true,
    typographer: true,
    highlight: (str: string, lang: string): string => {
      const codeIndex = generateCodeIndex()
      let html = `<button class="copy-btn iconfont iconfuzhi" type="button" data-clipboard-action="copy" data-clipboard-target="#copy${codeIndex}"></button>`
      const linesLength = str.split(/\n/).length - 1
      let linesNum = '<span aria-hidden="true" class="line-numbers-rows">'
      for (let index = 0; index < linesLength; index++) {
        linesNum += '<span></span>'
      }
      linesNum += '</span>'
      if (lang && hljs.getLanguage(lang)) {
        const preCode = hljs.highlight(str, { language: lang, ignoreIllegals: true }).value
        html += preCode
        if (linesLength) {
          html += '<b class="name">' + lang + '</b>'
        }
        return `<pre class="hljs"><code>${html}</code>${linesNum}</pre><textarea style="position: absolute;top: -9999px;left: -9999px;z-index: -9999;" id="copy${codeIndex}">${str.replace(/<\/textarea>/g, '&lt;/textarea>')}</textarea>`
      }
      return `<pre class="hljs"><code>${escapeHtml(str)}</code>${linesNum}</pre>`
    },
  })
  return md.render(content)
}

onMounted(() => {
  new Clipboard('.copy-btn')
  aboutContent.value = markdownToHtml(aboutContent.value)
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
}

.author-avatar {
  transition: all 0.5s;
}

.author-avatar:hover {
  transform: rotate(360deg);
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

  &:hover .copy-btn {
    display: flex;
    justify-content: center;
    align-items: center;
  }

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
    right: 45px;
    z-index: 1;
    color: #999;
    pointer-events: none;
  }

  .copy-btn {
    position: absolute;
    top: 6px;
    right: 6px;
    z-index: 1;
    color: #ccc;
    background-color: #525252;
    border-radius: 6px;
    display: none;
    font-size: 14px;
    width: 32px;
    height: 24px;
    outline: none;
  }
}
</style>
