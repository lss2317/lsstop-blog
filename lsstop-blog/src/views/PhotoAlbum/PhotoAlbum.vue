<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">相册</h1>
    </div>
    <!-- 相册内容 -->
    <v-card class="blog-container">
      <!-- 加载骨架屏 -->
      <v-row v-if="loading">
        <v-col :md="6" cols="12" v-for="n in 4" :key="n">
          <v-skeleton-loader type="image" height="250" class="skeleton-album" />
        </v-col>
      </v-row>
      <!-- 相册列表 -->
      <v-row v-else-if="photoAlbumList.length > 0">
        <v-col :md="6" cols="12" v-for="item of photoAlbumList" :key="item.id">
          <div class="album-item">
            <img class="album-cover" :src="item.photoAlbumCover" :alt="item.photoAlbumName" />
            <router-link :to="'/photoAlbum/' + item.id" class="album-wrapper">
              <div class="album-name">{{ item.photoAlbumName }}</div>
              <div class="album-desc">{{ item.photoAlbumDesc }}</div>
            </router-link>
          </div>
        </v-col>
      </v-row>
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <v-icon size="48" color="grey">mdi-image-off</v-icon>
        <p>暂无相册数据</p>
      </div>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { listPhotoAlbum, type PhotoAlbumVO } from '@/apis/photoAlbum'
import usePageInfoStore from '@/stores/modules/pageInfo.ts'
import { useSnackbarStore } from '@/stores/modules/snackbar.ts'

const pageInfoStore = usePageInfoStore()
const { currentCoverStyle: cover } = storeToRefs(pageInfoStore)

const snackbarStore = useSnackbarStore()

const photoAlbumList = ref<PhotoAlbumVO[]>([])
const loading = ref(true)

onMounted(() => {
  listPhotoAlbum()
    .then((res) => {
      photoAlbumList.value = res.data
    })
    .catch(() => {
      snackbarStore.error('获取相册列表失败')
    })
    .finally(() => {
      loading.value = false
    })
})
</script>

<style scoped>
.skeleton-album {
  border-radius: 0.5rem;
}

.album-item {
  overflow: hidden;
  position: relative;
  cursor: pointer;
  background: #000;
  border-radius: 0.5rem !important;
}

.album-cover {
  display: block;
  width: 100%;
  height: 250px;
  opacity: 0.8;
  transition:
    opacity 0.35s,
    transform 0.35s;
  object-fit: cover;
}

.album-wrapper {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1.8rem 2rem;
  color: #fff !important;
  text-decoration: none;
}

.album-item:hover .album-cover {
  transform: scale(1.05);
  opacity: 0.4;
}

.album-item:hover .album-name:after {
  transform: translate3d(0, 0, 0);
}

.album-item:hover .album-desc {
  opacity: 1;
  filter: none;
  transform: translate3d(0, 0, 0);
}

.album-name {
  font-weight: bold;
  font-size: 1.25rem;
  overflow: hidden;
  padding: 0.7rem 0;
  position: relative;
}

.album-name:after {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: #fff;
  content: '';
  transition: transform 0.35s;
  transform: translate3d(-101%, 0, 0);
}

.album-desc {
  margin: 0;
  padding: 0.4rem 0 0;
  line-height: 1.5;
  opacity: 0;
  transition:
    opacity 0.35s,
    transform 0.35s;
  transform: translate3d(100%, 0, 0);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: var(--color-text-tertiary);
}

.empty-state p {
  margin-top: 12px;
  font-size: 14px;
}
</style>

<style>
/* 夜间模式样式 */
.v-theme--dark .blog-container .album-item {
  background: #1a1a1a;
}

.v-theme--dark .blog-container .empty-state {
  color: var(--color-text-secondary);
}
</style>
