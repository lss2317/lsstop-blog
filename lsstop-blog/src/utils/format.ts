/**
 * 格式化数量显示
 * @param num 数量
 * @returns 格式化后的字符串，如 1.2K
 */
export function formatCount(num: number): string {
  if (num >= 10000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

/**
 * 格式化字数显示
 * @param num 字数
 * @returns 格式化后的字符串，如 1.2k
 */
export function formatWordNum(num: number): string {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}
