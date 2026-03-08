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
            maxlength="50"
            :placeholder="searchMode === 'title' ? '请输入搜索内容' : '回车进行内容搜索'"
            @keyup.enter="handleContentSearch"
            @focus="handleFocus"
            @compositionstart="isComposing = true"
            @compositionend="handleCompositionEnd"
          />
          <div class="search-mode-toggle">
            <div class="mode-slider" :class="{ 'slide-right': searchMode === 'content' }" />
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
            <span v-if="historyList.length > 0" class="section-action" @click="clearHistory">
              <v-icon size="16">mdi-trash-can-outline</v-icon>
              清除全部
            </span>
          </div>
          <div class="history-list">
            <div
              v-for="item in historyList"
              :key="item"
              class="history-item"
              @click="historySearch(item)"
            >
              <i class="iconfont iconlishi history-icon" />
              <span class="history-text">{{ item }}</span>
              <v-icon class="history-delete" size="18" @click.stop="removeHistory(item)">
                mdi-close
              </v-icon>
            </div>
            <div v-if="historyList.length === 0" class="empty-container">
              <span class="empty-text">暂无搜索记录</span>
            </div>
          </div>
        </template>

        <!-- 搜索结果 -->
        <template v-else>
          <LoadingSpinner v-if="loading" />
          <div v-else-if="searchResults.length === 0" class="empty-container">
            <span class="empty-text">
              {{
                searchMode === 'title'
                  ? searchValue.trim()
                    ? '未找到相关文章'
                    : '输入关键词搜索文章'
                  : hasSearched
                    ? '未找到相关文章'
                    : '按回车搜索内容'
              }}
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
import {
  searchArticleByTitle,
  searchArticleByContent,
  type ArticleSearchItem,
} from '@/apis/article';
import { useSnackbarStore } from '@/stores/modules/snackbar';
import { useLocalStorage } from '@/composables/useLocalStorage';
import { getErrorMessage } from '@/utils/error';
import { highlightKeyword, extractKeywordContext } from '@/utils/format';
import { stripMarkdown } from '@/utils/markdown';
import { useNavigate } from '@/composables/useNavigate';
import LoadingSpinner from '@/components/Loading/LoadingSpinner.vue';

const { navigateToArticle } = useNavigate();
const snackbar = useSnackbarStore();

// 弹窗状态
const dialogVisible = defineModel<boolean>({ default: false });

// 搜索相关
const searchValue = ref('');
const searchMode = ref<'title' | 'content'>('title');
const loading = ref(false);
const showHistory = ref(true);
const inputRef = ref<HTMLInputElement | null>(null);
const hasSearched = ref(false); // 内容搜索是否已执行

// 防抖和竞态控制
const isComposing = ref(false); // 中文输入法组合状态
let debounceTimer: ReturnType<typeof setTimeout> | null = null;
let searchVersion = 0; // 请求版本号，用于处理竞态

// 搜索历史（响应式本地存储）
const historyList = useLocalStorage<string[]>('searchHistory', []);

// 搜索结果
interface SearchResult extends ArticleSearchItem {
  highlightedTitle?: string;
  highlightedContent?: string;
}
const searchResults = ref<SearchResult[]>([]);

// 标题搜索（实时，带防抖和竞态处理）
function debouncedSearchByTitle() {
  // 清除之前的防抖定时器
  if (debounceTimer) {
    clearTimeout(debounceTimer);
  }

  debounceTimer = setTimeout(() => {
    searchByTitle();
  }, 300);
}

async function searchByTitle() {
  const keyword = searchValue.value.trim();
  if (!keyword) {
    searchResults.value = [];
    return;
  }

  // 递增版本号，用于处理竞态
  const currentVersion = ++searchVersion;

  try {
    const res = await searchArticleByTitle(keyword);

    // 检查版本号，忽略过期的响应
    if (currentVersion !== searchVersion) {
      return;
    }

    searchResults.value = (res.data || []).map((item) => ({
      ...item,
      highlightedTitle: highlightKeyword(item.articleTitle, keyword),
    }));
  } catch (error) {
    // 同样检查版本号
    if (currentVersion === searchVersion) {
      searchResults.value = [];
      snackbar.error(getErrorMessage(error));
    }
  }
}

// 内容搜索（回车触发）
async function handleContentSearch() {
  if (searchMode.value !== 'content' || !searchValue.value.trim()) return;

  // 添加到搜索历史
  addToHistory(searchValue.value);

  loading.value = true;
  hasSearched.value = true;
  try {
    const keyword = searchValue.value.trim();
    const res = await searchArticleByContent(keyword);
    searchResults.value = (res.data || []).map((item) => ({
      ...item,
      highlightedContent: item.articleContent
        ? highlightKeyword(
            extractKeywordContext(stripMarkdown(item.articleContent), keyword),
            keyword,
          )
        : undefined,
    }));
  } catch (error) {
    snackbar.error(getErrorMessage(error));
  } finally {
    loading.value = false;
  }
}

