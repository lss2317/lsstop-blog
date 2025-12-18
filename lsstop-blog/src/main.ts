import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify.ts'

import 'animate.css'
import './assets/css/index.css'
import './assets/css/markdown.css'
// import './assets/css/player.css'
import './assets/css/vue-social-share/client.css'
import './assets/css/icon/iconfont.css'
import 'highlight.js/styles/atom-one-dark.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(vuetify).mount('#app')
