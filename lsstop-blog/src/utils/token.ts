import axios from 'axios';
import { ResponseCode } from '@/constants/http';

const ACCESS_TOKEN_KEY = 'accessToken';
const REFRESH_TOKEN_KEY = 'refreshToken';

/** 是否正在刷新（防并发） */
let refreshingPromise: Promise<string | null> | null = null;

/**
 * Token 管理类
 */
class TokenManager {
  /**
   * 获取 accessToken
   */
  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  /**
   * 获取 refreshToken
   */
  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  /**
   * 设置 accessToken
   */
  setAccessToken(token: string): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, token);
  }

  /**
   * 设置 refreshToken
   */
  setRefreshToken(token: string): void {
    localStorage.setItem(REFRESH_TOKEN_KEY, token);
  }

  /**
   * 保存所有 token
   */
  setTokens(accessToken: string, refreshToken: string): void {
    this.setAccessToken(accessToken);
    this.setRefreshToken(refreshToken);
  }

  /**
   * 清除所有 token
   */
  clearTokens(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  }

  /**
   * 判断是否有 token
   */
  hasToken(): boolean {
    return !!this.getAccessToken();
  }

  /**
   * 刷新 accessToken（自动防并发）
   * @returns 新的 accessToken，刷新失败返回 null
   */
  async refreshAccessToken(): Promise<string | null> {
    // 防止并发刷新
    if (refreshingPromise) return refreshingPromise;

    refreshingPromise = this._doRefresh();
    try {
      return await refreshingPromise;
    } finally {
      refreshingPromise = null;
    }
  }

  private async _doRefresh(): Promise<string | null> {
    const refreshTokenValue = this.getRefreshToken();
    if (!refreshTokenValue) return null;

    try {
      const response = await axios.post<{
        code: number;
        data: { accessToken: string; refreshToken: string };
      }>(
        '/api/front/auth/refresh',
        { refreshToken: refreshTokenValue },
        { headers: { 'Content-Type': 'application/json;charset=UTF-8' } },
      );

      if (response.data.code === ResponseCode.SUCCESS && response.data.data) {
        const { accessToken, refreshToken } = response.data.data;
        this.setTokens(accessToken, refreshToken);
        return accessToken;
      }
      return null;
    } catch {
      return null;
    }
  }
}

export const tokenManager = new TokenManager();
