<template>
  <v-snackbar
    v-model="snackbarStore.show"
    :color="snackbarStore.type"
    :timeout="snackbarStore.timeout"
    location="top"
    multi-line
    style="z-index: 9999"
  >
    <div class="snackbar-content">
      <v-icon class="mr-2">{{ snackbarStore.icon }}</v-icon>
      <span class="snackbar-text">{{ snackbarStore.text }}</span>
    </div>
  </v-snackbar>
</template>

<script setup lang="ts">
import { useSnackbarStore } from '@/stores/modules/snackbar'

const snackbarStore = useSnackbarStore()
</script>

<style scoped>
.snackbar-content {
  display: flex;
  align-items: flex-start;
}

.snackbar-text {
  word-break: break-word;
  white-space: pre-wrap;
}
</style>
