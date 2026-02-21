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
    <v-row class="home-container" ref="articleListRef">
      <v-col md="9" cols="12">
        <!-- 说说轮播 -->
        <v-card class="animate__animated animate__zoomIn" v-if="talkList.length > 0">
          <TalkSwiper :list="talkList" />
        </v-card>
        <!-- 文章列表 -->
        <v-card
          class="animate__animated animate__zoomIn article-card"
          v-for="(item, index) in articleList"
          :key="item.id"
        >
          <!-- 文章封面图 -->
          <div
            :class="index % 2 === 0 ? 'article-cover left-radius' : 'article-cover right-radius'"
          >
            <a @click="navigateTo('/article/' + item.id)">
              <v-img class="on-hover" width="100%" height="100%" :src="item.articleCover" cover />
            </a>
          </div>
          <!-- 文章信息 -->
          <div class="article-wrapper">
            <div class="article-title">
              <a @click="navigateTo('/article/' + item.id)">
                {{ item.articleTitle }}
              </a>
            </div>
            <div class="article-info">
              <!-- 是否置顶 -->
              <span v-if="item.isTop === 1">
                <span style="color: #ff7242"> <i class="iconfont iconzhiding" /> 置顶 </span>
                <span class="separator">|</span>
              </span>
              <!-- 发表时间 -->
              <v-icon size="14">mdi-calendar-month-outline</v-icon>
              {{ dateFormat.date(item.createTime) }}
              <span class="separator">|</span>
              <!-- 文章分类 -->
              <a class="category-link" @click="navigateTo('/category/' + item.categoryId)">
                <v-icon size="14">mdi-inbox-full</v-icon>
                <span class="category-name">{{ item.categoryName }}</span>
              </a>
              <span class="separator">|</span>
              <!-- 文章标签 -->
              <a
                class="tag-link"
                @click="navigateTo('/tag/' + tag.id)"
                v-for="tag of item.tags?.slice(0, 3)"
                :key="tag.id"
              >
                <v-icon size="14">mdi-tag-multiple</v-icon>
                <span class="tag-name">{{ tag.tagName }}</span>
              </a>
            </div>
            <!-- 文章内容 -->
            <div class="article-content">
              {{ stripMarkdown(item.articleContent) }}
            </div>
          </div>
        </v-card>
        <!-- 分页 -->
        <div class="article-pagination" v-if="totalPage > 1">
          <v-pagination
            v-model="current"
            :length="totalPage"
            :total-visible="5"
            density="comfortable"
            variant="flat"
            @update:model-value="() => loadArticles(true)"
          />
        </div>
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

          <!-- 公告轮播 -->
          <v-card class="animate__animated animate__zoomIn mt-5">
            <AnnouncementSwiper :list="announcementList" />
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
import useAnnouncementStore from '@/stores/modules/announcement';
import { parseEmoji } from '@/utils/emoji';
import usePageInfoStore from '@/stores/modules/pageInfo';
import useWebsiteConfigStore from '@/stores/modules/websiteConfig';
import { previewImages } from '@/utils/photoPreview';
import { useTypedEffect } from '@/composables/useTypedEffect';
import ScrollNumber from '@/components/ScrollNumber/ScrollNumber.vue';
import AnnouncementSwiper from '@/components/Announcement/AnnouncementSwiper.vue';
import { getArticleHomeList, type ArticleHome } from '@/apis/article';
import { dateFormat } from '@/utils/date';
import { useNavigate } from '@/composables/useNavigate';
import { stripMarkdown } from '@/utils/markdown';

// 导航
const { navigateTo } = useNavigate();

// stores
const websiteConfigStore = useWebsiteConfigStore();
const announcementStore = useAnnouncementStore();
const config = computed(() => websiteConfigStore.config);
const announcementList = computed(() => announcementStore.homeList);

// 打字机效果
const { output: typedOutput, start: startTyped } = useTypedEffect();

// 状态
const talkList = ref<string[]>([]);
const runTime = ref('');
const articleList = ref<ArticleHome[]>([]);
const current = ref(1);
const total = ref(0);
const pageSize = 10;
const totalPage = computed(() => Math.ceil(total.value / pageSize));

// 文章列表容器引用
const articleListRef = ref<HTMLElement | null>(null);

