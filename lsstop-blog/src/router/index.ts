import { createRouter, createWebHistory } from 'vue-router';
import { constantRouter } from '@/router/routers.ts';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRouter,
});

// 路由切换时更新页面标题
router.afterEach((to) => {
  const title = to.meta.title as string;
  document.title = title ? `${title} | 阿圣BLOG` : '阿圣BLOG';
});

export default router;
