/**
 * 从错误对象中提取错误信息
 * @param error 错误对象
 * @param fallback 默认错误信息
 */
export const getErrorMessage = (error: unknown, fallback = '网络错误，请稍后重试'): string => {
  if (error && typeof error === 'object' && 'response' in error) {
    const res = (error as { response?: { data?: { msg?: string } } }).response;
    return res?.data?.msg || fallback;
  }
  return fallback;
};
