import EmojiList from '@/constants/emoji';
import { escapeHtml } from '@/utils/format';

/**
 * 将表情文本转换为图片
 * [微笑] -> <img src="..." />
 * 先转义HTML特殊字符，防止XSS攻击
 */
export function parseEmoji(content: string): string {
  if (!content) return '';
  // 先转义HTML特殊字符
  const escaped = escapeHtml(content);
  // 再替换表情
  return escaped.replace(/\[([^\]]+)]/g, (match) => {
    const url = EmojiList[match];
    return url ? `<img src="${url}" alt="${match}" class="comment-emoji" />` : match;
  });
}
