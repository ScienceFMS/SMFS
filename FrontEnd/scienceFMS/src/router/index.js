import { createRouter, createWebHistory } from 'vue-router';
import Login from '../components/Login.vue';
import TeacherHome from '../components/TeacherHome.vue';
import AdminHome from '../components/AdminHome.vue';
import Keyan from '../components/Keyan.vue';
const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/teacher',
    name: 'TeacherHome',
    component: TeacherHome,
    meta: { requiresAuth: true, role: 'teacher' }
  },
  {
    path: '/admin',
    name: 'AdminHome',
    component: AdminHome,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/Keyan',
    name: 'Keyan',
    component: Keyan,
    meta: { requiresAuth: true, role: 'teacher' }
  }

];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 全局导航守卫实现权限控制
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : null;

  // 判断页面是否需要登录权限
  if (to.meta.requiresAuth) {
    // 如果需要登录权限但用户未登录，重定向到登录页
    if (!isLoggedIn) {
      next({ name: 'Login' });
      return;
    }

    // 如果页面有特定角色要求，检查用户角色
    if (to.meta.role && to.meta.role !== user?.role) {
      alert('您没有权限访问此页面');
      // 根据用户角色重定向到对应首页
      if (user.role === 'teacher') {
        next({ name: 'TeacherHome' });
      } else if (user.role === 'admin') {
        next({ name: 'AdminHome' });
      } else {
        next({ name: 'Login' });
      }
      return;
    }
  }

  // 如果用户已登录且尝试访问登录页，重定向到对应首页
  if (isLoggedIn && to.name === 'Login') {
    if (user.role === 'teacher') {
      next({ name: 'TeacherHome' });
    } else if (user.role === 'admin') {
      next({ name: 'AdminHome' });
    } else {
      next();
    }
    return;
  }

  next();
});

export default router; 