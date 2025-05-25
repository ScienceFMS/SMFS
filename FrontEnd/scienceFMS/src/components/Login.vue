<template>
  <div class="login-container">
    <div class="login-form">
      <h2>系统登录</h2>
      
      <div class="form-group">
        <label for="username">用户名</label>
        <input 
          type="text" 
          id="username" 
          v-model="username" 
          placeholder="请输入用户名"
          @keyup.enter="login"
        />
      </div>
      
      <div class="form-group">
        <label for="password">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="password" 
          placeholder="请输入密码" 
          @keyup.enter="login"
        />
      </div>
      
      <div class="form-group">
        <label>角色选择</label>
        <div class="role-options">
          <label>
            <input type="radio" v-model="role" value="teacher" />
            教师
          </label>
          <label>
            <input type="radio" v-model="role" value="admin" />
            管理员
          </label>
        </div>
      </div>
      
      <div v-if="errorMsg" class="error-message">
        {{ errorMsg }}
      </div>
      
      <div class="form-actions">
        <button class="login-button" @click="login" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
        <a href="#" class="forgot-password" @click.prevent="forgotPassword">忘记密码?</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import * as auth from '../utils/auth';
import * as api from '../utils/api';

const router = useRouter();
const username = ref('');
const password = ref('');
const role = ref('teacher');
const errorMsg = ref('');
const loading = ref(false);

const login = async () => {
  // 清除之前的错误信息
  errorMsg.value = '';
  
  // 验证用户名和密码不能为空
  if (!username.value || !password.value) {
    errorMsg.value = '用户名和密码不能为空';
    return;
  }
  
  try {
    loading.value = true;
    
    // 准备登录请求数据
    const loginData = {
      username: username.value,
      password: password.value,
      role: role.value
    };
    
    // 调用后端登录API
    const result = await api.login(loginData);
    
    // 检查登录结果
    if (result.code === 200) {
      // 登录成功，保存用户信息和token
      auth.login(result.data);
      
      // 根据角色跳转到不同页面
      if (result.data.role === 'admin') {
        router.push('/admin');
      } else {
        router.push('/teacher');
      }
    } else if (result.code === 1002) {
      // 账号锁定
      errorMsg.value = '账号已锁定，请联系管理员';
    } else {
      // 其他错误
      errorMsg.value = result.message || '账号或密码错误';
    }
  } catch (error) {
    console.error('登录失败:', error);
    errorMsg.value = '登录失败，请稍后再试';
  } finally {
    loading.value = false;
  }
};

const forgotPassword = () => {
  // 实现忘记密码功能
  alert('请联系系统管理员重置密码');
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-form {
  width: 360px;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.role-options {
  display: flex;
  gap: 20px;
}

.role-options label {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 16px;
  font-size: 14px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.login-button {
  padding: 10px 24px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.login-button:hover:not(:disabled) {
  background-color: #40a9ff;
}

.login-button:disabled {
  background-color: #b2d6f5;
  cursor: not-allowed;
}

.forgot-password {
  color: #1890ff;
  text-decoration: none;
  font-size: 14px;
}

.forgot-password:hover {
  text-decoration: underline;
}
</style> 