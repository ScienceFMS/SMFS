<template>
  <div class="home-container">
    <header class="header">
      <h1>管理员控制台</h1>
      <div class="user-info">
        <span>欢迎，{{ username }}</span>
        <button class="logout-btn" @click="logout">登出</button>
      </div>
    </header>

    <div class="content">
      <div class="tabs">
        <div
          class="tab"
          :class="{ active: activeTab === 'dashboard' }"
          @click="setActiveTab('dashboard')"
        >
          系统概览
        </div>
        <div
          class="tab"
          :class="{ active: activeTab === 'search' }"
          @click="setActiveTab('search')"
        >
          多维查询
        </div>
        <div
          class="tab"
          :class="{ active: activeTab === 'workload' }"
          @click="setActiveTab('workload')"
        >
          工作量查询
        </div>
        <div
          class="tab"
          :class="{ active: activeTab === 'statistic' }"
          @click="setActiveTab('statistic')"
        >
          统计报表
        </div>
        <div
          class="tab"
          :class="{ active: activeTab === 'analysis' }"
          @click="setActiveTab('analysis')"
        >
          科研分析
        </div>
        <!-- <div
          class="tab"
          :class="{ active: activeTab === 'users' }"
          @click="setActiveTab('users')"
        >
          用户管理
        </div> -->
        <!-- <div
          class="tab"
          :class="{ active: activeTab === 'settings' }"
          @click="setActiveTab('settings')"
        >
          系统设置
        </div> -->
      </div>

      <!-- 系统概览 -->
      <div v-if="activeTab === 'dashboard'" class="tab-content">
        <admin-dashboard />
      </div>

      <!-- 多维查询 -->
      <div v-if="activeTab === 'search'" class="tab-content">
        <admin-multi-search />
      </div>

      <!-- 用户管理
      <div v-if="activeTab === 'users'" class="tab-content">
        <admin-user-management />
      </div> -->
      <!-- 工作量查询 -->
      <div v-if="activeTab === 'workload'" class="tab-content">
        <admin-workload />
      </div>
      <!-- 统计报表 -->
      <div v-if="activeTab === 'statistic'" class="tab-content">
        <admin-statistic />
      </div>
      <!-- 科研分析 -->
      <div v-if="activeTab === 'analysis'" class="tab-content">
        <research-analysis />
      </div>
      <!-- 系统设置
      <div v-if="activeTab === 'settings'" class="tab-content">
        <div class="settings-container">
          <h2>系统设置</h2>
          <p>系统设置功能待实现...</p>
        </div>
      </div> -->
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import * as auth from "../utils/auth";
import AdminDashboard from "./admin/AdminDashboard.vue";
import AdminMultiSearch from "./admin/AdminMultiSearch.vue";
import AdminUserManagement from "./admin/AdminUserManagement.vue";
import AdminWorkload from "./admin/AdminWorkload.vue";
import ResearchAnalysis from "./admin/ResearchAnalysis.vue";
import AdminStatistic from "./admin/AdminStatistic.vue";

const router = useRouter();
const route = useRoute();
const username = ref("");
const activeTab = ref("dashboard");

// 从路由参数中获取初始activeTab
onMounted(() => {
  const user = auth.getCurrentUser();
  if (user) {
    username.value = user.username;
  }

  // 如果路由中有参数，设置对应的标签页
  if (route.params.activeTab) {
    setActiveTab(route.params.activeTab);
  }
});

// 设置当前活动标签
const setActiveTab = (tab) => {
  activeTab.value = tab;
};

// 登出
const logout = () => {
  auth.logout();
  router.push("/login");
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

/* 标签页相关样式 */
.tabs {
  display: flex;
  border-bottom: 1px solid #ddd;
  margin-bottom: 20px;
}

.tab {
  padding: 10px 20px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
}

.tab.active {
  color: #1890ff;
  border-bottom: 2px solid #1890ff;
}

.tab:hover:not(.active) {
  color: #40a9ff;
}

.tab-content {
  margin-bottom: 30px;
}

.settings-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style> 