// 加载文章列表
async function loadArticles(isPageChange = false) {
  const res = await getArticleHomeList(current.value);
  articleList.value = res.data.list;
  total.value = res.data.total;
  // 分页切换时滚动到文章列表顶部
  if (isPageChange && articleListRef.value) {
    articleListRef.value.scrollIntoView({ behavior: 'smooth' });
  }
}

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

  // 获取文章列表
  await loadArticles();

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

  .article-card {
    display: flex;
    align-items: center;
    height: 280px;
    width: 100%;
    margin-top: 20px;
  }

  .article-cover {
    overflow: hidden;
    height: 100%;
    width: 45%;
  }

  .on-hover {
    transition: all 0.6s;
  }

  .article-card:hover .on-hover {
    transform: scale(1.1);
  }

  .article-wrapper {
    padding: 0 2.5rem;
    width: 55%;
  }

  .article-wrapper a {
    font-size: 1.5rem;
    transition: all 0.3s;
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
    margin: calc(100vh + 10px) auto 0 auto;
  }

  .article-card {
    margin-top: 1rem;
  }

  .article-cover {
    border-radius: 8px 8px 0 0 !important;
    height: 230px !important;
    width: 100%;
  }

  .article-cover :deep(.v-img) {
    border-radius: 8px 8px 0 0 !important;
  }

  .article-wrapper {
    padding: 1.25rem 1.25rem 1.875rem;
  }

  .article-wrapper a {
    font-size: 1.25rem;
    transition: all 0.3s;
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

.left-radius {
  border-radius: 8px 0 0 8px !important;
  order: 0;
}

.right-radius {
  border-radius: 0 8px 8px 0 !important;
  order: 1;
}

.article-wrapper a:hover {
  color: var(--color-primary);
}

.article-title {
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-info {
  font-size: 95%;
  color: #858585;
  line-height: 2;
  margin: 0.375rem 0;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  max-height: calc(2em * 2);
  overflow: hidden;
}

.article-info a {
  font-size: 95%;
  color: #858585 !important;
  cursor: pointer;
}

.article-info a:hover {
  color: var(--color-primary) !important;
}

.category-link,
.tag-link {
  display: inline-flex;
  align-items: center;
  max-width: 120px;
}

.tag-link {
  margin-right: 0.25rem;
}

.category-name,
.tag-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-content {
  line-height: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  color: #5a5a5a;
  letter-spacing: 0.3px;
  font-size: 0.95rem;
}

.separator {
  margin: 0 0.25rem;
}

/* 文章分页样式 */
.article-pagination {
  padding: 24px 0 8px;
  display: flex;
  justify-content: center;
}

.article-pagination :deep(.v-pagination__list) {
  gap: 4px;
}

.article-pagination :deep(.v-pagination .v-btn) {
  min-width: 32px !important;
  height: 32px !important;
  border-radius: 5px !important;
  background-color: rgba(0, 0, 0, 0.04) !important;
  box-shadow: none !important;
  color: rgba(0, 0, 0, 0.55) !important;
  font-size: 14px !important;
  font-weight: 400 !important;
  transition: background-color 0.2s ease !important;
}

.article-pagination :deep(.v-pagination .v-btn .v-btn__overlay),
.article-pagination :deep(.v-pagination .v-btn .v-btn__underlay) {
  display: none !important;
}

.article-pagination :deep(.v-pagination .v-btn:hover) {
  background-color: rgba(0, 0, 0, 0.08) !important;
}

.article-pagination :deep(.v-pagination__item--is-active .v-btn) {
  background-color: #fff !important;
  color: rgba(0, 0, 0, 0.85) !important;
  box-shadow:
    0 0 1px 0 rgba(0, 0, 0, 0.1),
    0 0.5px 5px 0 rgba(0, 0, 0, 0.1) !important;
  pointer-events: none !important;
}

.article-pagination :deep(.v-pagination .v-btn--disabled) {
  background-color: rgba(0, 0, 0, 0.04) !important;
  box-shadow: none !important;
  color: rgba(0, 0, 0, 0.55) !important;
  opacity: 0.4 !important;
}

.article-pagination :deep(.v-pagination__more .v-btn) {
  background-color: rgba(0, 0, 0, 0.04) !important;
  box-shadow: none !important;
  color: rgba(0, 0, 0, 0.55) !important;
  opacity: 0.4 !important;
}

/* 夜间模式 */
.v-theme--dark .article-info {
  color: rgba(255, 255, 255, 0.6);
}

.v-theme--dark .article-info a {
  color: rgba(255, 255, 255, 0.6) !important;
}

.v-theme--dark .article-pagination :deep(.v-pagination .v-btn) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.6) !important;
}

.v-theme--dark .article-pagination :deep(.v-pagination .v-btn:hover) {
  background-color: rgba(255, 255, 255, 0.15) !important;
}

.v-theme--dark .article-pagination :deep(.v-pagination__item--is-active .v-btn) {
  background-color: rgba(255, 255, 255, 0.9) !important;
  color: rgba(0, 0, 0, 0.85) !important;
  box-shadow:
    0 0 1px 0 rgba(0, 0, 0, 0.3),
    0 0.5px 5px 0 rgba(0, 0, 0, 0.3) !important;
}

.v-theme--dark .article-pagination :deep(.v-pagination .v-btn--disabled) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.6) !important;
}

.v-theme--dark .article-pagination :deep(.v-pagination__more .v-btn) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.6) !important;
}

.v-theme--dark .article-title a {
  color: rgba(255, 255, 255, 0.85);
}

.v-theme--dark .article-title a:hover,
.v-theme--dark .article-info a:hover {
  color: var(--color-primary) !important;
}

.v-theme--dark .article-content {
  color: rgba(255, 255, 255, 0.7);
}

.v-theme--dark .web-info-title {
  color: rgba(255, 255, 255, 0.85);
}

.v-theme--dark .web-info {
  color: rgba(255, 255, 255, 0.7);
}

.v-theme--dark .card-info-social a {
  filter: brightness(1.2);
}

.v-theme--dark .card-info-social .icongithub {
  color: #fff !important;
}

.v-theme--dark .article-card,
.v-theme--dark .blog-card {
  background-color: #3a3a3a !important;
}

.v-theme--dark .home-container :deep(.v-card) {
  background-color: #3a3a3a !important;
}
</style>
