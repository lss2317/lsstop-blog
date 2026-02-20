<template>
  <div>
    <!-- 加载状态 -->
    <template v-if="loading">
      <div class="banner skeleton-banner" />
      <v-row class="article-container">
        <v-col md="9" cols="12">
          <v-card class="article-wrapper">
            <v-skeleton-loader type="article, paragraph, paragraph, paragraph" />
          </v-card>
        </v-col>
        <v-col md="3" cols="12" class="d-md-block d-none">
          <v-card class="right-container">
            <v-skeleton-loader type="list-item-three-line" />
          </v-card>
        </v-col>
      </v-row>
    </template>

    <!-- 空状态 -->
    <template v-else-if="!article">
      <div class="banner" :style="archiveCover" />
      <v-card class="blog-container empty-state">
        <v-icon size="64" color="grey">mdi-file-document-outline</v-icon>
        <p>文章不存在</p>
      </v-card>
    </template>

    <!-- 内容 -->
    <template v-else>
      <!-- 封面图 -->
      <div class="banner" :style="bannerStyle">
        <div class="article-info-container fadeInUp" style="--delay: 0.1s">
          <!-- 文章标题 -->
          <div class="article-title">{{ article.articleTitle }}</div>
          <div class="article-info">
            <div class="info-line">
              <span>
                <v-icon size="small">mdi-calendar</v-icon>
                {{ formatDate(article.createTime) }}
              </span>
              <span class="separator">|</span>
              <span>
                <v-icon size="small">mdi-update</v-icon>
                {{ formatDate(article.updateTime) }}
              </span>
              <span class="separator">|</span>
              <span class="article-category">
                <v-icon size="small">mdi-folder-outline</v-icon>
                <a @click="navigateTo('/category/' + article.categoryId)">
                  {{ article.categoryName }}
                </a>
              </span>
            </div>
            <div class="info-line">
              <span>
                <v-icon size="small">mdi-text-box-outline</v-icon>
                {{ formatWordNum(wordNum) }}
              </span>
              <span class="separator">|</span>
              <span>
                <v-icon size="small">mdi-clock-outline</v-icon>
                {{ readTime }}
              </span>
              <span class="separator">|</span>
              <span>
                <v-icon size="small">mdi-eye-outline</v-icon>
                {{ article.viewCount }}
              </span>
            </div>
          </div>
        </div>
      </div>
      <!-- 内容 -->
      <v-row class="article-container">
        <v-col md="9" cols="12">
          <v-card class="article-wrapper fadeInUp" style="--delay: 0.2s">
            <article
              id="write"
              ref="articleRef"
              class="article-content markdown-body"
              v-html="articleHtml"
            />
            <!-- 版权声明 -->
            <div class="aritcle-copyright">
              <div>
                <span>文章作者：</span>
                <a @click="navigateTo('/')">
                  {{ websiteConfig.siteAuthor }}
                </a>
              </div>
              <div>
                <span>文章链接：</span>
                <a :href="articleHref" target="_blank">{{ articleHref }}</a>
              </div>
              <div>
                <span>版权声明：</span>本博客所有文章除特别声明外，均采用
                <a href="https://creativecommons.org/licenses/by-nc-sa/4.0/" target="_blank">
                  CC BY-NC-SA 4.0
                </a>
                许可协议。转载请注明文章出处。
              </div>
            </div>
            <!-- 标签、点赞和分享 -->
            <div class="article-operation">
              <div class="tag-container">
                <a
                  v-for="tag of article.tags?.slice(0, 3)"
                  :key="tag.id"
                  @click="navigateTo('/tag/' + tag.id)"
                >
                  {{ tag.tagName }}
                </a>
              </div>
              <div class="operation-right">
                <!-- 点赞按钮 -->
                <div :class="['like-btn-inline', isLiked ? 'liked' : '']" @click="handleLike">
                  <v-icon size="16" class="like-icon">
                    {{ isLiked ? 'mdi-thumb-up' : 'mdi-thumb-up-outline' }}
                  </v-icon>
                  <span class="like-count">{{ article.likeCount || 0 }}</span>
                </div>
                <ShareButtons :url="articleHref" :title="article.articleTitle" />
              </div>
            </div>
            <div class="pagination-post">
              <!-- 上一篇 -->
              <div
                v-if="article.preArticle"
                :class="postClass"
                @click="navigateToArticle(article.preArticle.id)"
              >
                <a>
                  <img
                    class="post-cover"
                    :src="article.preArticle.articleCover"
                    :alt="article.preArticle.articleTitle"
                    loading="lazy"
                  />
                  <div class="post-info">
                    <div class="label">上一篇</div>
                    <div class="post-title">{{ article.preArticle.articleTitle }}</div>
                  </div>
                </a>
              </div>
              <!-- 下一篇 -->
              <div
                v-if="article.nextArticle"
                :class="postClass"
                @click="navigateToArticle(article.nextArticle.id)"
              >
                <a>
                  <img
                    class="post-cover"
                    :src="article.nextArticle.articleCover"
                    :alt="article.nextArticle.articleTitle"
                    loading="lazy"
                  />
                  <div class="post-info" style="text-align: right">
                    <div class="label">下一篇</div>
                    <div class="post-title">{{ article.nextArticle.articleTitle }}</div>
                  </div>
                </a>
              </div>
            </div>
            <!-- 推荐文章 -->
            <div v-if="article.recommendArticles?.length" class="recommend-container">
              <div class="recommend-title">
                <v-icon size="20" color="#4c4948">mdi-thumb-up</v-icon>
                相关推荐
              </div>
              <div class="recommend-list">
                <div
                  v-for="item of article.recommendArticles"
                  :key="item.id"
                  class="recommend-item"
                  @click="navigateToArticle(item.id)"
                >
                  <a>
                    <img
                      class="recommend-cover"
                      :src="item.articleCover"
                      :alt="item.articleTitle"
                      loading="lazy"
                    />
                    <div class="recommend-info">
                      <div class="recommend-date">
                        <v-icon size="small">mdi-calendar</v-icon>
                        {{ formatDate(item.createTime) }}
                      </div>
                      <div class="recommend-article-title">{{ item.articleTitle }}</div>
                    </div>
                  </a>
                </div>
              </div>
            </div>
            <!-- 评论分隔线 -->
            <div class="comment-divider">
              <v-icon size="18" color="#8a919f">mdi-chat-processing-outline</v-icon>
              <span>评论区</span>
            </div>
            <!-- 评论 -->
            <BlogComment :type="CommentTypeEnum.ARTICLE" :typeId="String(article.id)" />
          </v-card>
        </v-col>
        <!-- 侧边功能 -->
        <v-col md="3" cols="12" class="d-md-block d-none">
          <div style="position: sticky; top: 20px">
            <!-- 文章目录 -->
            <v-card class="right-container fadeInUp" style="--delay: 0.3s">
              <div class="right-title">
                <v-icon size="16">mdi-menu</v-icon>
                <span style="margin-left: 10px">目录</span>
              </div>
              <div id="toc" />
            </v-card>
            <!-- 最新文章 -->
            <v-card class="right-container fadeInUp" style="margin-top: 20px; --delay: 0.4s">
              <div class="right-title">
                <v-icon size="16">mdi-clock-outline</v-icon>
                <span style="margin-left: 10px">最新文章</span>
              </div>
              <div class="article-list">
                <div v-for="item of article.newestArticles" :key="item.id" class="article-item">
                  <a class="content-cover" @click="navigateToArticle(item.id)">
                    <img :src="item.articleCover" :alt="item.articleTitle" loading="lazy" />
                  </a>
                  <div class="content">
                    <div class="content-title">
                      <a @click="navigateToArticle(item.id)">{{ item.articleTitle }}</a>
                    </div>
                    <div class="content-time">{{ formatDate(item.createTime) }}</div>
                  </div>
                </div>
              </div>
            </v-card>
          </div>
        </v-col>
      </v-row>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import { getArticleById, type Article } from '@/apis/article';
