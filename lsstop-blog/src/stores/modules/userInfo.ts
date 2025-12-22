import { defineStore } from 'pinia'
import { computed, shallowRef } from 'vue'

export interface UserInfo {
  userId: string | null
  avatar: string | null
  nickname: string | null
  intro: string | null
  email: string | null
  webSite: string | null
  loginType: number | null
}

const defaultUserInfo: UserInfo = {
  userId: null,
  avatar: null,
  nickname: null,
  intro: null,
  email: null,
  webSite: null,
  loginType: null,
}

const useUserInfoStore = defineStore('userInfo', () => {
  const userInfo = shallowRef<UserInfo>({ ...defaultUserInfo })
  const blogInfo = shallowRef<Record<string, unknown>>({})

  // 是否已登录
  const isLoggedIn = computed(() => !!userInfo.value.userId)

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

  return {
    userInfo,
    blogInfo,
    isLoggedIn,
    setUserInfo,
    clearUserInfo,
    setBlogInfo,
  }
})

export default useUserInfoStore
