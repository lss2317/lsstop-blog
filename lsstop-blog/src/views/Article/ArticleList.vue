<template>
  <div>
    <!-- 标签或分类名 -->
    <div class="banner" :style="cover">
      <h1 class="banner-title animated fadeInDown">{{ title }} - {{ name }}</h1>
    </div>
    <div class="article-list-wrapper">
      <!-- 加载骨架屏 -->
      <v-row v-if="loading">
        <v-col md="4" cols="12" v-for="n in 6" :key="n">
          <v-skeleton-loader class="article-item-card" type="image, article" :elevation="2" />
        </v-col>
      </v-row>
      <!-- 文章列表 -->
      <v-row v-else-if="articleList.length > 0">
        <v-col
          md="4"
          cols="12"
          v-for="(item, index) of articleList"
          :key="item.id"
          class="article-col"
          :style="{ '--delay': index * 0.1 + 's' }"
        >
          <v-card class="animated zoomIn article-item-card">
            <div class="article-item-cover" @click="navigateToArticle(item.id)">
              <a>
                <v-img class="on-hover" width="100%" height="220" :src="item.articleCover" cover />
              </a>
            </div>
            <div class="article-item-info">
              <div class="article-title">
                <a @click="navigateToArticle(item.id)">
                  {{ item.articleTitle }}
                </a>
              </div>
              <div class="article-meta">
                <v-icon size="20" class="meta-icon">mdi-clock-outline</v-icon>
                {{ formatTime(item.createTime) }}
                <a class="category-link" @click="goToCategory(item.categoryId)">
                  <v-icon>mdi-bookmark</v-icon>
                  <span class="category-name">{{ item.categoryName }}</span>
                </a>
              </div>
            </div>
            <div class="divider-line"></div>
            <div class="tag-wrapper">
              <a class="tag-btn" v-for="tag of item.tags" :key="tag.id" @click="goToTag(tag.id)">
                {{ tag.tagName }}
              </a>
            </div>
          </v-card>
        </v-col>
      </v-row>
      <!-- 空状态 -->
      <v-card v-else class="blog-container">
        <div class="empty-state">
          <v-icon size="48" color="grey">mdi-file-document-outline</v-icon>
          <p>暂无文章</p>
        </div>
      </v-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import usePageInfoStore from '@/stores/modules/pageInfo'
import { useSnackbarStore } from '@/stores/modules/snackbar'
import { getArticleListByCategory, getArticleListByTag, type ArticleList } from '@/apis/article'
import { listCategory } from '@/apis/category'
import { listTag } from '@/apis/tag'
import { formatTime } from '@/utils/date'
import { useNavigate } from '@/composables/useNavigate'

const route = useRoute()
const router = useRouter()
const { navigateToArticle } = useNavigate()
const pageInfoStore = usePageInfoStore()
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore)
const snackbarStore = useSnackbarStore()

const articleList = ref<ArticleList[]>([])
const loading = ref(true)
const name = ref('')

/** 是否为分类页面 */
const isCategory = computed(() => route.name === 'CategoryArticles')

/** 页面标题 */
const title = computed(() => (isCategory.value ? '分类' : '标签'))

/** 获取分类或标签名称 */
async function fetchName() {
  if (isCategory.value) {
    const categoryId = Number(route.params.categoryId)
    const res = await listCategory()
    const category = res.data.find((item) => item.id === categoryId)
    name.value = category?.categoryName || ''
  } else {
    const tagId = Number(route.params.tagId)
    const res = await listTag()
    const tag = res.data.find((item) => item.id === tagId)
    name.value = tag?.tagName || ''
  }
}

