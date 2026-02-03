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
    .replace(/'/g, '&#39;')
}

/**
 * 调整textarea高度，自动擑开贴合内容
 * @param textarea textarea元素
 * @param maxHeight 最大高度，默认200px
 */
export function adjustTextareaHeight(textarea: HTMLTextAreaElement, maxHeight = 200): void {
  textarea.style.height = 'auto'
  if (textarea.scrollHeight > maxHeight) {
    textarea.style.height = maxHeight + 'px'
    textarea.style.overflowY = 'auto'
  } else {
    textarea.style.height = textarea.scrollHeight + 'px'
    textarea.style.overflowY = 'hidden'
  }
}
