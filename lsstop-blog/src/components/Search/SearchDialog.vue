<template>
  <!-- 搜索弹窗 -->
  <v-dialog
    v-model="dialogVisible"
    width="50vw"
    min-width="400"
    max-width="800"
    content-class="search-dialog-content"
    :scrim-opacity="0.6"
  >
    <v-card class="search-card" rounded="xl">
      <!-- 标题栏 -->
      <div class="search-title-bar">
        <span class="dialog-title">搜索</span>
        <v-btn icon variant="text" size="small" @click="dialogVisible = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </div>

      <!-- 搜索输入框 -->
      <div class="search-input-wrapper">
        <div class="search-input-box">
          <i class="iconfont iconsousuo search-icon" />
          <input
            ref="inputRef"
            v-model="searchValue"
            class="search-input"
            :placeholder="searchMode === 'title' ? '请输入搜索内容' : '回车进行内容搜索'"
            @keyup.enter="handleContentSearch"
            @focus="handleFocus"
          />
          <div class="search-mode-toggle">
            <span
              class="mode-btn"
              :class="{ active: searchMode === 'title' }"
              @click="searchMode = 'title'"
            >
              标题
            </span>
            <span
              class="mode-btn"
              :class="{ active: searchMode === 'content' }"
              @click="searchMode = 'content'"
            >
              内容
            </span>
          </div>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="search-content">
        <!-- 搜索历史和热门推荐 -->
        <template v-if="showHistory">
          <!-- 搜索历史 -->
          <div class="section-header">
            <span class="section-title">搜索历史</span>
            <span class="section-action" @click="clearHistory">
              <v-icon size="16">mdi-delete-outline</v-icon>
              清除记录
            </span>
          </div>
          <div class="history-tags">
            <span
              v-for="item in historyList"
              :key="item"
              class="history-tag"
              @click="historySearch(item)"
            >
              {{ item }}
            </span>
            <span v-if="historyList.length === 0" class="empty-text">暂无搜索记录</span>
          </div>
        </template>

        <!-- 搜索结果 -->
        <template v-else>
          <div v-if="loading" class="loading-container">
            <v-progress-circular indeterminate color="primary" />
          </div>
          <div v-else-if="searchResults.length === 0" class="empty-container">
            <span class="empty-text">
              {{ searchMode === 'title' ? '输入关键词搜索文章' : '内容搜索每分钟限5次' }}
            </span>
          </div>
          <div v-else class="result-list">
            <div
              v-for="item in searchResults"
              :key="item.id"
              class="result-item"
              @click="goToArticle(item.id, true)"
            >
              <div class="result-info">
                <div class="result-title" v-html="item.highlightedTitle || item.articleTitle" />
                <div class="result-meta">
                  <span class="result-category">{{ item.categoryName }}</span>
                  <span
                    v-if="item.highlightedContent"
                    class="result-content"
                    v-html="item.highlightedContent"
                  />
                </div>
              </div>
              <div class="result-count">
                <v-icon size="14" color="orange">mdi-fire</v-icon>
                {{ item.viewCount }}
              </div>
            </div>
          </div>
        </template>
      </div>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import {
  searchArticleByTitle,
  searchArticleByContent,
  type ArticleSearchItem,
} from '@/apis/article';
import { useSnackbarStore } from '@/stores/modules/snackbar';
import { useLocalStorage } from '@/composables/useLocalStorage';

const router = useRouter();
const snackbar = useSnackbarStore();

// 弹窗状态
const dialogVisible = defineModel<boolean>({ default: false });

// 搜索相关
const searchValue = ref('');
const searchMode = ref<'title' | 'content'>('title');
const loading = ref(false);
const showHistory = ref(true);
const inputRef = ref<HTMLInputElement | null>(null);

// 搜索历史（响应式本地存储）
const historyList = useLocalStorage<string[]>('searchHistory', []);

// 搜索结果
interface SearchResult extends ArticleSearchItem {
  highlightedTitle?: string;
  highlightedContent?: string;
}
const searchResults = ref<SearchResult[]>([]);

// 高亮关键词
function highlightKeyword(text: string, keyword: string) {
  if (!keyword) return text;
  const regex = new RegExp(`(${keyword})`, 'gi');
  return text.replace(regex, '<span class="highlight">$1</span>');
}

// 标题搜索（实时）
async function searchByTitle() {
  if (!searchValue.value.trim()) {
    searchResults.value = [];
    return;
  }

  try {
    const res = await searchArticleByTitle(searchValue.value);
    searchResults.value = (res.data || []).map((item) => ({
      ...item,
      highlightedTitle: highlightKeyword(item.articleTitle, searchValue.value),
    }));
  } catch {
    searchResults.value = [];
  }
}

