<template>
  <div class="dashboard-container">
    <h2>系统概览</h2>
    
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon user-icon">
          <i class="fas fa-users"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.userCount || 0 }}</div>
          <div class="stat-label">用户数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon ip-icon">
          <i class="fas fa-lightbulb"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.intellectualPropertyCount || 0 }}</div>
          <div class="stat-label">知识产权</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon award-icon">
          <i class="fas fa-trophy"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.awardCount || 0 }}</div>
          <div class="stat-label">奖项</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon project-icon">
          <i class="fas fa-project-diagram"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.projectCount || 0 }}</div>
          <div class="stat-label">科研项目</div>
        </div>
      </div>
    </div>
    
    <div class="features">
      <h3>系统功能</h3>
      <div class="feature-cards">
        <div class="feature-card" @click="goToMultiSearch">
          <div class="feature-icon"><i class="fas fa-search"></i></div>
          <h4>多维查询</h4>
          <p>通过人员、类别、年度等条件检索科研成果</p>
        </div>
        
        <div class="feature-card" @click="goToUserManagement">
          <div class="feature-icon"><i class="fas fa-user-cog"></i></div>
          <h4>用户管理</h4>
          <p>管理系统用户账号和权限</p>
        </div>
        
        <div class="feature-card" @click="goToSettings">
          <div class="feature-icon"><i class="fas fa-cog"></i></div>
          <h4>系统设置</h4>
          <p>配置系统参数和权限规则</p>
        </div>
      </div>
    </div>
    
    <div class="recent-activity">
      <h3>最近活动</h3>
      <div v-if="loading" class="loading">
        <div class="loading-spinner">加载中...</div>
      </div>
      <div v-else-if="activities.length === 0" class="no-data">
        暂无活动记录
      </div>
      <div v-else class="activity-list">
        <div v-for="(activity, index) in activities" :key="index" class="activity-item">
          <div class="activity-icon">
            <i :class="getActivityIcon(activity.type)"></i>
          </div>
          <div class="activity-content">
            <div class="activity-header">
              <span class="activity-user">{{ activity.userName }}</span>
              <span class="activity-time">{{ formatDateTime(activity.time) }}</span>
            </div>
            <div class="activity-description">{{ activity.description }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import * as adminApi from '../../utils/adminApi';

const router = useRouter();
const loading = ref(true);
const stats = ref({
  userCount: 0,
  intellectualPropertyCount: 0,
  awardCount: 0,
  projectCount: 0
});
const activities = ref([]);

// 获取系统概览数据
const fetchSystemOverview = async () => {
  loading.value = true;
  try {
    const response = await adminApi.getSystemOverview();
    if (response.code === 200 && response.data) {
      stats.value = response.data.stats || {};
      activities.value = response.data.recentActivities || [];
    } else {
      console.error('获取系统概览数据返回错误:', response.message || '未知错误');
      stats.value = {
        userCount: 0,
        intellectualPropertyCount: 0,
        awardCount: 0,
        projectCount: 0
      };
      activities.value = [];
    }
  } catch (error) {
    console.error('获取系统概览数据失败:', error);
    stats.value = {
      userCount: 0,
      intellectualPropertyCount: 0,
      awardCount: 0,
      projectCount: 0
    };
    activities.value = [];
  } finally {
    loading.value = false;
  }
};

// 根据活动类型获取对应的图标
const getActivityIcon = (type) => {
  switch (type) {
    case 'LOGIN':
      return 'fas fa-sign-in-alt';
    case 'CREATE':
      return 'fas fa-plus';
    case 'UPDATE':
      return 'fas fa-edit';
    case 'DELETE':
      return 'fas fa-trash';
    case 'EXPORT':
      return 'fas fa-file-export';
    default:
      return 'fas fa-info-circle';
  }
};

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '';
  const date = new Date(dateTimeStr);
  return date.toLocaleString('zh-CN');
};

// 页面跳转函数
const goToMultiSearch = () => {
  router.push({ name: 'AdminHome', params: { activeTab: 'search' } });
};

const goToUserManagement = () => {
  router.push({ name: 'AdminHome', params: { activeTab: 'users' } });
};

const goToSettings = () => {
  router.push({ name: 'AdminHome', params: { activeTab: 'settings' } });
};

onMounted(() => {
  fetchSystemOverview();
});
</script>

<style scoped>
.dashboard-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  font-size: 1.6rem;
  color: #333;
}

h3 {
  margin: 20px 0 15px;
  font-size: 1.3rem;
  color: #333;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 1.5rem;
  color: #fff;
}

.user-icon {
  background-color: #1890ff;
}

.ip-icon {
  background-color: #52c41a;
}

.award-icon {
  background-color: #fa8c16;
}

.project-icon {
  background-color: #722ed1;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 600;
  color: #333;
  line-height: 1.2;
}

.stat-label {
  font-size: 0.9rem;
  color: #666;
}

/* 功能卡片 */
.feature-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.feature-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.feature-card:hover {
  background-color: #f0f0f0;
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 2rem;
  margin-bottom: 15px;
  color: #1890ff;
}

.feature-card h4 {
  font-size: 1.2rem;
  margin-bottom: 10px;
  color: #333;
}

.feature-card p {
  color: #666;
  font-size: 0.9rem;
}

/* 活动列表 */
.recent-activity {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
}

.loading, .no-data {
  padding: 30px;
  text-align: center;
  color: #666;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #e6f7ff;
  color: #1890ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.activity-content {
  flex: 1;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.activity-user {
  font-weight: 500;
  color: #333;
}

.activity-time {
  font-size: 0.8rem;
  color: #999;
}

.activity-description {
  color: #666;
  font-size: 0.9rem;
}
</style> 