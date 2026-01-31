<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">归档</h1>
    </div>
    <!-- 归档内容 -->
    <v-card class="blog-container">
      <!-- 文章统计 -->
      <div v-if="!loading" class="archive-header">
        <v-icon class="archive-header-icon" size="28">mdi-archive-outline</v-icon>
        <span class="archive-header-text"
          >共计 <strong>{{ totalCount }}</strong> 篇文章</span
        >
      </div>

      <!-- 加载骨架屏 -->
      <div v-if="loading" class="timeline-skeleton">
        <div v-for="n in 3" :key="n" class="skeleton-year-group">
          <v-skeleton-loader type="chip" width="80" class="mb-4" />
          <div v-for="m in 4" :key="m" class="skeleton-item">
            <v-skeleton-loader type="text" width="60%" />
          </div>
        </div>
      </div>

      <!-- 时间线 -->
      <div v-else-if="groupedArchives.length > 0" class="timeline">
        <div
          v-for="(yearGroup, yearIndex) in groupedArchives"
          :key="yearGroup.year"
          class="timeline-year"
          :style="{ '--year-delay': yearIndex * 0.15 + 's' }"
        >
          <!-- 年份标题 -->
          <div class="year-header">
            <span class="year-dot"></span>
            <span class="year-title">{{ yearGroup.year }}</span>
            <span class="year-count">{{ yearGroup.count }} 篇</span>
          </div>

          <!-- 月份分组 -->
          <div
            v-for="(monthGroup, monthIndex) in yearGroup.months"
            :key="monthGroup.month"
            class="timeline-month"
            :style="{ '--month-delay': yearIndex * 0.15 + monthIndex * 0.08 + 's' }"
          >
            <!-- 月份标签 -->
            <div class="month-header">
              <span class="month-label">{{ monthGroup.month }}月</span>
            </div>

            <!-- 文章列表 -->
            <div class="article-list">
              <a
                v-for="(article, articleIndex) in monthGroup.articles"
                :key="article.id"
                class="article-item"
                :style="{
                  '--article-delay':
                    yearIndex * 0.15 + monthIndex * 0.08 + articleIndex * 0.05 + 's',
                }"
                @click="navigateToArticle(article.id)"
              >
                <span class="article-dot"></span>
                <span class="article-title">{{ article.articleTitle }}</span>
                <span class="article-date">{{ dateFormat.monthDay(article.createTime) }}</span>
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <v-icon size="64" color="grey-lighten-1">mdi-archive-off-outline</v-icon>
        <p>暂无归档数据</p>
      </div>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { listArchives, type ArticleArchive } from '@/apis/archive'
import usePageInfoStore from '@/stores/modules/pageInfo.ts'
import { useSnackbarStore } from '@/stores/modules/snackbar.ts'
import { dateFormat, getYear, getMonth } from '@/utils/date'
import { useNavigate } from '@/composables/useNavigate'

const pageInfoStore = usePageInfoStore()
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore)

const { navigateToArticle } = useNavigate()
const snackbarStore = useSnackbarStore()

const archives = ref<ArticleArchive[]>([])
const totalCount = ref(0)
const loading = ref(true)

/** 按年月分组的归档数据 */
interface MonthGroup {
  month: number
  articles: ArticleArchive[]
}

interface YearGroup {
  year: number
  count: number
  months: MonthGroup[]
}

/** 将归档数据按年月分组 */
const groupedArchives = computed<YearGroup[]>(() => {
  const groups: Map<number, Map<number, ArticleArchive[]>> = new Map()

  archives.value.forEach((article) => {
    const year = getYear(article.createTime)
    const month = getMonth(article.createTime)

    if (!groups.has(year)) {
      groups.set(year, new Map())
    }
    const yearMap = groups.get(year)!
    if (!yearMap.has(month)) {
      yearMap.set(month, [])
    }
    yearMap.get(month)!.push(article)
  })

  // 转换为数组并排序（年份降序，月份降序）
  const result: YearGroup[] = []
  const sortedYears = Array.from(groups.keys()).sort((a, b) => b - a)

  sortedYears.forEach((year) => {
    const yearMap = groups.get(year)!
    const sortedMonths = Array.from(yearMap.keys()).sort((a, b) => b - a)
    const months: MonthGroup[] = sortedMonths.map((month) => ({
      month,
      articles: yearMap.get(month)!,
    }))
    const count = months.reduce((sum, m) => sum + m.articles.length, 0)
    result.push({ year, count, months })
  })

  return result
})

onMounted(() => {
  listArchives()
    .then((res) => {
      archives.value = res.data ?? []
      totalCount.value = archives.value.length
    })
    .catch(() => {
      snackbarStore.error('获取归档列表失败')
    })
    .finally(() => {
      loading.value = false
    })
})
</script>

