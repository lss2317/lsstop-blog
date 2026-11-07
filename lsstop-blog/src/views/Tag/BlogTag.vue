<template>
  <div>
    <!-- banner -->
    <div class="tag-banner banner" :style="cover">
      <h1 class="banner-title">标签</h1>
    </div>
    <!-- 标签列表 -->
    <main
      class="blog-container tag-page-container"
      :class="{ 'empty-content-container': !loading && tagList.length === 0 }"
    >
      <!-- 加载骨架屏 -->
      <div v-if="loading" class="tag-content">
        <div class="tag-header-skeleton">
          <v-skeleton-loader type="text" width="200" />
        </div>
        <div class="tag-grid">
          <div v-for="n in 6" :key="n" class="tag-card skeleton-card">
            <v-skeleton-loader type="avatar, text" class="tag-summary-skeleton" />
            <v-skeleton-loader type="list-item-three-line" class="tag-articles-skeleton" />
          </div>
        </div>
      </div>
      <!-- 标签列表 -->
      <div v-else-if="tagList.length > 0" class="tag-content">
        <!-- 统计头部 -->
        <div class="tag-header">
          <v-icon class="stat-icon" size="28">mdi-tag-multiple-outline</v-icon>
          <span class="stat-text"
            >共计 <strong>{{ tagList.length }}</strong> 个标签</span
          >
        </div>
        <div class="tag-grid">
          <article v-for="item of tagList" :key="item.id" class="tag-card">
            <button
              type="button"
              class="tag-summary"
              :title="item.tagName"
              @click="goToTag(item.id)"
            >
              <v-icon class="tag-icon" size="24">mdi-tag</v-icon>
              <span class="tag-name">{{ item.tagName }}</span>
              <span class="tag-count">{{ item.articleCount }}</span>
            </button>

            <div class="tag-articles" :class="{ 'is-empty': item.articles.length === 0 }">
              <button
                v-for="article in item.articles"
                :key="article.id"
                type="button"
                class="tag-article"
                :title="article.articleTitle"
                @click="goToArticle(article.id)"
              >
                <span class="article-dot"></span>
                <span class="article-title">{{ article.articleTitle }}</span>
              </button>
              <button
                v-if="item.articles.length === 0"
                type="button"
                class="tag-empty"
                @click="goToTag(item.id)"
              >
                暂无文章
              </button>
            </div>
          </article>
        </div>
      </div>
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <v-icon size="64" color="grey-lighten-1">mdi-tag-off-outline</v-icon>
        <p>暂无标签数据</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import { listTag, type Tag } from '@/apis/tag';
import usePageInfoStore from '@/stores/modules/pageInfo.ts';
import { useSnackbarStore } from '@/stores/modules/snackbar.ts';

const pageInfoStore = usePageInfoStore();
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore);

const router = useRouter();
const snackbarStore = useSnackbarStore();

const tagList = ref<Tag[]>([]);
const loading = ref(true);

onMounted(() => {
  listTag()
    .then((res) => {
      tagList.value = res.data;
    })
    .catch(() => {
      snackbarStore.error('获取标签列表失败');
    })
    .finally(() => {
      loading.value = false;
    });
});

function goToTag(id: number) {
  router.push('/tag/' + id).then(() => {
    window.scrollTo(0, 0);
  });
}

function goToArticle(id: number) {
  router.push('/article/' + id).then(() => {
    window.scrollTo(0, 0);
  });
}
</script>

<style scoped>
.tag-page-container {
  max-width: 1400px;
  padding: 0 24px;
  background: transparent;
}

.tag-content {
  width: 100%;
}

.tag-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 22px;
}

.tag-header-skeleton {
  display: flex;
  justify-content: center;
  margin-bottom: 28px;
}

.stat-icon {
  color: var(--color-primary);
  margin-right: 8px;
}

.stat-text {
  font-size: 16px;
  color: var(--color-text-secondary);
}

.stat-text strong {
  color: var(--color-primary);
  font-size: 18px;
  margin: 0 4px;
}

.tag-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 0.4rem;
}

.tag-card {
  display: flex;
  min-width: 0;
  gap: 14px;
  padding: 16px;
  border: 1px solid #e3e8f7;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 8px 16px -4px rgba(44, 45, 48, 0.05);
  backface-visibility: hidden;
  transform-style: preserve-3d;
  transition:
    color 0.3s ease,
    background-color 0.3s ease,
    border-color 0.2s ease,
    box-shadow 0.3s ease;
}

