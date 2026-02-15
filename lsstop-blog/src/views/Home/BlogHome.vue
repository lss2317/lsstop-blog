<template>
  <div>
    <br />
    <br />
    <br />
    <br />
    <br />
    <br />
    <br />
    <br />
    <TalkSwiper :list="talkList" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import TalkSwiper from '@/components/Swiper/TalkSwiper.vue';
import { listTalk } from '@/apis/talk';
import { parseEmoji } from '@/utils/emoji';

const talkList = ref<string[]>([]);

onMounted(async () => {
  const res = await listTalk();
  talkList.value = res.data.map((item) => parseEmoji(item.content));
});
</script>

<style scoped></style>
