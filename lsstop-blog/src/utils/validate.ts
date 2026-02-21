/**
 * 校验邮箱格式
 * @param email 邮箱地址
 * @returns 是否为有效邮箱格式
 */
export function isValidEmail(email: string): boolean {
  const v = email.trim();
  return /^(?!.*\.\.)(?!\.)(?!.*\.$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(v);
}