import usePageInfoStore from '@/stores/modules/pageInfo';
import { dateFormat } from '@/utils/date';
import { markdownToHtml, stripMarkdown } from '@/utils/markdown';
import { previewImages } from '@/utils/photoPreview';
import { formatWordNum } from '@/utils/format';
import { useNavigate } from '@/composables/useNavigate';
import useWebsiteConfigStore from '@/stores/modules/websiteConfig';
import useLikeStore from '@/stores/modules/like';
import { LikeTypeEnum } from '@/constants/likeType';
import BlogComment from '@/components/Comment/BlogComment.vue';
import { CommentTypeEnum } from '@/constants/commentType';
import ShareButtons from '@/components/Share/ShareButtons.vue';
import tocbot from 'tocbot';
import Clipboard from 'clipboard';
import { useSnackbarStore } from '@/stores/modules/snackbar';

const route = useRoute();
const { navigateToArticle, navigateTo } = useNavigate();

// 获取默认封面样式
const pageInfoStore = usePageInfoStore();

// 文章不存在时使用归档页封面
const archiveCover = computed(() => {
  const page = pageInfoStore.pageList.find((item) => item.pageLabel === 'archive');
  const cover = page?.pageCover || '';
  return {
    backgroundImage: `url(${cover})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
  };
});

const websiteConfigStore = useWebsiteConfigStore();
const likeStore = useLikeStore();
const snackbarStore = useSnackbarStore();

// Clipboard 实例
let clipboard: Clipboard | null = null;

// 加载状态
const loading = ref(true);

// 文章数据
const article = ref<Article | null>(null);

// refs
const articleRef = ref<HTMLElement | null>(null);

// 网站配置
const websiteConfig = computed(() => websiteConfigStore.config);

// 封面图样式
const bannerStyle = computed(() => {
  if (!article.value) return {};
  return {
    backgroundImage: `url(${article.value.articleCover})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
  };
});

