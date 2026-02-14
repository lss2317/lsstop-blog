<template>
  <div class="rightSide" :class="{ visible: isVisible }">
    <div class="rightSideConfigHide" :class="{ expanded: isExpanded }">
      <i :class="'iconfont rightSideIcon ' + icon" @click="toggleTheme" />
    </div>
    <div class="settingContainer" @click="toggleSettings">
      <i class="iconfont iconshezhi setting" />
    </div>
    <i @click="backTop" class="iconfont rightSideIcon iconziyuanldpi" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useTheme } from 'vuetify';

const theme = useTheme();

const isVisible = ref(false);
const isExpanded = ref(false);
// 根据当前主题初始化图标
const icon = ref(theme.global.current.value.dark ? 'icontaiyang' : 'iconyueliang');

const backTop = () => {
  window.scrollTo({ behavior: 'smooth', top: 0 });
};

const handleScroll = () => {
  isVisible.value = window.scrollY > 20;
};

const toggleSettings = () => {
  isExpanded.value = !isExpanded.value;
};

const toggleTheme = () => {
  const isDark = theme.global.current.value.dark;
  const newTheme = isDark ? 'light' : 'dark';
  document.documentElement.classList.add('theme-transition');
  theme.global.name.value = newTheme;
  icon.value = isDark ? 'iconyueliang' : 'icontaiyang';
  localStorage.setItem('theme', newTheme);
  setTimeout(() => {
    document.documentElement.classList.remove('theme-transition');
  }, 400);
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style scoped>
.rightSide {
  z-index: 4;
  position: fixed;
  right: -38px;
  bottom: 85px;
  opacity: 0;
  transition:
    opacity 0.5s,
    transform 0.5s;
}

.rightSide.visible {
  opacity: 1;
  transform: translateX(-38px);
}

.rightSideConfigHide {
  transform: translateX(30px);
  transition: transform 0.3s;
}

.rightSideConfigHide.expanded {
  transform: translateX(0);
}

.rightSideIcon,
.settingContainer {
  display: block;
  margin-bottom: 2px;
  width: 30px;
  height: 30px;
  background-color: #49b1f5;
  color: #fff;
  text-align: center;
  font-size: 16px;
  line-height: 30px;
  cursor: pointer;
}

.rightSideIcon:hover,
.settingContainer:hover {
  background-color: #ff7242;
}

.settingContainer i {
  display: block;
  animation: turnAround 2s linear infinite;
}

@keyframes turnAround {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
