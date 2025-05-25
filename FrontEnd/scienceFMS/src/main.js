import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'

// 创建Vue应用并使用路由
createApp(App)
  .use(router)
  .mount('#app')
