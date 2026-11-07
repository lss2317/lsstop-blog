<template>
  <div class="loading-spinner" role="status" aria-label="加载中">
    <div class="jump-loader" aria-hidden="true"></div>
  </div>
</template>

<style scoped>
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.jump-loader {
  position: relative;
  width: 48px;
  height: 65px;
  flex: none;
}

.jump-loader::before {
  position: absolute;
  top: 60px;
  left: 0;
  width: 48px;
  height: 5px;
  border-radius: 50%;
  background: var(--color-primary, #49b1f5);
  content: '';
  opacity: 0.5;
  animation: loader-shadow 0.5s linear infinite;
}

.jump-loader::after {
  position: absolute;
  top: 0;
  left: 0;
  width: 48px;
  height: 48px;
  border-radius: 4px;
  background: var(--color-primary, #49b1f5);
  content: '';
  animation: loader-jump 0.5s linear infinite;
}

@keyframes loader-jump {
  15% {
    border-bottom-right-radius: 3px;
  }

  25% {
    transform: translateY(9px) rotate(22.5deg);
  }

  50% {
    border-bottom-right-radius: 40px;
    transform: translateY(18px) scale(1, 0.9) rotate(45deg);
  }

  75% {
    transform: translateY(9px) rotate(67.5deg);
  }

  100% {
    transform: translateY(0) rotate(90deg);
  }
}

@keyframes loader-shadow {
  0%,
  100% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.2, 1);
  }
}

@media (prefers-reduced-motion: reduce) {
  .jump-loader::before,
  .jump-loader::after {
    animation-duration: 1.2s;
  }
}
</style>