// 文章HTML内容
const articleHtml = computed(() => markdownToHtml(article.value?.articleContent || ''));

// 文章链接
const articleHref = computed(() => window.location.href);

// 字数统计（去除Markdown语法）
const wordNum = computed(() => stripMarkdown(article.value?.articleContent || '').length);

// 阅读时长（300字/分钟）
const readTime = computed(() => {
  const minutes = Math.ceil(wordNum.value / 300);
  return minutes < 1 ? '1 分钟' : `${minutes} 分钟`;
});

// 是否已点赞
const isLiked = computed(() => likeStore.isLiked(LikeTypeEnum.ARTICLE, article.value?.id || 0));

// 上下篇文章样式
const postClass = computed(() => {
  const hasPre = !!article.value?.preArticle;
  const hasNext = !!article.value?.nextArticle;
  return hasPre && hasNext ? 'post' : 'post full';
});

// 格式化日期
const formatDate = (date: string) => dateFormat.date(date);

// 点赞
const handleLike = async () => {
  if (!article.value) return;
  const result = await likeStore.toggleLike(LikeTypeEnum.ARTICLE, article.value.id);
  if (result !== null) {
    article.value.likeCount += result ? 1 : -1;
  }
};

// 生成目录
const generateToc = () => {
  nextTick(() => {
    // 延迟确保 DOM 完全渲染
    setTimeout(() => {
      if (!articleRef.value) return;
      // 给标题添加id
      const headings = articleRef.value.querySelectorAll('h1, h2, h3, h4, h5, h6');
      headings.forEach((heading, index) => {
        heading.id = `heading-${index}`;
      });
      // 先销毁旧实例
      tocbot.destroy();
      // 初始化tocbot，使用视口高度35%作为偏移
      tocbot.init({
        tocSelector: '#toc',
        contentSelector: '.article-content',
        headingSelector: 'h1, h2, h3, h4, h5, h6',
        hasInnerContainers: true,
        headingsOffset: Math.round(window.innerHeight * 0.35),
        scrollSmoothOffset: -80,
        onClick: (e) => e.preventDefault(),
      });
      // 初始化代码复制
      clipboard?.destroy();
      clipboard = new Clipboard('.copy-btn');
      clipboard.on('success', () => {
        snackbarStore.success('复制成功');
      });
      // 图片预览
      initImagePreview();
    }, 100);
  });
};

// 初始化图片预览
const initImagePreview = () => {
  if (!articleRef.value) return;
  const images = articleRef.value.querySelectorAll('img');
  const imgList = Array.from(images).map((img) => img.src);
  images.forEach((img, index) => {
    img.style.cursor = 'zoom-in';
    img.addEventListener('click', () => previewImages(imgList, index));
  });
};

// 组件卸载时销毁
onUnmounted(() => {
  tocbot.destroy();
  clipboard?.destroy();
});

// 获取文章详情
const fetchArticle = async (id: number) => {
  // 验证 ID 是否为有效数字
  if (isNaN(id) || id <= 0) {
    loading.value = false;
    return;
  }

  loading.value = true;
  try {
    const res = await getArticleById(id);
    // 检查是否返回了有效数据
    if (!res.data || !res.data.id) {
      article.value = null;
      return;
    }
    article.value = res.data;
    // SEO: 设置页面标题
    document.title = res.data.articleTitle;
    // DOM 渲染后再生成目录
    generateToc();
  } catch (error) {
    console.error('获取文章失败', error);
    article.value = null;
  } finally {
    loading.value = false;
  }
};

