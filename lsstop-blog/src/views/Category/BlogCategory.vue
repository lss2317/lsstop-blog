<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">分类</h1>
    </div>
    <!-- 分类内容 -->
    <v-card class="blog-container">
      <!-- 加载骨架屏 -->
      <v-row v-if="loading">
        <v-col md="4" sm="6" cols="12" v-for="n in 6" :key="n">
          <v-skeleton-loader type="image" height="200" class="skeleton-category" />
        </v-col>
      </v-row>
      <!-- 分类列表 -->
      <v-row v-else-if="categoryList.length > 0">
        <v-col
          md="4"
          sm="6"
          cols="12"
          v-for="(item, index) of categoryList"
          :key="item.id"
          class="category-col"
          :style="{ '--delay': index * 0.1 + 's' }"
        >
          <a class="category-item" @click="goToCategory(item.id)">
            <img class="category-cover" :src="item.categoryCover" :alt="item.categoryName" />
            <div class="category-overlay">
              <div class="category-info">
                <h3 class="category-name">{{ item.categoryName }}</h3>
                <span
                  class="category-count"
                  :class="{ 'category-count-empty': item.articleCount === 0 }"
                >
                  {{ item.articleCount ? item.articleCount + ' 篇文章' : '暂无文章' }}
                </span>
              </div>
            </div>
          </a>
        </v-col>
      </v-row>
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
.category-col {
  --delay: 0s;
  animation: fadeInUp 0.5s ease forwards;
  animation-delay: var(--delay);
  opacity: 0;
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

.skeleton-category {
  border-radius: var(--radius-lg);
}

.category-item {
  display: block;
  position: relative;
  overflow: hidden;
  border-radius: var(--radius-lg);
  text-decoration: none;
  box-shadow: var(--shadow-card);
  transition: all 0.3s ease;
  cursor: pointer;
}

.category-item:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-card-hover);
}

.category-cover {
  display: block;
  width: 100%;
  height: 200px;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.category-item:hover .category-cover {
  transform: scale(1.1);
}

.category-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1) 0%, rgba(0, 0, 0, 0.6) 100%);
  display: flex;
  align-items: flex-end;
  padding: 20px;
  transition: background 0.3s ease;
}

.category-item:hover .category-overlay {
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.2) 0%, rgba(0, 0, 0, 0.7) 100%);
}

.category-info {
  color: #fff;
  width: 100%;
}

.category-name {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0 0 6px 0;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-count {
  font-size: 0.875rem;
  opacity: 0.9;
}

.category-count-empty {
  opacity: 0.7;
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
</style>

<style>
/* 夜间模式样式 */
.v-theme--dark .category-item {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.v-theme--dark .empty-state {
  color: var(--color-text-tertiary, #666);
}
</style>
