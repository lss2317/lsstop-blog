import { ref, watch, type Ref } from 'vue';

/**
 * 响应式本地存储
 * @param key 存储键名
 * @param defaultValue 默认值
 */
export function useLocalStorage<T>(key: string, defaultValue: T): Ref<T> {
  // 读取初始值
  const readValue = (): T => {
    try {
      const item = localStorage.getItem(key);
      return item ? JSON.parse(item) : defaultValue;
    } catch {
      return defaultValue;
    }
  };

  const storedValue = ref<T>(readValue()) as Ref<T>;

  // 监听变化自动保存
  watch(
    storedValue,
    (newValue) => {
      try {
        localStorage.setItem(key, JSON.stringify(newValue));
      } catch {
        console.error(`Failed to save ${key} to localStorage`);
      }
    },
    { deep: true },
  );

  return storedValue;
}
