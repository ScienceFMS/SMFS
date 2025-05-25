<template>
  <div class="home-container">
    <header class="header">
      <h1>教师工作台</h1>
      <div class="user-info">
        <span>欢迎，{{ username }}</span>
        <button class="logout-btn" @click="logout">登出</button>
      </div>
    </header>
    
    <div class="content">
      <h2>科研成果管理系统 - 教师页面</h2>
      <p>这里是教师可以访问的功能区域</p>
      
      <div class="features">
        <router-link to="/Keyan" class="feature-card">
          <h3>我的科研成果</h3>
          <p>查看和管理您的科研成果记录</p>
        </router-link>
        <div class="feature-card">
          <h3>数据统计</h3>
          <p>查看个人科研数据统计和报表</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import * as auth from '../utils/auth';

const router = useRouter();
const username = ref('');

onMounted(() => {
  const user = auth.getCurrentUser();
  if (user) {
    username.value = user.username;
  }
});

const logout = () => {
  auth.logout();
  router.push('/login');
};
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logout-btn {
  padding: 6px 15px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  background-color: #e6e6e6;
}

.content {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

h1 {
  margin: 0;
  font-size: 1.8rem;
  color: #1890ff;
}

h2 {
  margin-bottom: 20px;
  font-size: 1.6rem;
  color: #333;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.feature-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.feature-card h3 {
  font-size: 1.2rem;
  margin-bottom: 10px;
  color: #1890ff;
}
</style> 