/** 获取文章列表 */
async function fetchArticleList() {
  loading.value = true
  try {
    if (isCategory.value) {
      const categoryId = Number(route.params.categoryId)
      if (!categoryId) {
        loading.value = false
        return
      }
      const res = await getArticleListByCategory(categoryId)
      articleList.value = res.data
    } else {
      const tagId = Number(route.params.tagId)
      if (!tagId) {
        loading.value = false
        return
      }
      const res = await getArticleListByTag(tagId)
      articleList.value = res.data
    }
  } catch {
    snackbarStore.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchName()
  fetchArticleList()
})

// 跳转到分类页
function goToCategory(categoryId: number) {
  if (isCategory.value && Number(route.params.categoryId) === categoryId) return
  router.push('/category/' + categoryId).then(() => {
    window.scrollTo(0, 0)
  })
}

// 跳转到标签页
function goToTag(tagId: number) {
  if (!isCategory.value && Number(route.params.tagId) === tagId) return
  router.push('/tag/' + tagId).then(() => {
    window.scrollTo(0, 0)
  })
}
</script>

<style scoped>
.article-col {
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

@media (min-width: 760px) {
  .article-list-wrapper {
    max-width: 1106px;
    margin: 420px auto 1rem auto;
  }

  .article-item-card:hover {
    transition: all 0.3s;
    box-shadow: 0 4px 12px 12px rgba(7, 17, 27, 0.15);
  }

  .article-item-card:not(:hover) {
    transition: all 0.3s;
  }

  .article-item-card:hover .on-hover {
    transition: all 0.6s;
    transform: scale(1.1);
  }

  .article-item-card:not(:hover) .on-hover {
    transition: all 0.6s;
  }

  .article-item-info {
    line-height: 1.7;
    padding: 15px 15px 12px 18px;
    font-size: 15px;
  }
}

@media (max-width: 759px) {
  .article-list-wrapper {
    margin-top: 280px;
    padding: 0 12px;
  }

  .article-item-info {
    line-height: 1.7;
    padding: 15px 15px 12px 18px;
  }
}

.article-item-card {
  border-radius: 8px !important;
  box-shadow: 0 4px 8px 6px rgba(7, 17, 27, 0.06);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.article-item-card a {
  cursor: pointer;
}

.article-item-cover {
  height: 220px;
  overflow: hidden;
}

.article-item-card a:hover {
  color: #8e8cd8;
}

.article-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-meta {
  margin-top: 0.375rem;
  display: flex;
  align-items: center;
}

.meta-icon {
  margin-right: 4px;
}

.category-link {
  margin-left: auto;
  display: flex;
  align-items: center;
  max-width: 120px;
}

.category-link:hover {
  color: #8e8cd8;
}

.category-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.divider-line {
  border-top: 1px solid rgba(0, 0, 0, 0.12);
}

.tag-wrapper {
  padding: 10px 15px 10px 18px;
  min-height: 42px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  margin-top: auto;
  display: flex;
  align-items: center;
}

.tag-wrapper a {
  color: #fff !important;
}

.tag-btn {
  display: inline-block;
  font-size: 0.725rem;
  line-height: 22px;
  height: 22px;
  border-radius: 10px;
  padding: 0 12px !important;
  background: linear-gradient(to right, #bf4643 0%, #6c9d8f 100%);
  opacity: 0.6;
  margin-right: 0.5rem;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: opacity 0.3s;
}

.tag-btn:hover {
  opacity: 1;
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
.v-theme--dark .article-item-card {
  background: var(--color-bg-light);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.v-theme--dark .article-item-card a {
  color: var(--color-text-primary);
}

.v-theme--dark .category-link {
  color: var(--color-text-secondary);
}

.v-theme--dark .divider-line {
  border-top-color: #444;
}

.v-theme--dark .tag-wrapper {
  background: transparent;
}

.v-theme--dark .tag-btn {
  opacity: 0.85;
  background: linear-gradient(to right, #e05550 0%, #5fb3a3 100%);
}

.v-theme--dark .tag-btn:hover {
  opacity: 1;
}

.v-theme--dark .empty-state {
  color: var(--color-text-tertiary, #666);
}
</style>
