import { ref } from 'vue'

export function useEmoji() {
  const showEmoji = ref(false)
  const emojiDirection = ref<'down' | 'up'>('down')
  const emojiTriggerRef = ref<HTMLElement | null>(null)

  // 计算表情框展开方向
  const calculateEmojiDirection = (triggerEl?: HTMLElement | null) => {
    const el =
      triggerEl ||
      (Array.isArray(emojiTriggerRef.value) ? emojiTriggerRef.value[0] : emojiTriggerRef.value)
    if (!el) return

    const rect = el.getBoundingClientRect()
    const panelHeight = 220

    // 查找评论容器
    const commentContainer = el.closest('.lc-comment-container')
    if (commentContainer) {
      const containerRect = commentContainer.getBoundingClientRect()
      const spaceBelow = containerRect.bottom - rect.bottom
      const spaceAbove = rect.top - containerRect.top

      if (spaceBelow < panelHeight && spaceAbove > spaceBelow) {
        emojiDirection.value = 'up'
      } else {
        emojiDirection.value = 'down'
      }
    } else {
      // 回退到视口计算
      const spaceBelow = window.innerHeight - rect.bottom
      const spaceAbove = rect.top

      if (spaceBelow < panelHeight && spaceAbove > spaceBelow) {
        emojiDirection.value = 'up'
      } else {
        emojiDirection.value = 'down'
      }
    }
  }

  // 切换表情框
  const toggleEmoji = (event?: MouseEvent) => {
    if (!showEmoji.value) {
      const triggerEl = (event?.currentTarget || event?.target) as HTMLElement | null
      calculateEmojiDirection(triggerEl)
    }
    showEmoji.value = !showEmoji.value
  }

  // 关闭表情框
  const closeEmoji = () => {
    showEmoji.value = false
  }

  // 点击外部关闭表情框
  const handleClickOutside = (event: MouseEvent) => {
    const target = event.target as HTMLElement
    if (!target.closest('.lc-emoji-panel') && !target.closest('.lc-tool-icon')) {
      showEmoji.value = false
    }
  }

  // 注册/注销点击事件
  const registerClickOutside = () => {
    document.addEventListener('click', handleClickOutside)
  }

  const unregisterClickOutside = () => {
    document.removeEventListener('click', handleClickOutside)
  }

  return {
    showEmoji,
    emojiDirection,
    emojiTriggerRef,
    toggleEmoji,
    closeEmoji,
    registerClickOutside,
    unregisterClickOutside,
  }
}
