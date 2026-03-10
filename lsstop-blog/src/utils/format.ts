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

/**
 * 提取关键词上下文，截取关键词前后的内容
 * @param text 原始文本
 * @param keyword 关键词
 * @param contextLength 关键词前后截取的字符数，默认20
 * @returns 包含关键词的上下文摘要
 */
export function extractKeywordContext(
  text: string,
  keyword: string,
  contextLength: number = 20,
): string {
  if (!keyword || !text) return text;

  const lowerText = text.toLowerCase();
  const lowerKeyword = keyword.toLowerCase();
  const index = lowerText.indexOf(lowerKeyword);

  // 未找到关键词，返回原文本开头
  if (index === -1) {
    return text.slice(0, contextLength * 2) + (text.length > contextLength * 2 ? '...' : '');
  }

  const start = Math.max(0, index - contextLength);
  const end = Math.min(text.length, index + keyword.length + contextLength);

  let result = text.slice(start, end);

  // 添加省略号
  if (start > 0) result = '...' + result;
  if (end < text.length) result = result + '...';

  return result;
}

/**
 * 格式化网站显示，移除 http(s):// 前缀和尾部斜杠
 * @param url 原始 URL
 * @returns 简化后的网站地址
 */
export function formatWebsite(url: string): string {
  return url.replace(/^https?:\/\//, '').replace(/\/$/, '');
}
