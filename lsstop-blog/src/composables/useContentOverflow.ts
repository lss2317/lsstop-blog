import { reactive, nextTick, onUnmounted } from 'vue';

/**
 * 内容溢出检测 composable
 * 使用 ResizeObserver 监听元素高度变化，自动判断是否溢出
 * @param maxHeight 最大高度，超过则视为溢出，默认 162px（约6行）
 */
export function useContentOverflow(maxHeight: number = 162) {
  const overflowMap = reactive<Record<number, boolean>>({});
  const observers = new Map<number, ResizeObserver>();
  const elements = new Map<number, HTMLElement>();

  const checkOverflow = (id: number, el: HTMLElement) => {
    overflowMap[id] = el.scrollHeight > maxHeight;
  };

  const observe = (id: number, el: HTMLElement | null) => {
    // 清理旧的 observer
    if (observers.has(id)) {
      observers.get(id)?.disconnect();
      observers.delete(id);
    }

    if (!el) {
      elements.delete(id);
      return;
    }

    elements.set(id, el);

    const observer = new ResizeObserver(() => {
      requestAnimationFrame(() => {
        const isOverflow = el.scrollHeight > maxHeight;
        if (overflowMap[id] !== isOverflow) {
          overflowMap[id] = isOverflow;
        }
      });
    });

    observer.observe(el);
    observers.set(id, observer);

    // 首次检测
    void nextTick(() => checkOverflow(id, el));
  };

  const reset = () => {
    observers.forEach((obs) => obs.disconnect());
    observers.clear();
    elements.clear();
    Object.keys(overflowMap).forEach((key) => delete overflowMap[Number(key)]);
  };

  onUnmounted(() => {
    observers.forEach((obs) => obs.disconnect());
    observers.clear();
  });

  return {
    overflowMap,
    observe,
    reset,
  };
}
