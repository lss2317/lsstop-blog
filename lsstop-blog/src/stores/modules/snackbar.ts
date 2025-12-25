import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

type SnackbarType = 'success' | 'error' | 'warning' | 'info'

export const useSnackbarStore = defineStore('snackbar', () => {
  const show = ref(false)
  const text = ref('')
  const type = ref<SnackbarType>('info')
  const timeout = ref(2000)

  const icon = computed(() => {
    const icons: Record<SnackbarType, string> = {
      error: 'mdi-alert-circle',
      success: 'mdi-check-circle',
      warning: 'mdi-alert',
      info: 'mdi-information',
    }
    return icons[type.value]
  })

  function showMessage(message: string, messageType: SnackbarType = 'info', duration = 2000) {
    text.value = message
    type.value = messageType
    timeout.value = duration
    show.value = true
  }

  function success(message: string, duration = 2000) {
    showMessage(message, 'success', duration)
  }

  function error(message: string, duration = 2000) {
    showMessage(message, 'error', duration)
  }

  function warning(message: string, duration = 2000) {
    showMessage(message, 'warning', duration)
  }

  function info(message: string, duration = 2000) {
    showMessage(message, 'info', duration)
  }

  return {
    show,
    text,
    type,
    timeout,
    icon,
    showMessage,
    success,
    error,
    warning,
    info,
  }
})
