/**
 * 格式化字数显示
 * @param num 字数
 * @returns 格式化后的字符串，如 1.2k
 */
export function formatWordNum(num: number): string {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k';
  }
  return num.toString();
}

/**
 * HTML特殊字符转义，防止XSS攻击
 * @param str 原始字符串
 * @returns 转义后的安全字符串
 */
export function escapeHtml(str: string): string {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

/**
 * 高亮关键词
 * @param text 原始文本
 * @param keyword 需要高亮的关键词
 * @returns 包含高亮标签的HTML字符串
 */
export function highlightKeyword(text: string, keyword: string): string {
  if (!keyword) return text;
  const regex = new RegExp(`(${keyword})`, 'gi');
  return text.replace(regex, '<span class="highlight">$1</span>');
}