.tag-card:hover {
  border-color: var(--color-primary);
  box-shadow: 0 8px 12px -3px color-mix(in srgb, var(--color-primary) 14%, transparent);
}

.tag-summary {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  width: 120px;
  padding: 14px 12px;
  border: 0;
  border-radius: 10px;
  background: #f7f8fa;
  color: var(--color-text-primary);
  cursor: pointer;
  transition:
    color 0.3s ease,
    background-color 0.3s ease;
}

.tag-summary:focus-visible,
.tag-article:focus-visible,
.tag-empty:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: -2px;
}

.tag-icon {
  color: var(--color-primary);
  transition: transform 0.2s ease;
}

.tag-summary:hover .tag-icon {
  transform: rotate(-10deg) scale(1.08);
}

.tag-name {
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-align: center;
  font-size: 1.05em;
  font-weight: 700;
  line-height: 1.3;
  transition: color 0.2s ease;
}

.tag-summary:hover {
  background: color-mix(in srgb, var(--color-primary) 14%, transparent);
  color: var(--color-primary);
}

.tag-summary:hover .tag-name {
  color: var(--color-primary);
}

.tag-count {
  font-size: 0.78em;
  color: var(--color-primary);
  opacity: 0.85;
}

.tag-articles {
  flex: 1 1 auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
  overflow: hidden;
}

.tag-articles.is-empty {
  align-items: center;
  justify-content: center;
}

.tag-article {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  overflow: hidden;
  padding: 6px 8px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: var(--color-text-primary);
  text-align: left;
  font-size: 0.92em;
  line-height: 1.4;
  cursor: pointer;
  transition:
    color 0.3s ease,
    background-color 0.3s ease;
}

.article-dot {
  width: 5px;
  height: 5px;
  flex-shrink: 0;
  border-radius: 50%;
  background: var(--color-primary);
  opacity: 0.45;
}

.article-title {
  min-width: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag-article:hover {
  background: color-mix(in srgb, var(--color-primary) 14%, transparent);
  color: var(--color-primary);
}

.tag-empty {
  width: 100%;
  padding: 8px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: var(--color-text-tertiary);
  text-align: center;
  font-size: 0.9em;
  opacity: 0.7;
  cursor: pointer;
}

.skeleton-card {
  min-height: 128px;
}

.tag-summary-skeleton,
.tag-articles-skeleton {
  flex: 1 1 auto;
}

.tag-summary-skeleton {
  flex: 0 0 120px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: var(--color-text-tertiary, #9e9e9e);
}

.empty-state p {
  margin-top: 16px;
  font-size: 15px;
}

@media (max-width: 767px) {
  .tag-page-container {
    padding: 0 14px;
  }

  .tag-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .tag-header {
    margin-bottom: 16px;
  }

  .stat-text {
    font-size: 14px;
  }

  .stat-text strong {
    font-size: 18px;
  }

  .tag-card {
    gap: 12px;
    padding: 14px;
  }

  .tag-summary {
    width: 88px;
    padding: 12px 8px;
  }

  .tag-name {
    font-size: 13px;
  }

  .article-title {
    font-size: 13px;
  }

  .tag-summary-skeleton {
    flex-basis: 88px;
  }
}
</style>

<style>
/* 夜间模式样式 */
.v-theme--dark .tag-card {
  background: #1b1c20;
  border-color: #3d3d3f;
  box-shadow: 0 8px 16px -4px rgba(0, 0, 0, 0.31);
}

.v-theme--dark .tag-card:hover {
  border-color: color-mix(in srgb, var(--color-primary) 45%, transparent);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.22);
}

.v-theme--dark .tag-summary {
  background: #21232a;
  color: var(--color-text-primary);
}

.v-theme--dark .tag-summary:hover,
.v-theme--dark .tag-article:hover {
  background: color-mix(in srgb, var(--color-primary) 14%, transparent);
}

.v-theme--dark .tag-article {
  color: var(--color-text-secondary);
}

.v-theme--dark .empty-state {
  color: var(--color-text-tertiary, #666);
}
</style>
