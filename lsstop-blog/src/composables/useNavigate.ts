import { useRouter, useRoute } from 'vue-router'

/**
 * 导航组合函数
 */
export function useNavigate() {
  const router = useRouter()
  const route = useRoute()

  /**
   * 导航到文章详情页
   * @param id 文章ID
   */
  function navigateToArticle(id: number) {
    const path = `/article/${id}`
    if (route.path === path) return
    router.push(path).then(() => {
      window.scrollTo(0, 0)
    })
  }

  /**
   * 导航到指定路径
   * @param path 路径
   */
  function navigateTo(path: string) {
    if (route.path === path) return
    router.push(path).then(() => {
      window.scrollTo(0, 0)
    })
  }

  return {
    navigateToArticle,
    navigateTo,
  }
}
