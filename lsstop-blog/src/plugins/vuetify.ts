import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

// 优先读取用户设置，否则跟随系统偏好
const getDefaultTheme = () => {
  const saved = localStorage.getItem('theme')
  if (saved) return saved
  // 检测系统偏好
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}
const savedTheme = getDefaultTheme()

const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: savedTheme,
  },
})

export default vuetify
