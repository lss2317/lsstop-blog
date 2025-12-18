export const constantRouter = [
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/about/BlogAbout.vue'),
    meta: {
      title: '关于我',
    },
  },
  {
    path: '/message',
    component: () => import('../views/message/BlogMessage.vue'),
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