<style scoped>
.archive-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px dashed var(--color-border);
}

.archive-header-icon {
  color: var(--color-primary);
  margin-right: 10px;
}

.archive-header-text {
  font-size: 16px;
  color: var(--color-text-secondary);
}

.archive-header-text strong {
  color: var(--color-primary);
  font-size: 20px;
  margin: 0 4px;
}

.timeline {
  position: relative;
  padding-left: 20px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 8px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(
    to bottom,
    rgba(73, 177, 245, 0.4) 0%,
    rgba(73, 177, 245, 0.4) 50%,
    transparent 100%
  );
}

.timeline-year {
  --year-delay: 0s;
  animation: fadeInUp 0.5s ease forwards;
  animation-delay: var(--year-delay);
  opacity: 0;
  margin-bottom: 30px;
}

.timeline-year:last-child {
  margin-bottom: 0;
}

.year-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  margin-left: -20px;
}

.year-dot {
  width: 18px;
  height: 18px;
  background: var(--color-primary);
  border-radius: 50%;
  border: 3px solid var(--color-bg-white);
  box-shadow: 0 0 0 2px var(--color-primary);
  z-index: 1;
}

.year-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-left: 14px;
}

.year-count {
  font-size: 13px;
  color: var(--color-text-tertiary);
  margin-left: 10px;
  padding: 2px 8px;
  background: var(--color-bg-light);
  border-radius: var(--radius-round);
}

.timeline-month {
  --month-delay: 0s;
  animation: fadeInUp 0.4s ease forwards;
  animation-delay: var(--month-delay);
  opacity: 0;
  margin-bottom: 20px;
  margin-left: 10px;
}

.month-header {
  margin-bottom: 10px;
}

.month-label {
  display: inline-block;
  font-size: 15px;
  font-weight: 600;
  color: var(--color-primary);
  padding: 4px 12px;
  background: rgba(73, 177, 245, 0.1);
  border-radius: var(--radius-round);
}

.article-list {
  display: flex;
  flex-direction: column;
}

.article-item {
  --article-delay: 0s;
  animation: fadeInLeft 0.3s ease forwards;
  animation-delay: var(--article-delay);
  opacity: 0;
  display: flex;
  align-items: center;
  padding: 14px 0;
  margin-left: 10px;
  border-bottom: 1px dashed var(--color-border-light);
  text-decoration: none;
  color: var(--color-text-primary);
  transition: padding 0.3s ease;
  cursor: pointer;
}

.article-item:last-child {
  border-bottom: none;
}

.article-item:hover {
  padding-left: 8px;
}

.article-item:hover .article-title {
  color: var(--color-primary);
}

.article-dot {
  width: 6px;
  height: 6px;
  background: var(--color-text-tertiary);
  border-radius: 50%;
  margin-right: 12px;
  flex-shrink: 0;
  transition: transform 0.3s ease;
}

.article-item:hover .article-dot {
  background: var(--color-primary);
  transform: scale(1.4);
}

.article-title {
  flex: 1;
  font-size: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-date {
  font-size: 13px;
  color: var(--color-text-tertiary);
  margin-left: 16px;
  flex-shrink: 0;
}

.timeline-skeleton {
  padding-left: 20px;
}

.skeleton-year-group {
  margin-bottom: 30px;
}

.skeleton-item {
  margin: 12px 0 12px 30px;
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

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@media (max-width: 759px) {
  .timeline {
    padding-left: 16px;
  }

  .timeline::before {
    left: 6px;
  }

  .year-header {
    margin-left: -16px;
  }

  .year-dot {
    width: 14px;
    height: 14px;
  }

  .year-title {
    font-size: 18px;
    margin-left: 10px;
  }

  .timeline-month {
    margin-left: 6px;
  }

  .article-item {
    margin-left: 6px;
    padding: 12px 0;
  }

  .article-title {
    font-size: 14px;
  }

  .article-date {
    font-size: 12px;
  }
}
</style>

<style>
/* 夜间模式样式 */
.v-theme--dark .archive-header {
  border-bottom-color: var(--color-border);
}

.v-theme--dark .year-dot {
  border-color: var(--color-bg-white);
}

.v-theme--dark .year-count {
  background: var(--color-bg-light);
}

.v-theme--dark .month-label {
  background: rgba(73, 177, 245, 0.2);
}

.v-theme--dark .article-item {
  border-bottom-color: var(--color-border);
}

.v-theme--dark .article-date {
  color: var(--color-text-tertiary);
}

.v-theme--dark .empty-state {
  color: var(--color-text-tertiary, #666);
}
</style>
