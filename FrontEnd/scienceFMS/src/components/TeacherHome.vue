<template>
  <div class="common-layout">
    <el-container class="container">
      <!-- 左侧导航栏 -->
      <el-aside width="200px" class="aside">
        <div class="logo">
          <h2>科研管理系统</h2>
        </div>
        <!-- 菜单上部分：功能项 -->
        <el-menu
          :default-active="activeMenu"
          class="menu"
          background-color="#001529"
          text-color="#fff"
          active-text-color="#409eff"
          router
        >
          <el-menu-item index="/teacher/personal-info">
            <el-icon><User /></el-icon>
            <span>个人基本信息</span>
          </el-menu-item>
          <el-menu-item index="/teacher/research-projects">
            <el-icon><Files /></el-icon>
            <span>科研项目</span>
          </el-menu-item>
          <el-menu-item index="/teacher/awards">
            <el-icon><Trophy /></el-icon>
            <span>获奖信息</span>
          </el-menu-item>
          <el-menu-item index="/teacher/patents">
            <el-icon><Document /></el-icon>
            <span>专利著作</span>
          </el-menu-item>
          <el-menu-item index="/teacher/visits">
            <el-icon><Location /></el-icon>
            <span>出访记录</span>
          </el-menu-item>
          <el-menu-item index="/teacher/research-summary">
            <el-icon><ChatDotRound /></el-icon>
            <span>科研成果摘要</span>
          </el-menu-item>
        </el-menu>
        
        <!-- 底部功能区 -->
        <div class="bottom-menu">
          <el-menu
            background-color="#001529"
            text-color="#fff"
            active-text-color="#409eff"
            router
          >
            <el-menu-item index="/teacher/settings">
              <el-icon><Setting /></el-icon>
              <span>设置</span>
            </el-menu-item>
            <el-menu-item @click="logout">
              <el-icon><SwitchButton /></el-icon>
              <span>登出</span>
            </el-menu-item>
          </el-menu>
        </div>
      </el-aside>
      
      <!-- 右侧内容区 -->
      <el-container class="right-container">
        <el-header class="header">
          <div class="header-content">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/teacher' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ breadcrumbTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
            <div class="user-info">
              <el-dropdown>
                <span class="user-dropdown">
                  <el-avatar :size="32" :src="avatarUrl" icon="Avatar"></el-avatar>
                  <span class="username">{{ username }}</span>
                  <el-icon><CaretBottom /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="router.push('/teacher/personal-info')">个人资料</el-dropdown-item>
                    <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        
        <el-main class="main">
          <!-- 嵌套路由渲染区域 -->
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import * as auth from '../utils/auth';
import {
  User, Files, Trophy, Document, Location, Setting, SwitchButton, CaretBottom, ChatDotRound
} from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const username = ref('');
const avatarUrl = ref('');

// 计算当前激活的菜单项
const activeMenu = computed(() => {
  return route.path;
});

// 计算面包屑标题
const breadcrumbTitle = computed(() => {
  const pathMap = {
    '/teacher/personal-info': '个人基本信息',
    '/teacher/research-projects': '科研项目',
    '/teacher/awards': '获奖信息',
    '/teacher/patents': '专利著作',
    '/teacher/research-summary': '科研成果摘要',
    '/teacher/visits': '出访记录',
    '/teacher/settings': '设置'
  };
  return pathMap[route.path] || '教师工作台';
});

onMounted(() => {
  const user = auth.getCurrentUser();
  if (user) {
    username.value = user.username;
  }
  
  // 从localStorage获取头像URL
  try {
    const storedAvatarUrl = localStorage.getItem('userAvatarUrl');
    if (storedAvatarUrl) {
      avatarUrl.value = storedAvatarUrl;
    }
  } catch (error) {
    console.error('无法访问localStorage:', error);
  }
});

const logout = () => {
  auth.logout();
  router.push('/login');
};
</script>

<style scoped>
.container {
  min-height: 100vh;
  width: 100%;
  display: flex;
}

.aside {
  background-color: #001529;
  color: white;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  z-index: 1000;
  width: 200px;
}

.right-container {
  flex: 1;
  display: flex;
  margin-left: 190px;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  color: white;
}

.menu {
  border-right: none;
  flex: 1;
}

.bottom-menu {
  margin-top: auto;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  width: 100%;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin: 0 10px;
}

.main {
  background-color: #f5f5f5;
  padding: 20px;
  flex: 1;
  width: 100%;
  box-sizing: border-box;
}
</style> 