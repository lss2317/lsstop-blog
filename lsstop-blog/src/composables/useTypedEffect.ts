import { ref, onUnmounted } from 'vue';
import { getHitokoto } from '@/apis/hitokoto';

export function useTypedEffect(fallbackText = '欢迎来到我的博客') {
  const output = ref('');

  let timer: ReturnType<typeof setTimeout> | null = null;
  let index = 0;
  let text = '';
  let isDeleting = false;

  function typeEffect() {
    if (!text) return;

    if (!isDeleting) {
      output.value = text.slice(0, index + 1);
      index++;
      if (index >= text.length) {
        isDeleting = true;
        timer = setTimeout(typeEffect, 3000);
        return;
      }
    } else {
      output.value = text.slice(0, index);
      index--;
      if (index < 0) {
        isDeleting = false;
        index = 0;
        fetchText();
        return;
      }
    }
    timer = setTimeout(typeEffect, isDeleting ? 50 : 300);
  }

  async function fetchText() {
    try {
      text = await getHitokoto();
    } catch {
      text = fallbackText;
    }
    index = 0;
    isDeleting = false;
    typeEffect();
  }

  function start() {
    fetchText();
  }

  function stop() {
    if (timer) {
      clearTimeout(timer);
      timer = null;
    }
  }

  onUnmounted(stop);

  return { output, start, stop };
}
