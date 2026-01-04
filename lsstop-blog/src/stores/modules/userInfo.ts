import { defineStore } from 'pinia'
import { shallowRef } from 'vue'
import { getUserInfo } from '@/apis/user'

export interface UserInfo {
  /** 用户id */
  userId: number | null
  /** 昵称 */
  nickname: string | null
  /** 头像 */
  avatar: string | null
  /** 个人网站 */
  website: string | null
  /** 个人简介 */
  intro: string | null
  /** accessToken */
  accessToken?: string | null
  /** refreshToken */
  refreshToken?: string | null
}

const defaultUserInfo: UserInfo = {
  userId: null,
  nickname: null,
  avatar: null,
  website: null,
  intro: null,
  accessToken: null,
  refreshToken: null,
}

const useUserInfoStore = defineStore('userInfo', () => {
  const userInfo = shallowRef<UserInfo>({ ...defaultUserInfo })
  const blogInfo = shallowRef<Record<string, unknown>>({})

  // 设置用户信息
  function setUserInfo(info: Partial<UserInfo>) {
    userInfo.value = { ...userInfo.value, ...info }
  }

  // 清除用户信息
  function clearUserInfo() {
    userInfo.value = { ...defaultUserInfo }
  }

  // 设置博客信息
  function setBlogInfo(info: Record<string, unknown>) {
    blogInfo.value = info
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      if (res.code === 200 && res.data) {
        setUserInfo(res.data)
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  return {
    userInfo,
    blogInfo,
    setUserInfo,
    clearUserInfo,
    setBlogInfo,
    fetchUserInfo,
  }
})

export default useUserInfoStore
