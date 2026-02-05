<template>
  <v-navigation-drawer
    v-model="drawer"
    width="250"
    temporary
    location="end"
    :style="{ top: '0', height: '100%', zIndex: 1010 }"
  >
    <!-- 博主介绍 -->
    <div class="blogger-info">
      <v-avatar size="110" style="margin-bottom: 0.5rem">
        <v-img :src="avatar" cover />
      </v-avatar>
    </div>
    <!-- 博客信息 -->
    <div class="blog-info-wrapper">
      <div class="blog-info-data">
        <a @click="navigateTo('/archive')">
          <div style="font-size: 0.875rem">文章</div>
          <div style="font-size: 1.125rem">
            {{ articleCount }}
          </div>
        </a>
      </div>
      <div class="blog-info-data">
        <a @click="navigateTo('/categories')">
          <div style="font-size: 0.875rem">分类</div>
          <div style="font-size: 1.125rem">
            {{ categoriesCount }}
          </div>
        </a>
      </div>
      <div class="blog-info-data">
        <a @click="navigateTo('/tag')">
          <div style="font-size: 0.875rem">标签</div>
          <div style="font-size: 1.125rem">
            {{ labelCount }}
          </div>
        </a>
      </div>
    </div>
    <hr />
    <!-- 页面导航 -->
    <div class="menu-container">
      <div class="menus-item">
        <a @click="navigateTo('/')"> <i class="iconfont iconzhuye" /> 首页 </a>
      </div>
      <div class="menus-item">
        <a @click="navigateTo('/archive')"> <i class="iconfont iconguidang" /> 归档 </a>
      </div>
      <div class="menus-item">
        <a @click="navigateTo('/photoAlbum')"> <i class="iconfont iconxiangce1" /> 相册 </a>
      </div>
      <div class="menus-item">
        <a @click="navigateTo('/talk')"> <i class="iconfont iconpinglun" /> 说说 </a>
      </div>
      <div class="menus-item">
        <a @click="navigateTo('/category')"> <i class="iconfont iconfenlei" /> 分类 </a>
      </div>
      <div class="menus-item">
        <a @click="navigateTo('/tag')"> <i class="iconfont iconbiaoqian" /> 标签 </a>
      </div>
      <div class="menus-item">
        <a @click="navigateTo('/friendLink')"> <i class="iconfont iconlianjie" /> 友链 </a>
      </div>
      <div class="menus-item">
        <a @click="navigateTo('/about')"> <i class="iconfont iconzhifeiji" /> 关于 </a>
      </div>
      <div class="menus-item">
        <a @click="navigateTo('/message')"> <i class="iconfont iconpinglunzu" /> 留言 </a>
      </div>
      <div class="menus-item" v-if="!isLoggedIn">
        <a @click="openLoginDialog"><i class="iconfont icondenglu" /> 登录 </a>
      </div>
      <div v-else>
        <div class="menus-item">
          <a @click="navigateTo('/user')"> <i class="iconfont icongerenzhongxin" /> 个人中心 </a>
        </div>
        <div class="menus-item">
          <a @click="handleLogout"><i class="iconfont icontuichu" /> 退出</a>
        </div>
      </div>
    </div>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useDrawerStore } from '@/stores/modules/drawer';
import { useLoginStore } from '@/stores/modules/login';
import useWebsiteConfigStore from '@/stores/modules/websiteConfig';
import useUserInfoStore from '@/stores/modules/userInfo';
import { useAuthStore } from '@/stores/modules/auth';

const router = useRouter();
const route = useRoute();
const drawerStore = useDrawerStore();
const { drawer } = storeToRefs(drawerStore);

const loginStore = useLoginStore();
const { openLoginDialog } = loginStore;

const websiteConfigStore = useWebsiteConfigStore();
const { config } = storeToRefs(websiteConfigStore);
const avatar = computed(() => config.value.siteAvatar);

const userInfoStore = useUserInfoStore();
const authStore = useAuthStore();
const { handleLogout } = authStore;
const isLoggedIn = computed(() => !!userInfoStore.userInfo.userId);

const articleCount = ref(0);
const categoriesCount = ref(0);
const labelCount = ref(0);

// 导航
function navigateTo(path: string) {
  drawer.value = false;
  // 相同页面不滚动
  if (route.path === path) return;
  router.push(path).then(() => {
    window.scrollTo(0, 0);
  });
}
</script>

<style scoped>
.blogger-info {
  padding: 26px 30px 0;
  text-align: center;
}

.blog-info-wrapper {
  display: flex;
  align-items: center;
  padding: 12px 10px 0;
}

.blog-info-data {
  flex: 1;
  line-height: 2;
  text-align: center;
}

hr {
  border: 2px dashed #d2ebfd;
  margin: 20px 0;
}

.menu-container {
  padding: 0 10px 40px;
  animation: 0.8s ease 0s 1 normal none running sidebarItem;
}

.menus-item a {
  padding: 6px 30px;
  display: block;
  line-height: 2;
}

.menus-item i {
  margin-right: 2rem;
}

@keyframes sidebarItem {
  0% {
    transform: translateX(200px);
  }
  100% {
    transform: translateX(0);
  }
}
</style>
