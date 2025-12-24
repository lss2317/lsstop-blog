export const constantRouter = [
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About/BlogAbout.vue'),
    meta: {
      title: '关于我',
    },
  },
  {
    path: '/message',
    component: () => import('@/views/Message/BlogMessage.vue'),
    name: 'Message',
    meta: {
      title: '留言板',
    },
  },
  // 访问其他任何不存在的路由，重定向到首页
  // {
  //   path: '/:pathMatch(.*)*',
  //   redirect: '/',
  //   name: 'any',
  // }
]