// 监听路由变化
watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      fetchArticle(Number(newId));
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.skeleton-banner {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.banner:before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

/* 文章元信息 */
.article-info {
  font-size: 14px;
  line-height: 2;
}

.info-line {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 4px;
}

.info-line span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.separator {
  margin: 0 4px;
  opacity: 0.6;
}

/* 动画效果 */
.fadeInUp {
  --delay: 0s;
  opacity: 0;
  animation: fadeInUp 0.6s ease forwards;
  animation-delay: var(--delay);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (min-width: 760px) {
  .banner {
    color: #eee !important;
  }

  .article-info-container {
    position: absolute;
    bottom: 6.25rem;
    padding: 0 8%;
    width: 100%;
    text-align: center;
  }

  .article-title {
    font-size: 35px;
    margin: 20px 0 12px;
  }

  .pagination-post {
    display: flex;
  }

  .post {
    width: 50%;
  }

  .recommend-item {
    position: relative;
    display: inline-block;
    overflow: hidden;
    margin: 3px;
    width: calc(33.333% - 6px);
    height: 200px;
    background: #000;
    vertical-align: bottom;
  }
}

@media (max-width: 759px) {
  .banner {
    color: #eee !important;
    height: 360px;
  }

  .blog-container {
    margin: 322px 5px 0 5px;
  }

  .article-info-container {
    position: absolute;
    bottom: 1.3rem;
    padding: 0 5%;
    width: 100%;
    color: #eee;
    text-align: left;
  }

  .article-title {
    font-size: 1.5rem;
    margin-bottom: 0.4rem;
  }

  .info-line {
    justify-content: flex-start;
  }

  .post {
    width: 100%;
  }

  .pagination-post {
    display: block;
  }

  .recommend-item {
    position: relative;
    display: inline-block;
    overflow: hidden;
    width: calc(100% - 4px);
    height: 150px;
    margin: 2px;
    background: #000;
    vertical-align: bottom;
  }
}

.article-operation {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.operation-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.like-btn-inline {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  background: rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition:
    transform 0.2s ease,
    background 0.2s ease;
}

.like-btn-inline:hover {
  background: rgba(235, 80, 85, 0.1);
  color: #eb5055;
}

.like-btn-inline:active {
  transform: scale(0.95);
}

.like-btn-inline:hover :deep(.v-icon) {
  color: #eb5055 !important;
  transform: scale(1.15);
}

.like-btn-inline .like-icon {
  color: #666;
  transition: transform 0.2s ease;
}

.like-btn-inline.liked {
  color: #fff;
  background: #eb5055;
}

.like-btn-inline.liked .like-icon {
  color: #fff !important;
}

.like-btn-inline.liked:hover {
  background: #d64549;
  color: #fff;
}

.like-count {
  margin-left: 6px;
  font-weight: 500;
}

.article-category {
  display: inline-flex;
  align-items: center;
  max-width: 150px;
}

.article-category a {
  color: #fff !important;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s ease;
}

.article-category a:hover {
  color: var(--color-primary) !important;
}

.tag-container {
  display: flex;
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.tag-container a {
  display: inline-block;
  margin: 0.5rem 0.5rem 0.5rem 0;
  padding: 0 0.75rem;
  max-width: 150px;
  min-width: 50px;
  border: 1px solid #49b1f5;
  border-radius: 1rem;
  color: #49b1f5 !important;
  font-size: 12px;
  line-height: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 0 1 auto;
}

.tag-container a:hover {
  color: #fff !important;
  background: #49b1f5;
  transition: all 0.5s;
}

.aritcle-copyright {
  position: relative;
  margin-top: 40px;
  margin-bottom: 10px;
  font-size: 0.875rem;
  line-height: 2;
  padding: 0.625rem 1rem;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 3px 8px 6px rgb(7 17 27 / 6%);
}

.aritcle-copyright span {
  color: #49b1f5;
  font-weight: bold;
}

.aritcle-copyright a {
  text-decoration: underline !important;
  color: #99a9bf !important;
}

.aritcle-copyright:before {
  position: absolute;
  top: 0.7rem;
  right: 0.7rem;
  width: 1rem;
  height: 1rem;
  border-radius: 1rem;
  background: #49b1f5;
  content: '';
}

.aritcle-copyright:after {
  position: absolute;
  top: 0.95rem;
  right: 0.95rem;
  width: 0.5rem;
  height: 0.5rem;
  border-radius: 0.5em;
  background: #fff;
  content: '';
}

.pagination-post {
  margin-top: 40px;
  overflow: hidden;
  width: 100%;
  background: #000;
}

.post {
  position: relative;
  height: 150px;
  overflow: hidden;
  cursor: pointer;
}

.post-info {
  position: absolute;
  top: 50%;
  padding: 20px 40px;
  width: 100%;
  transform: translate(0, -50%);
  line-height: 2;
  font-size: 14px;
}

.post-cover {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0.4;
  transition: all 0.6s;
  object-fit: cover;
}

.post a {
  position: relative;
  display: block;
  overflow: hidden;
  height: 150px;
}

.post:hover .post-cover {
  opacity: 0.8;
  transform: scale(1.1);
}

.label {
  font-size: 90%;
  color: #eee;
}

.post-title {
  font-weight: 500;
  color: #fff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 评论区分隔线 */
.comment-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin: 40px 0 10px;
  padding: 16px 0;
  position: relative;
  color: #8a919f;
  font-size: 14px;
  font-weight: 500;
}

.comment-divider::before,
.comment-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(to right, transparent, #e4e6eb, transparent);
}

.comment-divider::before {
  background: linear-gradient(to left, #e4e6eb, transparent);
}

.comment-divider::after {
  background: linear-gradient(to right, #e4e6eb, transparent);
}

.full {
  width: 100% !important;
}

.right-container {
  padding: 20px 24px;
  font-size: 14px;
}

.right-title {
  display: flex;
  align-items: center;
  line-height: 2;
  font-size: 16px;
  margin-bottom: 6px;
}

.recommend-container {
  margin-top: 40px;
}

.recommend-title {
  font-size: 20px;
  line-height: 2;
  font-weight: bold;
  margin-bottom: 5px;
}

.recommend-cover {
  width: 100%;
  height: 100%;
  opacity: 0.4;
  transition: all 0.6s;
  object-fit: cover;
}

.recommend-info {
  line-height: 2;
  color: #fff;
  position: absolute;
  top: 50%;
  padding: 0 20px;
  width: 100%;
  transform: translate(0, -50%);
  text-align: center;
  font-size: 14px;
}

.recommend-date {
  font-size: 90%;
}

.recommend-article-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommend-item:hover .recommend-cover {
  opacity: 0.8;
  transform: scale(1.1);
}

.recommend-item {
  cursor: pointer;
}

.article-item {
  display: flex;
  align-items: center;
  padding: 6px 0;
}

.article-item:first-child {
  padding-top: 0;
}

.article-item:last-child {
  padding-bottom: 0;
}

.article-item:not(:last-child) {
  border-bottom: 1px dashed rgba(255, 255, 255, 0.1);
}

.article-item img {
  width: 100%;
  height: 100%;
  transition: all 0.6s;
  object-fit: cover;
}

.article-item img:hover {
  transform: scale(1.1);
}

.content {
  flex: 1;
  padding-left: 10px;
  word-break: break-all;
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
}

.content-cover {
  width: 60px;
  height: 60px;
  overflow: hidden;
  cursor: pointer;
}

.content-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.content-title a {
  font-size: 95%;
  cursor: pointer;
}

.content-title a:hover {
  color: #2ba1d1;
}

.content-time {
  color: #858585;
  font-size: 85%;
  line-height: 2;
}

/* 目录样式 */
#toc :deep(.toc-list) {
  list-style: none;
  padding: 0;
  margin: 0;
}

#toc :deep(.toc-list-item) {
  line-height: 1.8;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

#toc :deep(.toc-link) {
  color: #666;
  text-decoration: none;
}

#toc :deep(.toc-link:hover),
#toc :deep(.is-active-link) {
  color: #49b1f5;
  border-radius: 4px;
}

#toc :deep(.is-active-link) {
  border-left: none !important;
}

#toc :deep(.toc-list .toc-list) {
  padding-left: 12px;
}

/* 空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #999;
}

.empty-state p {
  margin: 16px 0 24px;
  font-size: 16px;
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
