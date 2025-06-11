import { createRouter, createWebHistory } from 'vue-router';
import Login from '../components/Login.vue';
import TeacherHome from '../components/TeacherHome.vue';
import AdminHome from '../components/AdminHome.vue';

// 教师功能页面
import PersonalInfo from '../components/teacher/PersonalInfo.vue';
import ResearchProjects from '../components/teacher/ResearchProjects.vue';
import Awards from '../components/teacher/Awards.vue';
import Patents from '../components/teacher/Patents.vue';
import Visits from '../components/teacher/Visits.vue';
import Settings from '../components/teacher/Settings.vue';
import ResearchSummary from '../components/teacher/ResearchSummary.vue';

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
    meta: { requiresAuth: true, role: 'teacher' },
    redirect: '/teacher/personal-info',
    children: [
      {
        path: 'personal-info',
        name: 'PersonalInfo',
        component: PersonalInfo,
        meta: { requiresAuth: true, role: 'teacher' }
      },
      {
        path: 'research-projects',
        name: 'ResearchProjects',
        component: ResearchProjects,
        meta: { requiresAuth: true, role: 'teacher' }
      },
      {
        path: 'awards',
        name: 'Awards',
        component: Awards,
        meta: { requiresAuth: true, role: 'teacher' }
      },
      {
        path: 'patents',
        name: 'Patents',
        component: Patents,
        meta: { requiresAuth: true, role: 'teacher' }
      },
      {
        path: 'research-summary',
        name: 'ResearchSummary',
        component: ResearchSummary,
        meta: { requiresAuth: true, role: 'teacher' }
      },
      {
        path: 'visits',
        name: 'Visits',
        component: Visits,
        meta: { requiresAuth: true, role: 'teacher' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: Settings,
        meta: { requiresAuth: true, role: 'teacher' }
      }
    ]
  },
  {
    path: '/admin',
    name: 'AdminHome',
    component: AdminHome,
    meta: { requiresAuth: true, role: 'admin' }
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