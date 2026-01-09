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
      <div v-else-if="photoList.length > 0" class="photo-wrap">
        <img
          v-for="(item, index) of photoList"
          class="photo"
          :key="item.id"
          :src="item.photoSrc"
          @click="preview(index)"
        />
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
import { getPhotoAlbumById, listPhotoByAlbumId, type PhotoAlbumImage } from '@/apis/photoAlbum'
import { useSnackbarStore } from '@/stores/modules/snackbar.ts'
import { createCoverStyle } from '@/stores/modules/pageInfo'
import { previewImages } from '@/utils/photoPreview'

const route = useRoute()
const snackbarStore = useSnackbarStore()

const photoList = ref<PhotoAlbumImage[]>([])
const albumName = ref('相册详情')
const albumCoverUrl = ref('')
const loading = ref(true)

// 相册封面样式
const albumCover = computed(() => createCoverStyle(albumCoverUrl.value))

// 从路由获取相册id
const albumId = Number(route.params.albumId)

// 图片预览
const preview = (index: number) => {
  const images = photoList.value.map((item) => item.photoSrc)
  previewImages(images, index)
}

onMounted(() => {
  if (!albumId) {
    snackbarStore.error('相册ID无效')
    loading.value = false
    return
  }

  // 并行请求相册信息和照片列表
  Promise.all([getPhotoAlbumById(albumId), listPhotoByAlbumId(albumId)])
    .then(([albumRes, photoRes]) => {
      // 相册不存在
      if (!albumRes.data) {
        snackbarStore.error('相册不存在')
        return
      }
      albumName.value = albumRes.data.photoAlbumName
      albumCoverUrl.value = albumRes.data.photoAlbumCover
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
.photo-wrap {
  display: flex;
  flex-wrap: wrap;
}

.photo {
  margin: 3px;
  cursor: pointer;
  flex-grow: 1;
  object-fit: cover;
  height: 200px;
}

.photo-wrap::after {
  content: '';
  display: block;
  flex-grow: 9999;
}

@media (max-width: 759px) {
  .photo {
    width: 100%;
  }
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
