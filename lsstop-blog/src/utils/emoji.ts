import EmojiList from '@/constants/emoji';
import type { EmojiAsset } from '@/constants/emoji';
import { escapeHtml } from '@/utils/format';

export interface ParseEmojiOptions {
  /** 聊天室模式：单个超级表情独立放大，混排超级表情使用静态小图。 */
  chatMode?: boolean;
}

function canPlayEmojiMotion(): boolean {
  return (
    typeof window === 'undefined' || !window.matchMedia('(prefers-reduced-motion: reduce)').matches
  );
}

function getStandaloneSuperEmoji(content: string): EmojiAsset | null {
  const emoji = EmojiList[content.trim()];
  return emoji?.category === 'super' ? emoji : null;
}

export function isStandaloneSuperEmoji(content: string | null | undefined): boolean {
  return Boolean(content && getStandaloneSuperEmoji(content));
}

/**
 * 将表情文本转换为 APNG 图片或 Lottie 容器。
 * 先转义HTML特殊字符，防止XSS攻击。
 * 统一换行符、裁剪首尾空行、限制连续空行最多2个，最后将换行转为<br>。
 */
export function parseEmoji(content: string, options: ParseEmojiOptions = {}): string {
  if (!content) return '';
  const shouldAnimate = canPlayEmojiMotion();
  const standaloneSuper = options.chatMode ? getStandaloneSuperEmoji(content) : null;

  const escaped = escapeHtml(standaloneSuper ? standaloneSuper.token : content);
  return escaped
    .replace(/\[([^\]]+)]/g, (match) => {
      const emoji = EmojiList[match];
      if (!emoji) return match;

      const isStandalone = standaloneSuper?.token === emoji.token;
      // QQ 行内规则：普通/特殊超级表情仍可小尺寸播放；大尺寸接龙表情
      // (qqType=3) 在评论或混排时使用静态图，聊天室单独发送时才放大播放。
      const allowAnimation =
        shouldAnimate && (emoji.category !== 'super' || emoji.qqType !== 3 || isStandalone);
      const className = isStandalone
        ? 'comment-emoji comment-emoji--standalone comment-emoji--super'
        : 'comment-emoji';
      const width = isStandalone ? emoji.standaloneWidth : 20;
      const height = isStandalone ? emoji.standaloneHeight : 20;
      const style = 'width:' + width + 'px;height:' + height + 'px';

      if (emoji.animationType === 'lottie' && allowAnimation) {
        return (
          '<span class="' +
          className +
          ' comment-emoji-lottie" style="' +
          style +
          '"' +
          ' data-emoji-lottie="' +
          emoji.animatedUrl +
          '" data-emoji-fallback="' +
          emoji.staticUrl +
          '" role="img" aria-label="' +
          emoji.token +
          '">' +
          '<img src="' +
          emoji.staticUrl +
          '" alt="" decoding="async" draggable="false" />' +
          '</span>'
        );
      }

      const url =
        emoji.animationType === 'apng' && allowAnimation ? emoji.animatedUrl : emoji.staticUrl;

      return (
        '<img src="' +
        url +
        '" alt="' +
        emoji.token +
        '" class="' +
        className +
        '" width="' +
        width +
        '" height="' +
        height +
        '" style="' +
        style +
        '"' +
        ' loading="lazy" decoding="async" draggable="false" />'
      );
    })
    .replace(/\r\n/g, '\n')
    .replace(/^\n+|\n+$/g, '')
    .replace(/\n{3,}/g, '\n\n')
    .replace(/\n/g, '<br>');
}
