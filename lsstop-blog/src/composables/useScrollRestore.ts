import { onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const SCROLL_KEY = 'page_scroll_position'

/**
 * 页面刷新时保持滚动位置
 */
export function useScrollRestore() {
  const route = useRoute()
  const router = useRouter()

  // 检查是否需要恢复
  const saved = sessionStorage.getItem(SCROLL_KEY)
  if (saved) {
    const { y } = JSON.parse(saved)
    if (y > 0) {
      // 添加恢复中标记，隐藏 footer
      document.body.classList.add('scroll-restoring')
      // 擐高页面，让页面一开始就有足够高度
      document.body.style.minHeight = `${y + window.innerHeight}px`
      // 立即滚动到位置
      window.scrollTo(0, y)
    }
  }

  // 保存滚动位置
  const saveScrollPosition = () => {
    sessionStorage.setItem(SCROLL_KEY, JSON.stringify({ path: route.fullPath, y: window.scrollY }))
  }

  onMounted(async () => {
    window.addEventListener('beforeunload', saveScrollPosition)

    await router.isReady()
    sessionStorage.removeItem(SCROLL_KEY)

    // 内容加载完成后移除临时样式
    setTimeout(() => {
      document.body.style.minHeight = ''
      document.body.classList.remove('scroll-restoring')
    }, 500)
  })

  onUnmounted(() => {
    window.removeEventListener('beforeunload', saveScrollPosition)
  })
}
