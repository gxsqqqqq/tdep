import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { VueQueryPlugin } from '@tanstack/vue-query'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

import './assets/styles/variables.css'
import './assets/styles/base.css'
import './assets/styles/utilities.css'

const app = createApp(App)

// 注册插件
app.use(createPinia())
app.use(router)
app.use(VueQueryPlugin)
app.use(ElementPlus, {
  size: 'default',
  zIndex: 3000,
})

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
