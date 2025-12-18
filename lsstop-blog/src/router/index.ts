import { createRouter, createWebHistory } from 'vue-router'
import { constantRouter } from '@/router/routers.ts'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRouter,
})

export default router
