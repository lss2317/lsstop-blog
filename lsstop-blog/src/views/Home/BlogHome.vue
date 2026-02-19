<template>
  <div>
    <!-- banner -->
    <div class="home-banner" :style="cover">
      <div class="banner-container">
        <h1 class="blog-title animate__animated animate__zoomIn">
          {{ config.siteName }}
        </h1>
        <div class="blog-intro">{{ typedOutput }}<span class="typed-cursor">|</span></div>
        <!-- 联系方式 -->
        <div class="blog-contact animate__animated animate__zoomIn">
          <a
            v-if="config.qqUrl"
            class="ml-5 mr-5 iconfont iconqq"
            style="color: #12b7f5"
            target="_blank"
            :href="config.qqUrl"
          />
          <a
            v-if="config.githubUrl"
            class="ml-5 mr-5 iconfont icongithub"
            style="color: #24292e"
            target="_blank"
            :href="config.githubUrl"
          />
          <a
            v-if="config.giteeUrl"
            class="ml-5 mr-5 iconfont icongitee-fill-round"
            style="color: #c71d23"
            target="_blank"
            :href="config.giteeUrl"
          />
        </div>
      </div>
      <!-- 向下滚动 -->
      <div class="scroll-down" @click="scrollDown">
        <v-icon color="#fff" class="scroll-down-effects"> mdi-chevron-down </v-icon>
      </div>
    </div>

    <!-- 主页内容 -->
    <v-row class="home-container">
      <v-col md="9" cols="12">
        <!-- 说说轮播 -->
        <v-card class="animate__animated animate__zoomIn" v-if="talkList.length > 0">
          <TalkSwiper :list="talkList" />
        </v-card>
      </v-col>

      <!-- 博主信息 -->
      <v-col md="3" cols="12" class="d-md-block d-none">
        <div class="blog-wrapper">
          <v-card class="animate__animated animate__zoomIn blog-card mt-5">
            <div class="author-wrapper">
              <!-- 博主头像 -->
              <v-avatar size="110" class="author-avatar" @click="previewAvatar">
                <v-img :src="config.siteAvatar" cover />
              </v-avatar>
              <div style="font-size: 1.375rem">
                {{ config.siteName }}
              </div>
              <div style="font-size: 0.875rem">
                {{ config.siteIntro }}
              </div>
            </div>
            <div class="card-info-social">
              <a
                v-if="config.qqUrl"
                class="social-icon iconfont iconqq"
                style="color: #12b7f5"
                target="_blank"
                :href="config.qqUrl"
              />
              <a
                v-if="config.githubUrl"
                class="social-icon iconfont icongithub"
                style="color: #24292e"
                target="_blank"
                :href="config.githubUrl"
              />
              <a
                v-if="config.giteeUrl"
                class="social-icon iconfont icongitee-fill-round"
                style="color: #c71d23"
                target="_blank"
                :href="config.giteeUrl"
              />
            </div>
          </v-card>

          <!-- 网站资讯 -->
          <v-card class="blog-card animate__animated animate__zoomIn mt-5">
            <div class="web-info-title">
              <v-icon size="18">mdi-chart-line</v-icon>
              网站资讯
            </div>
            <div class="web-info">
              <div style="padding: 4px 0 0">
                总访问量:<span class="float-right">{{ websiteConfigStore.viewCount }}</span>
              </div>
              <div style="padding: 4px 0 0">
                运行时间:<span class="float-right"><ScrollNumber :value="runTime" /></span>
              </div>
            </div>
          </v-card>
        </div>
      </v-col>
    </v-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import TalkSwiper from '@/components/Swiper/TalkSwiper.vue';
import { listTalk } from '@/apis/talk';
import { parseEmoji } from '@/utils/emoji';
import usePageInfoStore from '@/stores/modules/pageInfo';
import useWebsiteConfigStore from '@/stores/modules/websiteConfig';
import { previewImages } from '@/utils/photoPreview';
import { useTypedEffect } from '@/composables/useTypedEffect';
import ScrollNumber from '@/components/ScrollNumber/ScrollNumber.vue';

// stores
const websiteConfigStore = useWebsiteConfigStore();
const config = computed(() => websiteConfigStore.config);

// 打字机效果
const { output: typedOutput, start: startTyped } = useTypedEffect();

// 状态
const talkList = ref<string[]>([]);
const runTime = ref('');

// 页面封面（按旧代码方式获取）
const pageInfoStore = usePageInfoStore();
const cover = computed(() => {
  const page = pageInfoStore.pageList.find((item) => item.pageLabel === 'home');
  const pageCover = page?.pageCover || '';
  return `background: #49b1f5 url(${pageCover}) center center / cover no-repeat`;
});

// 运行时间定时器
let runTimeInterval: ReturnType<typeof setInterval> | null = null;

function updateRunTime() {
  const startTime = config.value.siteStartTime;
  if (!startTime) return;
  const elapsed = Date.now() - new Date(startTime).getTime();
  const days = Math.floor(elapsed / (24 * 60 * 60 * 1000));
  const now = new Date();
  runTime.value = `${days}天${now.getHours()}时${now.getMinutes()}分${now.getSeconds()}秒`;
}

