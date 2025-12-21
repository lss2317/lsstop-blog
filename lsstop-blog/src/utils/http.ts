// 封装axios
import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 创建axios实例
const http: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 60000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
})

// request拦截器
http.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    NProgress.start()
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// response拦截器
// 应改为
http.interceptors.response.use(
  (response) => {
    NProgress.done()
    return response.data
  },
  (error) => {
    NProgress.done()
    return Promise.reject(error)
  },
)

export default http
