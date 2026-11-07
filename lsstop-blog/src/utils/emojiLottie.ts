import type { AnimationItem } from 'lottie-web';

const LOTTIE_SELECTOR = '.comment-emoji-lottie[data-emoji-lottie]';
const animations = new WeakMap<HTMLElement, AnimationItem>();
const pendingElements = new WeakSet<HTMLElement>();

let observer: MutationObserver | null = null;
let lottiePromise: Promise<typeof import('lottie-web').default> | null = null;

function prefersReducedMotion(): boolean {
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
}

function getLottiePlayer(): Promise<typeof import('lottie-web').default> {
  if (!lottiePromise) {
    lottiePromise = import('lottie-web').then((module) => module.default);
  }
  return lottiePromise;
}

export async function preloadEmojiLottiePlayer(): Promise<void> {
  if (typeof window === 'undefined' || prefersReducedMotion()) return;
  await getLottiePlayer();
}

function renderFallback(element: HTMLElement): void {
  const fallbackUrl = element.dataset.emojiFallback;
  if (!fallbackUrl) return;

  const image = document.createElement('img');
  image.src = fallbackUrl;
  image.alt = '';
  image.decoding = 'async';
  image.draggable = false;
  element.replaceChildren(image);
}

async function mountLottieEmoji(element: HTMLElement): Promise<void> {
  if (animations.has(element) || pendingElements.has(element) || prefersReducedMotion()) {
    return;
  }

  const path = element.dataset.emojiLottie;
  if (!path) return;

  pendingElements.add(element);

  try {
    const lottie = await getLottiePlayer();
    if (!element.isConnected || animations.has(element)) return;

    element.replaceChildren();
    const animation = lottie.loadAnimation({
      container: element,
      renderer: 'svg',
      loop: true,
      autoplay: true,
      path,
      rendererSettings: {
        preserveAspectRatio: 'xMidYMid meet',
      },
    });

    animations.set(element, animation);

    const handleFailure = () => {
      if (animations.get(element) !== animation) return;
      animation.destroy();
      animations.delete(element);
      renderFallback(element);
    };

    animation.addEventListener('data_failed', handleFailure);
    animation.addEventListener('error', handleFailure);
  } catch {
    renderFallback(element);
  } finally {
    pendingElements.delete(element);
  }
}

function scanForLottie(root: ParentNode): void {
  if (root instanceof HTMLElement && root.matches(LOTTIE_SELECTOR)) {
    void mountLottieEmoji(root);
  }

  root
    .querySelectorAll<HTMLElement>(LOTTIE_SELECTOR)
    .forEach((element) => void mountLottieEmoji(element));
}

function destroyLottieWithin(root: Node): void {
  const destroyElement = (element: HTMLElement) => {
    const animation = animations.get(element);
    if (!animation) return;
    animation.destroy();
    animations.delete(element);
  };

  if (root instanceof HTMLElement && root.matches(LOTTIE_SELECTOR)) {
    destroyElement(root);
  }

  if (root instanceof Element) {
    root.querySelectorAll<HTMLElement>(LOTTIE_SELECTOR).forEach(destroyElement);
  }
}

export function startEmojiLottieObserver(): void {
  if (observer || typeof document === 'undefined' || !document.body) return;

  scanForLottie(document);

  observer = new MutationObserver((mutations) => {
    mutations.forEach((mutation) => {
      mutation.removedNodes.forEach(destroyLottieWithin);
      mutation.addedNodes.forEach((node) => {
        if (node instanceof Element) scanForLottie(node);
      });
    });
  });

  observer.observe(document.body, {
    childList: true,
    subtree: true,
  });
}

export function stopEmojiLottieObserver(): void {
  if (!observer) return;
  observer.disconnect();
  observer = null;
  destroyLottieWithin(document.body);
}

if (import.meta.hot) {
  import.meta.hot.dispose(stopEmojiLottieObserver);
}
