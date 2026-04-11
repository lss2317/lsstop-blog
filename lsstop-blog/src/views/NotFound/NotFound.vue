<template>
  <div class="not-found-wrapper">
    <v-card class="blog-container not-found-card animate__animated animate__zoomIn">
      <div class="not-found-body">
        <div class="not-found-icon">
          <v-icon size="80" color="var(--color-primary)">mdi-compass-off-outline</v-icon>
        </div>
        <h1 class="not-found-code">404</h1>
        <h2 class="not-found-title">页面不存在</h2>
        <p class="not-found-desc">抱歉，你访问的页面不存在，请检查网址是否正确</p>
        <v-btn
          class="home-btn"
          color="var(--color-primary)"
          variant="flat"
          size="large"
          rounded="pill"
          @click="goHome"
        >
          <v-icon start>mdi-home-outline</v-icon>
          返回首页
        </v-btn>
      </div>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { useNavigate } from '@/composables/useNavigate';

const { navigateTo } = useNavigate();

function goHome() {
  navigateTo('/');
}
</script>

<style scoped>
.not-found-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: main 1s;
}

.not-found-card {
  margin: 40px auto !important;
  max-width: 80%;
  width: 100%;
  padding: 80px 40px;
  min-height: 400px;
}

.not-found-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.not-found-icon {
  margin-bottom: 24px;
  animation: float 3s ease-in-out infinite;
}

.not-found-code {
  font-size: 10rem;
  font-weight: 700;
  color: var(--color-primary);
  letter-spacing: 10px;
  margin: 0;
  line-height: 1;
}

.not-found-title {
  font-size: 2rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 24px 0 16px;
}

.not-found-desc {
  font-size: 1.1rem;
  color: var(--color-text-tertiary);
  margin-bottom: 40px;
  line-height: 1.8;
}

.home-btn {
  font-size: 1.1rem;
  padding: 0 48px;
  height: 50px;
  letter-spacing: 1px;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.home-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(73, 177, 245, 0.4);
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-12px);
  }
}

@keyframes main {
  0% {
    opacity: 0;
    transform: translateY(50px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 759px) {
  .not-found-card {
    margin: 20px 5px !important;
    padding: 60px 14px;
    min-height: 320px;
  }

  .not-found-code {
    font-size: 6rem;
    letter-spacing: 6px;
  }

  .not-found-title {
    font-size: 1.5rem;
  }

  .not-found-desc {
    font-size: 0.95rem;
  }

  .home-btn {
    padding: 0 36px;
    height: 46px;
  }
}
</style>

<style>
/* 夜间模式样式 */
.v-theme--dark .not-found-card {
  background-color: #3a3a3a !important;
}

.v-theme--dark .not-found-title {
  color: rgba(255, 255, 255, 0.85);
}

.v-theme--dark .not-found-desc {
  color: rgba(255, 255, 255, 0.5);
}
</style>
