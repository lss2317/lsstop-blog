<template>
  <div>
    <!-- banner -->
    <div class="tag-banner banner" :style="cover">
      <h1 class="banner-title">标签</h1>
    </div>
    <!-- 标签列表 -->
    <v-card class="blog-container">
      <!-- 加载骨架屏 -->
      <v-row v-if="loading" class="tag-container">
        <v-col md="3" sm="4" cols="6" v-for="n in 12" :key="n">
          <div class="tag-wrapper skeleton-wrapper">
            <v-skeleton-loader type="avatar" width="24" height="24" class="mb-2" />
            <v-skeleton-loader type="text" width="60%" />
            <v-skeleton-loader type="text" width="40%" class="mt-2" />
          </div>
        </v-col>
      </v-row>
      <!-- 标签列表 -->
      <v-row v-else-if="tagList.length > 0" class="tag-container">
        <v-col
          md="3"
          sm="4"
          cols="6"
          v-for="(item, index) of tagList"
          :key="item.id"
          class="tag-col"
          :style="{ '--delay': index * 0.05 + 's' }"
        >
          <a class="tag-wrapper" @click="goToTag(item.id)">
            <v-icon class="tag-icon" size="24">mdi-tag-outline</v-icon>
            <div class="tag-name">{{ item.tagName }}</div>
            <div class="tag-count" :class="{ 'tag-count-empty': item.articleCount === 0 }">
              {{ item.articleCount > 0 ? item.articleCount + ' 篇文章' : '暂无文章' }}
            </div>
          </a>
        </v-col>
      </v-row>
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <v-icon size="64" color="grey-lighten-1">mdi-tag-off-outline</v-icon>
        <p>暂无标签数据</p>
      </div>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { listTag, type Tag } from '@/apis/tag'
import usePageInfoStore from '@/stores/modules/pageInfo.ts'
import { useSnackbarStore } from '@/stores/modules/snackbar.ts'

const pageInfoStore = usePageInfoStore()
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore)

const router = useRouter()
const snackbarStore = useSnackbarStore()

const tagList = ref<Tag[]>([])
const loading = ref(true)

onMounted(() => {
  listTag()
    .then((res) => {
      tagList.value = res.data
    })
    .catch(() => {
      snackbarStore.error('获取标签列表失败')
    })
    .finally(() => {
      loading.value = false
    })
})

function goToTag(id: number) {
  router.push('/tag/' + id).then(() => {
    window.scrollTo(0, 0)
  })
}
</script>

<style scoped>
.tag-container {
  margin: 10px 0 0;
}

.tag-col {
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

.tag-wrapper {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 15px;
  margin-bottom: 16px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  text-decoration: none;
  color: var(--color-text-primary);
  transition: all 0.3s ease;
  min-height: 100px;
}

.tag-wrapper:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card-hover);
}

.tag-wrapper:hover .tag-icon,
.tag-wrapper:hover .tag-name {
  color: var(--color-primary);
}

.tag-icon {
  color: var(--color-text-tertiary);
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.tag-wrapper:hover .tag-icon {
  color: var(--color-primary);
  transform: scale(1.1);
}

.tag-name {
  font-size: 16px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 6px;
  transition: all 0.3s ease;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag-count {
  font-size: 13px;
  color: var(--color-text-secondary);
  transition: color 0.3s ease;
}

.tag-count-empty {
  color: var(--color-text-tertiary, #bbb);
}

/* 骨架屏样式 */
.skeleton-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
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
.v-theme--dark .tag-wrapper {
  background: var(--color-bg-light);
  border-color: rgba(255, 255, 255, 0.1);
  color: var(--color-text-primary);
}

.v-theme--dark .tag-wrapper:hover {
  box-shadow: var(--shadow-card-hover);
}

.v-theme--dark .tag-count {
  color: var(--color-text-secondary);
}

.v-theme--dark .tag-count-empty {
  color: var(--color-text-tertiary, #666);
}

.v-theme--dark .empty-state {
  color: var(--color-text-tertiary, #666);
}
</style>
