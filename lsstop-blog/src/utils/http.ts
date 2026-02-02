// 封装axios
import axios from 'axios'
import type { AxiosRequestConfig, InternalAxiosRequestConfig, AxiosError } from 'axios'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { tokenManager } from '@/utils/token'
import useUserInfoStore from '@/stores/modules/userInfo'
import useLikeStore from '@/stores/modules/like'
import { ResponseCode, HttpStatus } from '@/constants/http'

// 统一响应结构
export interface ApiResponse<T = unknown> {
  /** 状态码 */
  code: number
  /** 提示信息 */
  msg: string
  /** 结果数据 */
  data: T
}

// 自定义请求配置，扩展 showProgress 参数
export interface CustomAxiosRequestConfig extends AxiosRequestConfig {
  /** 是否显示加载进度条，默认 true */
  showProgress?: boolean
}

// 自定义请求实例接口，统一返回 ApiResponse<T>
interface HttpInstance {
  <T = unknown>(config: CustomAxiosRequestConfig): Promise<ApiResponse<T>>
  get<T = unknown>(url: string, config?: CustomAxiosRequestConfig): Promise<ApiResponse<T>>
  post<T = unknown>(
    url: string,
    data?: unknown,
    config?: CustomAxiosRequestConfig,
  ): Promise<ApiResponse<T>>
  put<T = unknown>(
    url: string,
    data?: unknown,
    config?: CustomAxiosRequestConfig,
  ): Promise<ApiResponse<T>>
  delete<T = unknown>(url: string, config?: CustomAxiosRequestConfig): Promise<ApiResponse<T>>
  request<T = unknown>(config: CustomAxiosRequestConfig): Promise<ApiResponse<T>>
}

// 创建axios实例
const instance = axios.create({
  baseURL: '/api/front',
  timeout: 60000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
})

// 标记是否正在刷新token
let isRefreshing = false
// 等待刷新token的请求队列
let requestsQueue: Array<(token: string) => void> = []
// 请求计数器，用于控制NProgress
let requestCount = 0

// request拦截器
instance.interceptors.request.use(
  (config: InternalAxiosRequestConfig & { _isRetry?: boolean; showProgress?: boolean }) => {
    // 判断是否需要显示进度条（默认显示）
    const showProgress = config.showProgress !== false

    // 重试请求不参与计数（原请求已经计过了）
    if (!config._isRetry && showProgress) {
      if (requestCount === 0) {
        NProgress.start()
      }
      requestCount++
    }

    // 如果有token，添加到请求头
    const token = tokenManager.getAccessToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    requestCount--
    if (requestCount === 0) {
      NProgress.done()
    }
    return Promise.reject(error)
  },
)

// response拦截器
instance.interceptors.response.use(
  (response) => {
    const config = response.config as InternalAxiosRequestConfig & {
      _isRetry?: boolean
      showProgress?: boolean
    }
    const showProgress = config.showProgress !== false

    // 重试请求不参与计数
    if (!config._isRetry && showProgress) {
      requestCount--
      if (requestCount === 0) {
        NProgress.done()
      }
    }
    return response.data
  },
  async (error: AxiosError<ApiResponse>) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean
      _isRetry?: boolean
      showProgress?: boolean
    }
    const showProgress = originalRequest.showProgress !== false

    // 重试请求不参与计数
    if (!originalRequest._isRetry && showProgress) {
      requestCount--
      if (requestCount === 0) {
        NProgress.done()
      }
    }

    // 如果是401错误且未重试过，尝试刷新token
    if (error.response?.status === HttpStatus.UNAUTHORIZED && !originalRequest._retry) {
      // 刷新token的请求不重试
      if (originalRequest.url?.includes('/auth/refresh')) {
        tokenManager.clearTokens()
        useUserInfoStore().clearUserInfo()
        useLikeStore().clearAll()
        return Promise.reject(error)
      }

      if (isRefreshing) {
        // 如果正在刷新，将请求加入队列等待
        return new Promise((resolve) => {
          requestsQueue.push((token: string) => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            originalRequest._isRetry = true
            resolve(instance(originalRequest))
          })
        })
      }

      originalRequest._retry = true
      isRefreshing = true

      const refreshTokenValue = tokenManager.getRefreshToken()
      if (!refreshTokenValue) {
        tokenManager.clearTokens()
        useUserInfoStore().clearUserInfo()
        useLikeStore().clearAll()
        isRefreshing = false
        return Promise.reject(error)
      }

      try {
        // 刷新token
        const response = await axios.post<
          ApiResponse<{ accessToken: string; refreshToken: string }>
        >(
          '/api/front/auth/refresh',
          { refreshToken: refreshTokenValue },
          { headers: { 'Content-Type': 'application/json;charset=UTF-8' } },
        )

        if (response.data.code === ResponseCode.SUCCESS && response.data.data) {
          const { accessToken, refreshToken } = response.data.data
          tokenManager.setTokens(accessToken, refreshToken)

          // 执行队列中的请求
          requestsQueue.forEach((callback) => callback(accessToken))
          requestsQueue = []

          // 重试原请求，标记为重试请求
          originalRequest.headers.Authorization = `Bearer ${accessToken}`
          originalRequest._isRetry = true
          return instance(originalRequest)
        } else {
          tokenManager.clearTokens()
          useUserInfoStore().clearUserInfo()
          useLikeStore().clearAll()
          return Promise.reject(error)
        }
      } catch (refreshError) {
        tokenManager.clearTokens()
        useUserInfoStore().clearUserInfo()
        useLikeStore().clearAll()
        requestsQueue = []
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(error)
  },
)

const http = instance as unknown as HttpInstance

export default http