// 向下滚动
function scrollDown() {
  window.scrollTo({
    behavior: 'smooth',
    top: document.documentElement.clientHeight,
  });
}

// 预览头像
function previewAvatar() {
  if (config.value.siteAvatar) {
    previewImages([config.value.siteAvatar]);
  }
}

// 初始化数据
onMounted(async () => {
  // 获取说说列表
  const talkRes = await listTalk();
  talkList.value = talkRes.data.map((item) => parseEmoji(item.content));

  // 设置页面标题
  document.title = config.value.siteName || '博客首页';

  // 启动打字机效果
  startTyped();

  // 启动运行时间计时器
  updateRunTime();
  runTimeInterval = setInterval(updateRunTime, 1000);
});

onUnmounted(() => {
  if (runTimeInterval) {
    clearInterval(runTimeInterval);
  }
});
</script>

<style lang="scss">
.typed-cursor {
  opacity: 1;
  animation: blink 0.7s infinite;
}

@keyframes blink {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

/* 覆盖 TalkSwiper 在首页的样式 */
.home-container .swiper-container {
  margin-top: 0;
}
</style>

<style scoped>
.home-banner {
  position: absolute;
  top: -58px;
  left: 0;
  right: 0;
  height: calc(100vh + 58px);
  background-attachment: fixed;
  text-align: center;
  color: #fff !important;
  animation: header-effect 1s;
}

.banner-container {
  margin-top: 43vh;
  line-height: 1.5;
  color: #eee;
}

.blog-contact a {
  color: #fff !important;
}

.card-info-social {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  padding: 12px 0 6px;
}

.social-icon {
  font-size: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (min-width: 760px) {
  .blog-title {
    font-size: 2.5rem;
  }

  .blog-intro {
    font-size: 1.5rem;
  }

  .blog-contact {
    display: none;
  }

  .home-container {
    animation: main 1s;
    max-width: 1200px;
    margin: calc(100vh + 10px) auto 28px auto;
    padding: 0 5px;
  }
}

@media (max-width: 759px) {
  .blog-title {
    font-size: 26px;
  }

  .blog-contact {
    font-size: 1.25rem;
    line-height: 2;
  }

  .home-container {
    animation: main 1s;
    width: 100%;
    margin: calc(100vh - 8px) auto 0 auto;
  }
}

@keyframes main {
  0% {
    opacity: 0;
    transform: translateY(50px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

.scroll-down {
  cursor: pointer;
  position: absolute;
  bottom: 0;
  width: 100%;
}

.scroll-down i {
  font-size: 2rem;
}

.blog-wrapper {
  position: sticky;
  top: 10px;
}

.blog-card {
  line-height: 2;
  padding: 1.25rem 1.5rem;
}

.author-wrapper {
  text-align: center;
}

.blog-info-wrapper {
  display: flex;
  justify-content: space-evenly;
  padding: 0.875rem 0;
  margin: 0.5rem 0;
}

.blog-info-data {
  flex: 1;
  text-align: center;
}

.blog-info-data a {
  text-decoration: none;
}

.collection-btn {
  text-align: center;
  z-index: 1;
  font-size: 14px;
  position: relative;
  display: block;
  background-color: #49b1f5;
  color: #fff !important;
  height: 32px;
  line-height: 32px;
  transition-duration: 1s;
  transition-property: color;
}

.collection-btn:before {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: -1;
  background: #ff7242;
  content: '';
  transition-timing-function: ease-out;
  transition-duration: 0.5s;
  transition-property: transform;
  transform: scaleX(0);
  transform-origin: 0 50%;
}

.collection-btn:hover:before {
  transition-timing-function: cubic-bezier(0.45, 1.64, 0.47, 0.66);
  transform: scaleX(1);
}

.author-avatar {
  cursor: pointer;
  transition: transform 0.3s ease;
}

.author-avatar:hover {
  transform: scale(1.1);
}

.web-info {
  padding: 0.25rem;
  font-size: 0.875rem;
}

.web-info-title {
  font-size: 0.9rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.float-right {
  float: right;
}

.scroll-down-effects {
  color: #eee !important;
  text-align: center;
  text-shadow: 0.1rem 0.1rem 0.2rem rgba(0, 0, 0, 0.15);
  line-height: 1.5;
  display: inline-block;
  text-rendering: auto;
  -webkit-font-smoothing: antialiased;
  animation: scroll-down-effect 1.5s infinite;
}

@keyframes scroll-down-effect {
  0% {
    top: 0;
    opacity: 0.4;
    filter: alpha(opacity=40);
  }
  50% {
    top: -16px;
    opacity: 1;
    filter: none;
  }
  100% {
    top: 0;
    opacity: 0.4;
    filter: alpha(opacity=40);
  }
}

@keyframes header-effect {
  0% {
    opacity: 0;
    transform: translateY(-50px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
