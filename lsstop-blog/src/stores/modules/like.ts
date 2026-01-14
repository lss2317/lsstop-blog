import { defineStore } from 'pinia'
import { ref } from 'vue'
import { LikeTypeEnum } from '@/constants/likeType'
import { toggleLike as toggleLikeApi, getUserLike } from '@/apis/like'
import useUserInfoStore from '@/stores/modules/userInfo'
import { useSnackbarStore } from '@/stores/modules/snackbar'

/**
 * 点赞状态集中管理 Store
 * 管理说说、文章、评论的点赞状态
 */
const useLikeStore = defineStore('like', () => {
  // 点赞的说说ID集合
  const likedTalkIds = ref<Set<number>>(new Set())
  // 点赞的文章ID集合
  const likedArticleIds = ref<Set<number>>(new Set())
  // 点赞的评论ID集合
  const likedCommentIds = ref<Set<string>>(new Set())
  // 是否已获取过用户点赞数据
  const hasFetched = ref(false)
  // 是否正在获取
  const isFetching = ref(false)

  /**
   * 判断是否已点赞
   * @param type 点赞类型
   * @param id 目标ID
   */
  function isLiked(type: LikeTypeEnum, id: number | string): boolean {
    switch (type) {
      case LikeTypeEnum.TALK:
        return likedTalkIds.value.has(id as number)
      case LikeTypeEnum.ARTICLE:
        return likedArticleIds.value.has(id as number)
      case LikeTypeEnum.COMMENT:
        return likedCommentIds.value.has(id as string)
      default:
        return false
    }
  }

  /**
   * 切换点赞状态（并发送API请求）
   * @param type 点赞类型
   * @param id 目标ID
   * @returns 切换后是否为已点赞状态，失败时返回 null
   */
  async function toggleLike(type: LikeTypeEnum, id: number | string): Promise<boolean | null> {
    const userInfoStore = useUserInfoStore()
    const snackbarStore = useSnackbarStore()
    const userId = userInfoStore.userInfo.userId

    // 未登录时提示用户登录
    if (!userId) {
      snackbarStore.info('登录后即可点赞哦~')
      return null
    }

    // 发送点赞请求
    try {
      await toggleLikeApi({
        targetId: Number(id),
        type,
      })
    } catch (error) {
      // 请求失败，显示错误信息
      console.error(error)
      snackbarStore.error('点赞失败，请稍后重试')
      // 请求失败时返回 null，让调用方知道失败了
      return null
    }

    // 请求成功，更新本地状态
    switch (type) {
      case LikeTypeEnum.TALK:
        if (likedTalkIds.value.has(id as number)) {
          likedTalkIds.value.delete(id as number)
          return false
        } else {
          likedTalkIds.value.add(id as number)
          return true
        }
      case LikeTypeEnum.ARTICLE:
        if (likedArticleIds.value.has(id as number)) {
          likedArticleIds.value.delete(id as number)
          return false
        } else {
          likedArticleIds.value.add(id as number)
          return true
        }
      case LikeTypeEnum.COMMENT:
        if (likedCommentIds.value.has(id as string)) {
          likedCommentIds.value.delete(id as string)
          return false
        } else {
          likedCommentIds.value.add(id as string)
          return true
        }
      default:
        return false
    }
  }

  /**
   * 添加点赞
   * @param type 点赞类型
   * @param id 目标ID
   */
  function addLike(type: LikeTypeEnum, id: number | string) {
    switch (type) {
      case LikeTypeEnum.TALK:
        likedTalkIds.value.add(id as number)
        break
      case LikeTypeEnum.ARTICLE:
        likedArticleIds.value.add(id as number)
        break
      case LikeTypeEnum.COMMENT:
        likedCommentIds.value.add(id as string)
        break
    }
  }

  /**
   * 移除点赞
   * @param type 点赞类型
   * @param id 目标ID
   */
  function removeLike(type: LikeTypeEnum, id: number | string) {
    switch (type) {
      case LikeTypeEnum.TALK:
        likedTalkIds.value.delete(id as number)
        break
      case LikeTypeEnum.ARTICLE:
        likedArticleIds.value.delete(id as number)
        break
      case LikeTypeEnum.COMMENT:
        likedCommentIds.value.delete(id as string)
        break
    }
  }

  /**
   * 批量设置点赞（用于从服务端初始化）
   * @param type 点赞类型
   * @param ids ID数组
   */
  function setLikedIds(type: LikeTypeEnum, ids: (number | string)[]) {
    switch (type) {
      case LikeTypeEnum.TALK:
        likedTalkIds.value = new Set(ids as number[])
        break
      case LikeTypeEnum.ARTICLE:
        likedArticleIds.value = new Set(ids as number[])
        break
      case LikeTypeEnum.COMMENT:
        likedCommentIds.value = new Set(ids as string[])
        break
    }
  }

  /**
   * 清空所有点赞状态
   */
  function clearAll() {
    likedTalkIds.value.clear()
    likedArticleIds.value.clear()
    likedCommentIds.value.clear()
    hasFetched.value = false
  }

  /**
   * 从服务端获取用户点赞数据
   * @param force 是否强制重新获取
   */
  async function fetchUserLike(force = false) {
    // 已获取过且不强制刷新，直接返回
    if (hasFetched.value && !force) return
    // 正在获取中，避免重复请求
    if (isFetching.value) return

    const userInfoStore = useUserInfoStore()
    const userId = userInfoStore.userInfo.userId
    if (!userId) return

    try {
      isFetching.value = true
      const res = await getUserLike(userId)
      const data = res.data
      likedTalkIds.value = new Set(data.talkLikeIds || [])
      likedArticleIds.value = new Set(data.articleLikeIds || [])
      likedCommentIds.value = new Set(data.commentLikeIds?.map(String) || [])
      hasFetched.value = true
    } catch (error) {
      console.error('获取用户点赞数据失败', error)
    } finally {
      isFetching.value = false
    }
  }

  return {
    likedTalkIds,
    likedArticleIds,
    likedCommentIds,
    isLiked,
    toggleLike,
    addLike,
    removeLike,
    setLikedIds,
    clearAll,
    fetchUserLike,
    hasFetched,
  }
})

export default useLikeStore
