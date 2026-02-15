import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';

/**
 * HTML 特殊字符转义
 */
export const escapeHtml = (str: string): string => {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
};

/**
 * 生成代码块行号
 */
const generateLineNumbers = (code: string): string => {
  const lines = code.split('\n');
  // 处理末尾换行符的情况
  const linesCount = code.endsWith('\n') ? lines.length - 1 : lines.length;
  if (linesCount <= 0) return '';

  const lineSpans = Array(linesCount).fill('<span></span>').join('');
  return `<span aria-hidden="true" class="line-numbers-rows">${lineSpans}</span>`;
};

/**
 * 生成复制按钮
 */
const generateCopyButton = (code: string): string => {
  return `<button class="copy-btn" data-clipboard-text="${escapeHtml(code)}"><i class="iconfont iconfuzhi"></i></button>`;
};

/**
 * 代码高亮处理函数
 */
const highlightCode = (str: string, lang: string): string => {
  const lineNumbers = generateLineNumbers(str);
  const copyBtn = generateCopyButton(str);
  const linesCount = str.split('\n').length - (str.endsWith('\n') ? 1 : 0);

  // 有语言标识且 hljs 支持该语言
  if (lang && hljs.getLanguage(lang)) {
    const highlightedCode = hljs.highlight(str, { language: lang, ignoreIllegals: true }).value;
    const langLabel = linesCount > 0 ? `<b class="name">${lang}</b>` : '';
    return `<pre class="hljs">${copyBtn}<code>${highlightedCode}${langLabel}</code>${lineNumbers}</pre>`;
  }

  // 无语言标识或不支持的语言，直接转义输出
  return `<pre class="hljs">${copyBtn}<code>${escapeHtml(str)}</code>${lineNumbers}</pre>`;
};

// 创建单例 MarkdownIt 实例，避免重复创建
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: highlightCode,
});

/**
 * Markdown 转 HTML（带代码高亮和行号）
 */
export const markdownToHtml = (content: string): string => {
  return md.render(content);
};

/**
 * 去除 Markdown 语法，提取纯文本
 */
export const stripMarkdown = (content: string): string => {
  return content
    .replace(/```[\s\S]*?```/g, '') // 代码块
    .replace(/`[^`]+`/g, '') // 行内代码
    .replace(/!?\[[^\]]*\]\([^)]*\)/g, '') // 图片和链接
    .replace(/<[^>]+>/g, '') // HTML标签
    .replace(/[#*_~>\-|]/g, '') // Markdown符号
    .replace(/\s+/g, ''); // 空白
};
