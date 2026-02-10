import EmojiList from '@/constants/emoji';
import { escapeHtml } from '@/utils/format';

/**
 * 将表情文本转换为图片
 * [微笑] -> <img src="..." />
 * 先转义HTML特殊字符，防止XSS攻击
 * 统一换行符、裁剪首尾空行、限制连续空行最多2个，最后将换行转为<br>
 */
export function parseEmoji(content: string): string {
  if (!content) return '';
  // 先转义HTML特殊字符
  const escaped = escapeHtml(content);
  return escaped
    .replace(/\[([^\]]+)]/g, (match) => {
      const url = EmojiList[match];
      return url ? `<img src="${url}" alt="${match}" class="comment-emoji" />` : match;
    })
    .replace(/\r\n/g, '\n')
    .replace(/^\n+|\n+$/g, '')
    .replace(/\n{3,}/g, '\n\n')
    .replace(/\n/g, '<br>');
}
