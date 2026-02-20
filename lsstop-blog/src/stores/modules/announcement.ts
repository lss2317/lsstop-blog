import { defineStore } from 'pinia';
import { shallowRef, computed } from 'vue';
import { listAnnouncement, AnnouncementType, type AnnouncementVo } from '@/apis/announcement';

const useAnnouncementStore = defineStore('announcement', () => {
  const list = shallowRef<AnnouncementVo[]>([]);
  const isLoaded = shallowRef(false);

  // 弹窗公告（type=1 或 type=3）
  const popupList = computed(() =>
    list.value.filter(
      (item) => item.type === AnnouncementType.POPUP || item.type === AnnouncementType.BOTH,
    ),
  );

  // 首页展示公告（type=2 或 type=3）
  const homeList = computed(() =>
    list.value.filter(
      (item) => item.type === AnnouncementType.HOME || item.type === AnnouncementType.BOTH,
    ),
  );

  // 获取公告列表（已有数据则不重复请求）
  async function fetchAnnouncementList() {
    if (isLoaded.value) return;
    const res = await listAnnouncement();
    list.value = res.data;
    isLoaded.value = true;
  }

  return {
    list,
    isLoaded,
    popupList,
    homeList,
    fetchAnnouncementList,
  };
});

export default useAnnouncementStore;
