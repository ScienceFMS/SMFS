import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'

// 引入Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

// 创建Vue应用并使用路由和Element Plus
createApp(App)
  .use(router)
  .use(ElementPlus, {
    locale: zhCn,
  })
  .mount('#app')
