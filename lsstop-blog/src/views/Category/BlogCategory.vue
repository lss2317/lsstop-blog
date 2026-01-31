<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">分类</h1>
    </div>
    <!-- 分类内容 -->
    <v-card class="blog-container">
      <!-- 加载骨架屏 -->
      <div v-if="loading" class="category-list">
        <v-skeleton-loader v-for="n in 6" :key="n" type="list-item" class="skeleton-item" />
      </div>
      <!-- 分类列表 -->
      <div v-else-if="categoryList.length > 0">
        <!-- 统计头部 -->
        <div class="category-header">
          <v-icon class="stat-icon" size="28">mdi-layers-outline</v-icon>
          <span class="stat-text"
            >共计 <strong>{{ categoryList.length }}</strong> 个分类</span
          >
        </div>
        <!-- 列表 -->
        <div class="category-list">
          <div
            v-for="(item, index) of categoryList"
            :key="item.id"
            class="category-item"
            :style="{ '--delay': index * 0.05 + 's' }"
            @click="goToCategory(item.id)"
          >
            <div class="category-left">
              <span class="category-dot"></span>
              <span class="category-name">{{ item.categoryName }}</span>
            </div>
            <div class="category-right">
              <span class="category-count">{{
                item.articleCount ? item.articleCount + ' 篇文章' : '暂无文章'
              }}</span>
              <v-icon size="18" class="category-arrow">mdi-chevron-right</v-icon>
            </div>
          </div>
        </div>
      </div>
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <v-icon size="64" color="grey-lighten-1">mdi-folder-off-outline</v-icon>
        <p>暂无分类数据</p>
      </div>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { listCategory, type Category } from '@/apis/category'
import usePageInfoStore from '@/stores/modules/pageInfo.ts'
import { useSnackbarStore } from '@/stores/modules/snackbar.ts'

const pageInfoStore = usePageInfoStore()
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore)

const router = useRouter()
const snackbarStore = useSnackbarStore()

const categoryList = ref<Category[]>([])
const loading = ref(true)

onMounted(() => {
  listCategory()
    .then((res) => {
      categoryList.value = res.data
    })
    .catch(() => {
      snackbarStore.error('获取分类列表失败')
    })
    .finally(() => {
      loading.value = false
    })
})

function goToCategory(id: number) {
  router.push('/category/' + id).then(() => {
    window.scrollTo(0, 0)
  })
}
</script>

<style scoped>
.category-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px dashed color-mix(in srgb, var(--color-primary) 50%, transparent);
}

.stat-icon {
  color: var(--color-primary);
  margin-right: 10px;
}

.stat-text {
  font-size: 16px;
  color: var(--color-text-secondary);
}

.stat-text strong {
  color: var(--color-primary);
  font-size: 20px;
  margin: 0 4px;
}

.category-list {
  padding: 8px 0;
}

.skeleton-item {
  margin: 4px 0;
}

.category-item {
  --delay: 0s;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  cursor: pointer;
  border-bottom: 1px dashed var(--color-border, #e5e5e5);
  transition: background-color 0.2s ease;
  animation: fadeIn 0.4s ease forwards;
  animation-delay: var(--delay);
  opacity: 0;
}

.category-item:last-child {
  border-bottom: none;
}

.category-item:hover {
  background-color: var(--color-bg-hover, #f5f5f5);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.category-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  flex: 1;
  overflow: hidden;
  transition: transform 0.3s ease-in-out;
}

.category-item:hover .category-left {
  transform: translateX(8px);
}

.category-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--color-primary, #1867c0);
  flex-shrink: 0;
}

.category-name {
  font-size: 15px;
  color: var(--color-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s ease-in-out;
}

.category-item:hover .category-name {
  color: var(--color-primary);
}

.category-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  margin-left: 16px;
}

.category-count {
  font-size: 13px;
  color: var(--color-text-secondary, #666);
}

.category-arrow {
  color: var(--color-text-tertiary, #999);
  opacity: 0;
  transform: translateX(-4px);
  transition:
    opacity 0.2s ease,
    transform 0.2s ease;
}

.category-item:hover .category-arrow {
  opacity: 1;
  transform: translateX(0);
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

@media (max-width: 759px) {
  .category-header {
    padding-bottom: 16px;
    margin-bottom: 20px;
  }

  .stat-text {
    font-size: 14px;
  }

  .stat-text strong {
    font-size: 18px;
  }

  .category-item {
    padding: 14px 16px;
  }

  .category-name {
    font-size: 14px;
  }

  .category-count {
    font-size: 12px;
  }
}
</style>

<style>
/* 夜间模式样式 */
.v-theme--dark .category-item {
  border-bottom-color: var(--color-border, #333);
}

.v-theme--dark .category-item:hover {
  background-color: var(--color-bg-hover, #2a2a2a);
}

.v-theme--dark .category-name {
  color: var(--color-text-primary, #e5e5e5);
}

.v-theme--dark .category-count {
  color: var(--color-text-secondary, #999);
}

.v-theme--dark .empty-state {
  color: var(--color-text-tertiary, #666);
}
</style>
