import { defineStore } from 'pinia';
import { ref } from 'vue';

/**
 * 侧边栏抽屉状态管理
 */
export const useDrawerStore = defineStore('drawer', () => {
  /** 抽屉显示状态 */
  const drawer = ref(false);

  /** 打开抽屉 */
  const openDrawer = () => {
    drawer.value = true;
  };

  /** 关闭抽屉 */
  const closeDrawer = () => {
    drawer.value = false;
  };

  return {
    drawer,
    openDrawer,
    closeDrawer,
  };
});
