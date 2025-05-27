import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'

// 引入Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

// 引入权限检测工具
import * as auth from './utils/auth'
import { ElMessage } from 'element-plus'

// 全局定期检查token是否有效
const checkAuthStatus = () => {
  if (auth.isAuthenticated()) {
    const token = auth.getToken();
    if (token) {
      try {
        // 解析JWT的过期时间（不验证签名，只解析）
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        
        const payload = JSON.parse(jsonPayload);
        // 检查是否过期（exp是UNIX时间戳，单位为秒）
        if (payload.exp && payload.exp < Date.now() / 1000) {
          console.log('JWT已过期，正在登出并跳转到登录页面');
          auth.logout();
          if (router.currentRoute.value.meta.requiresAuth) {
            window.location.href = '/login';
            ElMessage.error('登录已过期，请重新登录');
          }
        }
      } catch (e) {
        console.error('解析JWT时出错:', e);
      }
    }
  }
};

// 初始检查
checkAuthStatus();
// 每分钟检查一次
setInterval(checkAuthStatus, 60000);

// 注册全局错误处理器
window.addEventListener('unhandledrejection', (event) => {
  if (event.reason && 
      (event.reason.message === '登录已过期，请重新登录' || 
       (event.reason.response && event.reason.response.status === 401))) {
    console.log('捕获到未处理的401错误，即将跳转到登录页');
    auth.logout();
    window.location.href = '/login';
  }
});

// 创建Vue应用并使用路由和Element Plus
createApp(App)
  .use(router)
  .use(ElementPlus, {
    locale: zhCn,
  })
  .mount('#app')
