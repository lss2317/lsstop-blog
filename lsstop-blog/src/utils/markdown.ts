import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

/**
 * HTML 特殊字符转义
 */
export const escapeHtml = (str: string): string => {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

/**
 * Markdown 转 HTML（带代码高亮和行号）
 */
export const markdownToHtml = (content: string): string => {
  const md = new MarkdownIt({
    html: true,
    linkify: true,
    typographer: true,
    highlight: (str: string, lang: string): string => {
      const linesLength = str.split(/\n/).length - 1
      let linesNum = '<span aria-hidden="true" class="line-numbers-rows">'
      for (let index = 0; index < linesLength; index++) {
        linesNum += '<span></span>'
      }
      linesNum += '</span>'
      if (lang && hljs.getLanguage(lang)) {
        const preCode = hljs.highlight(str, { language: lang, ignoreIllegals: true }).value
        let html = preCode
        if (linesLength) {
          html += '<b class="name">' + lang + '</b>'
        }
        return `<pre class="hljs"><code>${html}</code>${linesNum}</pre>`
      }
      return `<pre class="hljs"><code>${escapeHtml(str)}</code>${linesNum}</pre>`
    },
  })
  return md.render(content)
}
