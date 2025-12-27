import { defineStore } from 'pinia'
import { computed, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { listPageInfo } from '@/apis/pageInfo'

interface PageInfo {
  pageName: string
  pageLabel: string
  pageCover: string
}

const usePageInfoStore = defineStore('pageInfo', () => {
  const pageList = shallowRef<PageInfo[]>([])

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
    return `background: #49b1f5 url(${cover}) center 30% / cover no-repeat`
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
