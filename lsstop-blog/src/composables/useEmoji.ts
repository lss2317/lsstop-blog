import { ref } from 'vue'

export function useEmoji() {
  const showEmoji = ref(false)
  const emojiDirection = ref<'down' | 'up'>('down')
  const emojiTriggerRef = ref<HTMLElement | null>(null)

  // 计算表情框展开方向
  const calculateEmojiDirection = () => {
    if (!emojiTriggerRef.value) return
    const rect = emojiTriggerRef.value.getBoundingClientRect()
    const panelHeight = 220
    const spaceBelow = window.innerHeight - rect.bottom
    const spaceAbove = rect.top

    if (spaceBelow < panelHeight && spaceAbove > spaceBelow) {
      emojiDirection.value = 'up'
    } else {
      emojiDirection.value = 'down'
    }
  }

  // 切换表情框
  const toggleEmoji = () => {
    if (!showEmoji.value) {
      calculateEmojiDirection()
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