// 内容搜索（回车触发）
async function handleContentSearch() {
  if (searchMode.value !== 'content' || !searchValue.value.trim()) return;

  // 添加到搜索历史
  addToHistory(searchValue.value);

  loading.value = true;
  try {
    const res = await searchArticleByContent(searchValue.value);
    if (!res.data) {
      snackbar.warning('搜索请求过于频繁，请稍后再试');
      return;
    }
    searchResults.value = (res.data || []).map((item) => ({
      ...item,
      highlightedContent: item.articleContent
        ? highlightKeyword(item.articleContent, searchValue.value)
        : undefined,
    }));
  } catch {
    snackbar.error('搜索失败，请稍后再试');
  } finally {
    loading.value = false;
  }
}

// 输入框获得焦点
function handleFocus() {
  showHistory.value = !searchValue.value;
}

// 添加搜索历史
function addToHistory(value: string) {
  const trimmed = value.trim();
  if (!trimmed) return;
  // 去重并添加到开头
  historyList.value = [trimmed, ...historyList.value.filter((h) => h !== trimmed)].slice(0, 10);
}

// 清除搜索历史
function clearHistory() {
  historyList.value = [];
}

// 点击历史记录搜索
function historySearch(value: string) {
  searchValue.value = value;
  showHistory.value = false;
  if (searchMode.value === 'title') {
    searchByTitle();
  } else {
    handleContentSearch();
  }
}

// 跳转文章详情
function goToArticle(id: number, saveHistory = false) {
  if (saveHistory && searchValue.value) {
    addToHistory(searchValue.value);
  }
  dialogVisible.value = false;
  router.push(`/article/${id}`);
}

// 监听搜索值变化（标题搜索实时响应）
watch(searchValue, (val) => {
  showHistory.value = !val;
  if (searchMode.value === 'title' && val) {
    searchByTitle();
  } else if (!val) {
    searchResults.value = [];
  }
});

// 监听搜索模式变化
watch(searchMode, () => {
  searchResults.value = [];
  searchValue.value = '';
  showHistory.value = true;
});

// 监听弹窗打开
watch(dialogVisible, (val) => {
  if (val) {
    searchValue.value = '';
    searchResults.value = [];
    showHistory.value = true;
  }
});
</script>

<style scoped>
.search-card {
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 标题栏 */
.search-title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px 12px;
}

.dialog-title {
  font-size: 18px;
  font-weight: 600;
}

/* 搜索输入框 */
.search-input-wrapper {
  padding: 0 20px 16px;
}

.search-input-box {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 8px;
  padding: 0 12px;
  height: 44px;
}

.v-theme--dark .search-input-box {
  background: #2d2d2d;
}

.search-icon {
  font-size: 18px;
  color: #4caf50;
  margin-right: 8px;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  color: inherit;
}

.search-input::placeholder {
  color: #999;
}

/* 标题/内容切换 */
.search-mode-toggle {
  display: flex;
  align-items: center;
  background: #e8e8e8;
  border-radius: 16px;
  padding: 2px;
}

.v-theme--dark .search-mode-toggle {
  background: #3d3d3d;
}

.mode-btn {
  padding: 4px 12px;
  font-size: 12px;
  border-radius: 14px;
  cursor: pointer;
  color: #666;
  transition: all 0.2s;
}

.v-theme--dark .mode-btn {
  color: #aaa;
}

.mode-btn.active {
  background: #ff6b6b;
  color: #fff;
}

/* 内容区域 */
.search-content {
  padding: 0 20px 20px;
  flex: 1;
  overflow-y: auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  margin-top: 16px;
}

.section-header:first-child {
  margin-top: 0;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
}

.section-action {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

.section-action:hover {
  color: #ff6b6b;
}

.section-action :deep(.v-icon) {
  margin-right: 2px;
}

/* 搜索历史标签 */
.history-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.history-tag {
  padding: 6px 14px;
  background: #49b1f5;
  color: #fff;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.history-tag:hover {
  background: #3da1e5;
  transform: scale(1.02);
}

/* 加载和空状态 */
.loading-container,
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
}

.empty-text {
  font-size: 13px;
  color: #999;
}

/* 搜索结果 */
.result-list {
  display: flex;
  flex-direction: column;
}

.result-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.result-item:hover {
  background-color: #f5f5f5;
}

.v-theme--dark .result-item:hover {
  background-color: #2d2d2d;
}

.result-info {
  flex: 1;
  overflow: hidden;
}

.result-title {
  font-size: 14px;
  margin-bottom: 6px;
}

.result-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #999;
}

.result-category {
  padding: 2px 8px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 4px;
  font-size: 11px;
}

.v-theme--dark .result-category {
  background: #1e3a5f;
  color: #64b5f6;
}

.result-content {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 300px;
}

.result-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.result-count :deep(.v-icon) {
  margin-right: 2px;
}

/* 关键词高亮 */
:deep(.highlight) {
  background-color: #fff3cd;
  color: #856404;
  padding: 0 2px;
  border-radius: 2px;
}

.v-theme--dark :deep(.highlight) {
  background-color: #664d03;
  color: #ffc107;
}
</style>

<style>
.search-dialog-content {
  height: 65vh !important;
}
</style>
