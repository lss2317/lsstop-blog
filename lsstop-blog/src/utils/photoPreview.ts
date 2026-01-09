import PhotoSwipe from 'photoswipe'
import 'photoswipe/style.css'

// 图片缓存（尺寸 + 加载状态）
const imageCache = new Map<string, { width: number; height: number; loaded: boolean }>()

// 预加载图片（获取尺寸并缓存）
const preloadImage = (src: string): Promise<{ width: number; height: number }> => {
  const cached = imageCache.get(src)
  if (cached?.loaded) {
    return Promise.resolve({ width: cached.width, height: cached.height })
  }
  return new Promise((resolve) => {
    const img = new Image()
    img.onload = () => {
      const size = { width: img.naturalWidth, height: img.naturalHeight, loaded: true }
      imageCache.set(src, size)
      resolve({ width: size.width, height: size.height })
    }
    img.onerror = () => {
      const size = { width: 1200, height: 800, loaded: true }
      imageCache.set(src, size)
      resolve({ width: size.width, height: size.height })
    }
    img.src = src
  })
}

/**
 * 图片预览
 * @param images 图片地址数组
 * @param index 起始索引
 */
export async function previewImages(images: string[], index: number = 0) {
  if (!images.length) return

  // 确保索引在有效范围内
  const safeIndex = Math.max(0, Math.min(index, images.length - 1))

  // 等待所有图片加载完成
  const sizes = await Promise.all(images.map((src) => preloadImage(src)))

  const items = images.map((src, i) => ({
    src,
    width: sizes[i]?.width ?? 1200,
    height: sizes[i]?.height ?? 800,
  }))

  const pswp = new PhotoSwipe({
    dataSource: items,
    index: safeIndex,
    wheelToZoom: true,
    closeOnVerticalDrag: true,
    bgOpacity: 0.9,
    zoom: true,
    counter: true,
    arrowKeys: true,
    // 图片周围留出边距
    padding: { top: 40, bottom: 40, left: 60, right: 60 },
    // 初始缩放级别：适应屏幕
    initialZoomLevel: 'fit',
    // 双击/缩放按钮的缩放级别
    secondaryZoomLevel: 1.5,
    // 最大缩放级别
    maxZoomLevel: 3,
    // 禁用打开动画，避免黑屏闪烁
    showHideAnimationType: 'none',
  })

  pswp.init()
}