// 输入框获得焦点
function handleFocus() {
  showHistory.value = !searchValue.value;
}

// 中文输入法组合结束
function handleCompositionEnd() {
  isComposing.value = false;
  // 组合结束后，如果是标题搜索模式，触发搜索
  if (searchMode.value === 'title' && searchValue.value) {
    debouncedSearchByTitle();
  }
}

// 添加搜索历史
function addToHistory(value: string) {
  const trimmed = value.trim();
  if (!trimmed) return;
  // 去重并添加到开头，最多保留7条
  historyList.value = [trimmed, ...historyList.value.filter((h) => h !== trimmed)].slice(0, 7);
}

// 清除搜索历史
function clearHistory() {
  historyList.value = [];
}

// 删除单条搜索历史
function removeHistory(value: string) {
  historyList.value = historyList.value.filter((h) => h !== value);
}

// 点击历史记录搜索
function historySearch(value: string) {
  searchValue.value = value;
  showHistory.value = false;
  if (searchMode.value === 'title') {
    searchByTitle(); // 历史记录点击直接搜索，不需要防抖
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
  navigateToArticle(id);
}

// 监听搜索值变化（标题搜索实时响应，带防抖和输入法兼容）
watch(searchValue, (val) => {
  showHistory.value = !val;

  // 内容搜索模式下，输入变化时重置搜索状态
  if (searchMode.value === 'content') {
    hasSearched.value = false;
  }

  // 中文输入法组合过程中不触发搜索
  if (isComposing.value) {
    return;
  }

  if (searchMode.value === 'title' && val) {
    debouncedSearchByTitle();
  } else if (!val) {
    // 清空时取消防抖并清空结果
    if (debounceTimer) {
      clearTimeout(debounceTimer);
    }
    searchResults.value = [];
  }
});

// 监听搜索模式变化
watch(searchMode, () => {
  searchResults.value = [];
  searchValue.value = '';
  showHistory.value = true;
  hasSearched.value = false;
});

// 监听弹窗状态
watch(dialogVisible, (val) => {
  if (val) {
    searchValue.value = '';
    searchResults.value = [];
    showHistory.value = true;
    hasSearched.value = false;
  } else {
    // 关闭弹窗时清除防抖定时器
    if (debounceTimer) {
      clearTimeout(debounceTimer);
      debounceTimer = null;
    }
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
  position: relative;
  display: flex;
  align-items: center;
  background: #e8e8e8;
  border-radius: 16px;
  padding: 2px;
}

.v-theme--dark .search-mode-toggle {
  background: #3d3d3d;
}

/* 滑动背景块 */
.mode-slider {
  position: absolute;
  top: 2px;
  left: 2px;
  width: calc(50% - 2px);
  height: calc(100% - 4px);
  background: linear-gradient(135deg, #49b1f5, #79c9f9);
  border-radius: 14px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 4px rgba(73, 177, 245, 0.3);
}

.mode-slider.slide-right {
  transform: translateX(100%);
}

.mode-btn {
  position: relative;
  z-index: 1;
  padding: 4px 12px;
  font-size: 12px;
  border-radius: 14px;
  cursor: pointer;
  color: #666;
  transition: color 0.3s ease;
  user-select: none;
}

.v-theme--dark .mode-btn {
  color: #aaa;
}

.mode-btn.active {
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
  color: #49b1f5;
}

.section-action :deep(.v-icon) {
  margin-right: 2px;
}

/* 搜索历史列表 */
.history-list {
  display: flex;
  flex-direction: column;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 10px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.history-item:hover {
  background-color: #f5f5f5;
}

.v-theme--dark .history-item:hover {
  background-color: #2d2d2d;
}

.history-icon {
  font-size: 16px;
  color: #999;
  margin-right: 10px;
}

.history-text {
  flex: 1;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
}

.history-delete {
  color: #999;
  opacity: 0;
  transition:
    opacity 0.2s,
    color 0.2s;
}

.history-item:hover .history-delete {
  opacity: 1;
}

.history-delete:hover {
  color: #49b1f5;
}

/* 空状态 */
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
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex-shrink: 0;
}

.v-theme--dark .result-category {
  background: #1e3a5f;
  color: #64b5f6;
}

.result-content {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
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
  background-color: #e3f2fd;
  color: #1976d2;
  padding: 0 2px;
  border-radius: 2px;
}

.v-theme--dark :deep(.highlight) {
  background-color: #1e3a5f;
  color: #64b5f6;
}
</style>

<style>
.search-dialog-content {
  height: 70vh !important;
}
</style>
