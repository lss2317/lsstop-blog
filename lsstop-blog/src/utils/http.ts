// 封装axios
import axios from 'axios';
import type { AxiosRequestConfig, InternalAxiosRequestConfig, AxiosError } from 'axios';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import { tokenManager } from '@/utils/token';
import { clearAuthState } from '@/stores/modules/auth';
import { ResponseCode, HttpStatus } from '@/constants/http';
import { useSnackbarStore } from '@/stores/modules/snackbar';

// 统一响应结构
export interface ApiResponse<T = unknown> {
  /** 状态码 */
  code: number;
  /** 提示信息 */
  msg: string;
  /** 结果数据 */
  data: T;
}

// 自定义请求配置，扩展 showProgress 参数
export interface CustomAxiosRequestConfig extends AxiosRequestConfig {
  /** 是否显示加载进度条，默认 true */
  showProgress?: boolean;
}

// 自定义请求实例接口，统一返回 ApiResponse<T>
interface HttpInstance {
  <T = unknown>(config: CustomAxiosRequestConfig): Promise<ApiResponse<T>>;

  get<T = unknown>(url: string, config?: CustomAxiosRequestConfig): Promise<ApiResponse<T>>;

  post<T = unknown>(
    url: string,
    data?: unknown,
    config?: CustomAxiosRequestConfig,
  ): Promise<ApiResponse<T>>;

  put<T = unknown>(
    url: string,
    data?: unknown,
    config?: CustomAxiosRequestConfig,
  ): Promise<ApiResponse<T>>;

  delete<T = unknown>(url: string, config?: CustomAxiosRequestConfig): Promise<ApiResponse<T>>;

  request<T = unknown>(config: CustomAxiosRequestConfig): Promise<ApiResponse<T>>;
}

// 创建axios实例
const instance = axios.create({
  baseURL: '/api/front',
  timeout: 60000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
});

// 标记是否正在刷新token
let isRefreshing = false;
// 等待刷新token的请求队列
interface QueuedRequest {
  resolve: (value: unknown) => void;
  reject: (reason: unknown) => void;
  config: InternalAxiosRequestConfig;
}
let requestsQueue: QueuedRequest[] = [];
// 请求计数器，用于控制NProgress
let requestCount = 0;

// 拒绝队列中所有请求并清空
function rejectQueuedRequests(reason: unknown) {
  requestsQueue.forEach(({ reject }) => reject(reason));
  requestsQueue = [];
}

// request拦截器
instance.interceptors.request.use(
  (
    config: InternalAxiosRequestConfig & {
      _isReplayRequest?: boolean;
      showProgress?: boolean;
      _counted?: boolean;
    },
  ) => {
    // 判断是否需要显示进度条（默认显示）
    const showProgress = config.showProgress !== false;

    // 重试请求不参与计数（原请求已经计过了）
    if (!config._isReplayRequest && showProgress) {
      if (requestCount === 0) {
        NProgress.start();
      }
      requestCount++;
      config._counted = true;
    }

    // 如果有token，添加到请求头
    const token = tokenManager.getAccessToken();
    if (token) {
      (config.headers as Record<string, string>)['Authorization'] = `Bearer ${token}`;
    }

    // FormData 时删除 Content-Type，让浏览器自动设置（包含 boundary）
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    }

    return config;
  },
  (error) => {
    // request error 时 requestCount 还没增加，无需减
    return Promise.reject(error);
  },
);

// response拦截器
instance.interceptors.response.use(
  (response) => {
    const config = response.config as InternalAxiosRequestConfig & {
      _isReplayRequest?: boolean;
      showProgress?: boolean;
      _counted?: boolean;
    };

    // 只有计数过的请求才减
    if (config._counted) {
      requestCount = Math.max(0, requestCount - 1);
      if (requestCount === 0) {
        NProgress.done();
      }
    }
    return response.data;
  },
  async (error: AxiosError<ApiResponse>) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & {
      _tokenRefreshed?: boolean;
      _isReplayRequest?: boolean;
      showProgress?: boolean;
      _counted?: boolean;
    };

    // 只有计数过的请求才减
    if (originalRequest?._counted) {
      originalRequest._counted = false; // 防止重复减
      requestCount = Math.max(0, requestCount - 1);
      if (requestCount === 0) {
        NProgress.done();
      }
    }

    // 限流错误，提示用户
    if (error.response?.status === HttpStatus.TOO_MANY_REQUESTS) {
      const snackbar = useSnackbarStore();
      const msg = error.response.data?.msg || '请求过于频繁，请稍后再试';
      snackbar.error(msg);
      return Promise.reject(error);
    }

    // 如果是401错误且未重试过，尝试刷新token
    if (error.response?.status === HttpStatus.UNAUTHORIZED && !originalRequest._tokenRefreshed) {
      // 刷新token的请求不重试
      if (originalRequest.url?.includes('/auth/refresh')) {
        clearAuthState();
        rejectQueuedRequests(error);
        return Promise.reject(error);
      }

      if (isRefreshing) {
        // 如果正在刷新，将请求加入队列等待（clone config 避免并发污染）
        const clonedConfig = {
          ...originalRequest,
          headers: { ...(originalRequest.headers || {}) },
        } as InternalAxiosRequestConfig;
        return new Promise((resolve, reject) => {
          requestsQueue.push({ resolve, reject, config: clonedConfig });
        });
      }

      originalRequest._tokenRefreshed = true;
      isRefreshing = true;

      const refreshTokenValue = tokenManager.getRefreshToken();
      if (!refreshTokenValue) {
        clearAuthState();
        rejectQueuedRequests(error);
        isRefreshing = false;
        return Promise.reject(error);
      }

      try {
        // 刷新token
        const response = await axios.post<
          ApiResponse<{ accessToken: string; refreshToken: string }>
        >(
          '/api/front/auth/refresh',
          { refreshToken: refreshTokenValue },
          { headers: { 'Content-Type': 'application/json;charset=UTF-8' } },
        );

        if (response.data.code === ResponseCode.SUCCESS && response.data.data) {
          const { accessToken, refreshToken } = response.data.data;
          tokenManager.setTokens(accessToken, refreshToken);

          // 执行队列中的请求
          requestsQueue.forEach(({ resolve, reject, config }) => {
            (config.headers as Record<string, string>)['Authorization'] = `Bearer ${accessToken}`;
            (
              config as InternalAxiosRequestConfig & {
                _tokenRefreshed?: boolean;
                _isReplayRequest?: boolean;
              }
            )._tokenRefreshed = true;
            (
              config as InternalAxiosRequestConfig & { _isReplayRequest?: boolean }
            )._isReplayRequest = true;
            instance(config).then(resolve).catch(reject);
          });
          requestsQueue = [];

          // 重试原请求，标记为重试请求
          (originalRequest.headers as Record<string, string>)['Authorization'] =
            `Bearer ${accessToken}`;
          originalRequest._tokenRefreshed = true;
          originalRequest._isReplayRequest = true;
          return instance(originalRequest);
        } else {
          clearAuthState();
          rejectQueuedRequests(error);
          return Promise.reject(error);
        }
      } catch (refreshError) {
        clearAuthState();
        rejectQueuedRequests(refreshError);
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  },
);

const http = instance as unknown as HttpInstance;

export default http;
