<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">分类</h1>
    </div>
    <!-- 分类内容 -->
    <main
      class="blog-container category-page-container"
      :class="{ 'empty-content-container': !loading && categoryList.length === 0 }"
    >
      <!-- 加载骨架屏 -->
      <div v-if="loading" class="category-content">
        <div class="category-header-skeleton">
          <v-skeleton-loader type="text" width="200" />
        </div>
        <div class="category-list">
          <div v-for="n in 4" :key="n" class="category-item skeleton-item">
            <v-skeleton-loader type="avatar, text" class="category-summary-skeleton" />
            <v-skeleton-loader type="list-item-three-line" class="category-posts-skeleton" />
          </div>
        </div>
      </div>
      <!-- 分类列表 -->
      <div v-else-if="categoryList.length > 0" class="category-content">
        <!-- 统计头部 -->
        <div class="category-header">
          <v-icon class="stat-icon" size="28">mdi-format-list-bulleted</v-icon>
          <span class="stat-text"
            >共计 <strong>{{ categoryList.length }}</strong> 个分类</span
          >
        </div>
        <!-- 列表 -->
        <div class="category-list">
          <article v-for="item of categoryList" :key="item.id" class="category-item">
            <button
              type="button"
              class="category-summary"
              :title="item.categoryName"
              @click="goToCategory(item.id)"
            >
              <v-icon class="category-icon" size="26">mdi-checkbox-multiple-blank</v-icon>
              <span class="category-name">{{ item.categoryName }}</span>
              <span class="category-count">{{ item.articleCount }}</span>
            </button>

            <div class="category-posts">
              <button
                v-for="article in item.articles"
                :key="article.id"
                type="button"
                class="category-post"
                :title="article.articleTitle"
                @click="goToArticle(article.id)"
              >
                <span class="post-dot"></span>
                <span class="post-title">{{ article.articleTitle }}</span>
              </button>
            </div>
          </article>
        </div>
      </div>
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <v-icon size="64" color="grey-lighten-1">mdi-folder-off-outline</v-icon>
        <p>暂无分类数据</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import { listCategory, type Category } from '@/apis/category';
import usePageInfoStore from '@/stores/modules/pageInfo.ts';
import { useSnackbarStore } from '@/stores/modules/snackbar.ts';

const pageInfoStore = usePageInfoStore();
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore);

const router = useRouter();
const snackbarStore = useSnackbarStore();

const categoryList = ref<Category[]>([]);
const loading = ref(true);

onMounted(() => {
  listCategory()
    .then((res) => {
      categoryList.value = res.data;
    })
    .catch(() => {
      snackbarStore.error('获取分类列表失败');
    })
    .finally(() => {
      loading.value = false;
    });
});

function goToCategory(id: number) {
  router.push('/category/' + id).then(() => {
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
.category-page-container {
  max-width: 1220px;
  padding: 0 20px;
  background: transparent;
}

.category-content {
  width: 100%;
}

.category-header,
.category-header-skeleton {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 22px;
}

.stat-icon {
  margin-right: 8px;
  color: var(--color-primary);
}

.stat-text {
  color: var(--color-text-secondary);
  font-size: 16px;
}

.stat-text strong {
  margin: 0 4px;
  color: var(--color-primary);
  font-size: 18px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 0.4rem;
}

.category-item {
  display: flex;
  min-width: 0;
  gap: 16px;
  padding: 14px;
  border: 1px solid rgba(227, 232, 247, 0.9);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 6px 16px -6px rgba(44, 45, 48, 0.12);
  backdrop-filter: blur(8px);
  backface-visibility: hidden;
  transform-style: preserve-3d;
  transition:
    color 0.3s ease,
    background-color 0.3s ease,
    border-color 0.3s ease,
    box-shadow 0.3s ease;
}

.category-item:hover {
  border-color: var(--color-primary);
  box-shadow: 0 8px 12px -3px color-mix(in srgb, var(--color-primary) 14%, transparent);
}

.category-summary {
  flex: 0 0 auto;
  width: 132px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 14px 12px;
  border: 0;
  border-radius: 10px;
  background: rgba(247, 247, 249, 0.9);
  color: var(--color-text-primary);
  cursor: pointer;
  transition:
    color 0.3s ease,
    background-color 0.3s ease;
}

.category-summary:hover {
  background: color-mix(in srgb, var(--color-primary) 14%, transparent);
  color: var(--color-primary);
}

.category-summary:focus-visible,
.category-post:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: -2px;
}

.category-icon {
  color: var(--color-primary);
}

.category-name {
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-align: center;
  font-size: 1.05em;
  font-weight: 700;
  line-height: 1.3;
}

.category-count {
  color: var(--color-primary);
  font-size: 0.78em;
  opacity: 0.85;
}

.category-posts {
  flex: 1 1 auto;
  min-width: 0;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  grid-auto-rows: min-content;
  gap: 2px 12px;
  align-content: start;
}

.category-post {
  display: flex;
  align-items: center;
  min-width: 0;
  overflow: hidden;
  gap: 8px;
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

.category-post:hover {
  background: color-mix(in srgb, var(--color-primary) 14%, transparent);
  color: var(--color-primary);
}

.post-dot {
  width: 5px;
  height: 5px;
  flex: 0 0 auto;
  border-radius: 50%;
  background: var(--color-primary);
  opacity: 0.45;
}

.post-title {
  flex: 1 1 auto;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.skeleton-item {
  min-height: 164px;
}

.category-summary-skeleton,
.category-posts-skeleton {
  flex: 1 1 auto;
}

.category-summary-skeleton {
  flex: 0 0 132px;
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
  .category-page-container {
    padding: 0 14px;
  }

  .category-header {
    margin-bottom: 16px;
  }

  .stat-text {
    font-size: 14px;
  }

  .stat-text strong {
    font-size: 18px;
  }

  .category-item {
    flex-direction: column;
    gap: 12px;
    padding: 14px;
  }

  .category-summary {
    width: 100%;
    flex-direction: row;
    justify-content: flex-start;
    gap: 8px;
    padding: 10px 12px;
  }

  .category-posts {
    grid-template-columns: minmax(0, 1fr);
  }

  .category-summary-skeleton {
    flex-basis: auto;
  }
}
</style>

<style>
.v-theme--dark .category-item {
  background: rgba(27, 28, 32, 0.9);
  border-color: #3d3d3f;
  box-shadow: 0 8px 16px -4px rgba(0, 0, 0, 0.31);
}

.v-theme--dark .category-item:hover {
  border-color: var(--color-primary);
}

.v-theme--dark .category-summary {
  background: #21232a;
  color: var(--color-text-primary);
}

.v-theme--dark .category-summary:hover,
.v-theme--dark .category-post:hover {
  background: color-mix(in srgb, var(--color-primary) 14%, transparent);
}

.v-theme--dark .category-post {
  color: var(--color-text-primary);
}

.v-theme--dark .empty-state {
  color: var(--color-text-tertiary, #666);
}
</style>
