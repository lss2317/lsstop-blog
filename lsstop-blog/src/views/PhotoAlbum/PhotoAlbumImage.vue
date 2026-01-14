<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="albumCover">
      <h1 class="banner-title">{{ albumName }}</h1>
    </div>
    <!-- 相册列表 -->
    <v-card class="blog-container">
      <!-- 加载骨架屏 -->
      <div v-if="loading" class="photo-wrap">
        <v-skeleton-loader v-for="n in 6" :key="n" type="image" class="photo-skeleton" />
      </div>
      <!-- 图片列表 -->
      <div v-else-if="photoList.length > 0" class="photo-grid">
        <div
          v-for="(item, index) of photoList"
          class="photo-item"
          :key="item.id"
          @click="preview(index)"
        >
          <img :src="item.photoSrc" alt="相册图片" />
          <div class="photo-overlay">
            <v-icon color="white" size="32">mdi-magnify-plus</v-icon>
          </div>
        </div>
      </div>
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <v-icon size="48" color="grey">mdi-image-off</v-icon>
        <p>暂无照片</p>
      </div>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { getPhotoAlbumById, listPhotoByAlbumId, type PhotoAlbumImage } from '@/apis/photoAlbum'
import { useSnackbarStore } from '@/stores/modules/snackbar.ts'
import usePageInfoStore, { createCoverStyle } from '@/stores/modules/pageInfo'
import { previewImages } from '@/utils/photoPreview'

const route = useRoute()
const snackbarStore = useSnackbarStore()
const pageInfoStore = usePageInfoStore()
const { currentCoverStyle: defaultCover } = storeToRefs(pageInfoStore)

const photoList = ref<PhotoAlbumImage[]>([])
const albumName = ref('相册详情')
const albumCoverUrl = ref('')
const loading = ref(true)

// 相册封面样式：有封面用封面，没有则用默认封面
const albumCover = computed(() =>
  albumCoverUrl.value ? createCoverStyle(albumCoverUrl.value) : defaultCover.value
)

// 从路由获取相册id
const albumId = Number(route.params.albumId)

// 图片预览
const preview = (index: number) => {
  const images = photoList.value.map((item) => item.photoSrc)
  previewImages(images, index)
}

onMounted(() => {
  if (!albumId) {
    loading.value = false
    return
  }

  // 并行请求相册信息和照片列表
  Promise.all([getPhotoAlbumById(albumId), listPhotoByAlbumId(albumId)])
    .then(([albumRes, photoRes]) => {
      albumName.value = albumRes.data?.photoAlbumName ?? '相册详情'
      albumCoverUrl.value = albumRes.data?.photoAlbumCover ?? ''
      photoList.value = photoRes.data || []
    })
    .catch(() => {
      snackbarStore.error('获取相册数据失败')
    })
    .finally(() => {
      loading.value = false
    })
})
</script>

<style scoped>
.photo-grid {
  columns: 3;
  column-gap: 16px;
}

.photo-item {
  position: relative;
  break-inside: avoid;
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.photo-item img {
  width: 100%;
  display: block;
  transition: transform 0.3s ease;
}

.photo-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.photo-item:hover img {
  transform: scale(1.05);
}

.photo-item:hover .photo-overlay {
  opacity: 1;
}

@media (max-width: 1200px) {
  .photo-grid {
    columns: 2;
  }
}

@media (max-width: 759px) {
  .photo-grid {
    columns: 1;
    column-gap: 0;
  }
  .photo-item {
    margin-bottom: 12px;
  }
}

.photo-wrap {
  display: flex;
  flex-wrap: wrap;
}

.photo-skeleton {
  margin: 3px;
  flex-grow: 1;
  height: 200px;
  min-width: 200px;
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
