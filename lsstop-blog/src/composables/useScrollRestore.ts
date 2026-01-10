import { onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'

const SCROLL_KEY = 'page_scroll_position'

/**
 * 页面刷新时保持滚动位置
 * @param delay 恢复滚动位置的延迟时间（等待内容加载）
 */
export function useScrollRestore(delay = 200) {
  const route = useRoute()

  // 保存滚动位置
  const saveScrollPosition = () => {
    sessionStorage.setItem(SCROLL_KEY, JSON.stringify({ path: route.fullPath, y: window.scrollY }))
  }

  // 恢复滚动位置
  const restoreScrollPosition = () => {
    const saved = sessionStorage.getItem(SCROLL_KEY)
    if (!saved) return

    const { path, y } = JSON.parse(saved)
    if (path === route.fullPath && y > 0) {
      window.scrollTo(0, y)
    }
    sessionStorage.removeItem(SCROLL_KEY)
  }

  onMounted(() => {
    window.addEventListener('beforeunload', saveScrollPosition)
    setTimeout(restoreScrollPosition, delay)
  })

  onUnmounted(() => {
    window.removeEventListener('beforeunload', saveScrollPosition)
  })
}
