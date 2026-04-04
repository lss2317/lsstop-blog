/**
 * 判断是否为移动端设备
 * 优先使用现代 API，降级使用 UA 检测
 */
export const isMobile = (): boolean => {
  // 优先使用 userAgentData API（现代浏览器）
  if ('userAgentData' in navigator) {
    return (navigator as Navigator & { userAgentData: { mobile: boolean } }).userAgentData.mobile;
  }
  // 降级使用 UA 字符串检测
  return /Android|iPhone|iPad|iPod|Mobile|webOS|BlackBerry|IEMobile|Opera Mini/i.test(
    navigator.userAgent,
  );
};
