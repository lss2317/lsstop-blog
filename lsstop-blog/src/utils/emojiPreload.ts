import type { EmojiAsset } from '@/constants/emoji';

const MAX_BACKGROUND_REQUESTS = 3;
const loadedUrls = new Set<string>();
const inFlight = new Map<string, Promise<boolean>>();
const queuedTokens = new Set<string>();
const queue: EmojiAsset[] = [];

let activeBackgroundRequests = 0;

function preloadImage(url: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const image = new Image();
    image.decoding = 'async';
    image.onload = () => resolve();
    image.onerror = () => reject(new Error('Failed to preload emoji image'));
    image.src = url;
  });
}

async function preloadJson(url: string): Promise<void> {
  const response = await fetch(url, {
    cache: 'force-cache',
    credentials: 'omit',
  });
  if (!response.ok) throw new Error('Failed to preload emoji animation');
  await response.arrayBuffer();
}

function removeQueuedEmoji(token: string): void {
  if (!queuedTokens.delete(token)) return;
  const index = queue.findIndex((emoji) => emoji.token === token);
  if (index >= 0) queue.splice(index, 1);
}

function startPreload(emoji: EmojiAsset): Promise<boolean> {
  if (emoji.animationType === 'static' || loadedUrls.has(emoji.animatedUrl)) {
    return Promise.resolve(true);
  }

  const existing = inFlight.get(emoji.animatedUrl);
  if (existing) return existing;

  const request = (
    emoji.animationType === 'lottie'
      ? preloadJson(emoji.animatedUrl)
      : preloadImage(emoji.animatedUrl)
  )
    .then(() => {
      loadedUrls.add(emoji.animatedUrl);
      return true;
    })
    .catch(() => false)
    .finally(() => {
      inFlight.delete(emoji.animatedUrl);
    });

  inFlight.set(emoji.animatedUrl, request);
  return request;
}

function drainQueue(): void {
  while (activeBackgroundRequests < MAX_BACKGROUND_REQUESTS && queue.length > 0) {
    const emoji = queue.shift();
    if (!emoji) return;
    queuedTokens.delete(emoji.token);

    if (loadedUrls.has(emoji.animatedUrl) || inFlight.has(emoji.animatedUrl)) continue;

    activeBackgroundRequests += 1;
    void startPreload(emoji).finally(() => {
      activeBackgroundRequests -= 1;
      drainQueue();
    });
  }
}

/** 低优先级预加载；限制并发，避免动态资源阻塞面板静态图。 */
export function queueEmojiAnimationPreload(emoji: EmojiAsset): void {
  if (
    emoji.animationType === 'static' ||
    loadedUrls.has(emoji.animatedUrl) ||
    inFlight.has(emoji.animatedUrl) ||
    queuedTokens.has(emoji.token)
  ) {
    return;
  }

  queuedTokens.add(emoji.token);
  queue.push(emoji);
  drainQueue();
}

/** 悬停时立即提权加载，并与已经进行中的请求复用同一个 Promise。 */
export function preloadEmojiAnimation(emoji: EmojiAsset): Promise<boolean> {
  removeQueuedEmoji(emoji.token);
  return startPreload(emoji);
}
