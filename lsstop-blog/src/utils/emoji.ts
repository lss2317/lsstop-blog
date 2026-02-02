import EmojiList from '@/constants/emoji'

/**
 * 将表情文本转换为图片
 * [微笑] -> <img src="..." />
 */
export function parseEmoji(content: string): string {
  if (!content) return ''
  return content.replace(/\[([^\]]+)\]/g, (match) => {
    const url = EmojiList[match]
    return url ? `<img src="${url}" alt="${match}" class="comment-emoji" />` : match
  })
}
