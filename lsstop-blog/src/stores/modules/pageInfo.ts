import { defineStore } from 'pinia'
import { computed, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { listPageInfo, type PageInfoVo } from '@/apis/pageInfo'

/**
 * 生成封面背景样式
 * @param coverUrl 封面图片地址
 * @param position 背景位置，默认 'center 35%'
 */
export function createCoverStyle(coverUrl?: string, position = 'center 35%') {
  if (!coverUrl) return {}
  return { background: `#49b1f5 url(${coverUrl}) ${position} / cover no-repeat` }
}

const usePageInfoStore = defineStore('pageInfo', () => {
  const pageList = shallowRef<PageInfoVo[]>([])

  // 获取页面列表（已有数据则不重复请求）
  async function fetchPageList() {
    if (pageList.value.length > 0) return
    const res = await listPageInfo()
    pageList.value = res.data
  }

  // 根据 pageLabel 获取对应的 cover 样式
  function getCoverStyle(pageLabel: string): string {
    const page = pageList.value.find((item) => item.pageLabel === pageLabel)
    const cover = page?.pageCover || ''
    return `background: #49b1f5 url(${cover}) center 35% / cover no-repeat`
  }

  // 自动根据当前路由获取 cover 样式
  const router = useRouter()
  const currentCoverStyle = computed(() => {
    const pageLabel = router.currentRoute.value.meta.pageLabel as string
    return getCoverStyle(pageLabel)
  })

  return {
    pageList,
    fetchPageList,
    currentCoverStyle,
  }
})

export default usePageInfoStore
