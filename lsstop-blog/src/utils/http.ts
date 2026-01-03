// 封装axios
import axios from 'axios'
import type { AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { tokenManager } from '@/utils/token'

// 统一响应结构
export interface ApiResponse<T = unknown> {
  /** 状态码 */
  code: number
  /** 提示信息 */
  msg: string
  /** 结果数据 */
  data: T
}

// 自定义请求实例接口，统一返回 ApiResponse<T>
interface HttpInstance {
  <T = unknown>(config: AxiosRequestConfig): Promise<ApiResponse<T>>
  get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>>
  post<T = unknown>(
    url: string,
    data?: unknown,
    config?: AxiosRequestConfig,
  ): Promise<ApiResponse<T>>
  put<T = unknown>(
    url: string,
    data?: unknown,
    config?: AxiosRequestConfig,
  ): Promise<ApiResponse<T>>
  delete<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>>
  request<T = unknown>(config: AxiosRequestConfig): Promise<ApiResponse<T>>
}

// 创建axios实例
const instance = axios.create({
  baseURL: '/api/front',
  timeout: 60000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
})

// request拦截器
instance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    NProgress.start()

    // 如果有token，添加到请求头
    const token = tokenManager.getAccessToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// response拦截器
instance.interceptors.response.use(
  (response) => {
    NProgress.done()
    return response.data
  },
  (error) => {
    NProgress.done()
    return Promise.reject(error)
  },
)

const http = instance as unknown as HttpInstance

export default http
