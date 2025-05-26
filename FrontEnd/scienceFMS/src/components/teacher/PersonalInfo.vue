<template>
  <div class="personal-info">
    <h2 class="page-title">个人基本信息</h2>
    
    <el-card class="info-card" v-loading="loading">
      <div class="card-header">
        <div class="user-avatar">
          <el-avatar :size="80" :src="userInfo.avatarUrl" icon="User"></el-avatar>
        </div>
        <div class="profile-header">
          <h3>{{ userInfo.realName }}</h3>
          <p>{{ userInfo.department }} | {{ userInfo.title }}</p>
          <el-button type="primary" size="small">编辑资料</el-button>
        </div>
      </div>
      
      <el-divider></el-divider>
      
      <el-descriptions title="基本信息" :column="2" border>
        <el-descriptions-item label="工号">{{ userInfo.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ userInfo.realName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ userInfo.gender }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ formatDate(userInfo.birthDate) }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ userInfo.title }}</el-descriptions-item>
        <el-descriptions-item label="所属院系">{{ userInfo.department }}</el-descriptions-item>
        <el-descriptions-item label="研究方向">{{ userInfo.researchArea }}</el-descriptions-item>
        <el-descriptions-item label="电子邮箱">{{ userInfo.email }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ userInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="办公地点">{{ userInfo.officeLocation }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider></el-divider>
      
      <div class="section">
        <h4>教育背景</h4>
        <el-timeline v-if="userInfo.educationList && userInfo.educationList.length > 0">
          <el-timeline-item
            v-for="(edu, index) in userInfo.educationList"
            :key="index"
            :timestamp="edu.period"
            placement="top"
          >
            <el-card>
              <h4>{{ edu.degree }} - {{ edu.institution }}</h4>
              <p>{{ edu.major }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无教育背景信息"></el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { User } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { getTeacherProfile } from '../../utils/api';
import * as auth from '../../utils/auth';

const loading = ref(true);
const userInfo = ref({
  id: '',
  realName: '',
  gender: '',
  birthDate: '',
  title: '',
  department: '',
  position: '',
  researchArea: '',
  email: '',
  phone: '',
  officeLocation: '',
  avatarUrl: '',
  educationList: []
});

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  
  try {
    const date = new Date(dateStr);
    return date.toLocaleDateString('zh-CN');
  } catch (error) {
    return dateStr;
  }
};

// 获取教师个人资料
const fetchUserInfo = async () => {
  loading.value = true;
  console.log('[PersonalInfo] 开始请求教师个人资料...');
  try {
    const response = await getTeacherProfile();
    console.log('[PersonalInfo] 获取个人资料响应:', response);
    if (response.success) {
      userInfo.value = response.data;
    } else {
      ElMessage.error('获取个人信息失败');
    }
  } catch (error) {
    console.error('[PersonalInfo] 获取用户信息失败:', error);
    ElMessage.error('获取个人信息失败，请稍后重试');
    
    // 开发环境：如果API未连接，使用本地模拟数据
    if (process.env.NODE_ENV === 'development') {
      simulateUserData();
    }
  } finally {
    loading.value = false;
  }
};

// 模拟用户数据（仅用于开发环境）
const simulateUserData = () => {
  console.log('[PersonalInfo] 使用模拟数据');
  userInfo.value = {
    id: '10001',
    username: 'teacher1',
    realName: '张教授',
    gender: '男',
    birthDate: '1975-01-15',
    title: '教授',
    department: '计算机科学与技术学院',
    position: '教授',
    researchArea: '人工智能、大数据分析',
    email: 'zhang@example.edu.cn',
    phone: '13911111111',
    officeLocation: '科研楼A座301室',
    avatarUrl: '',
    educationList: [
      {
        id: 1,
        degree: '博士',
        institution: '北京大学',
        major: '计算机科学',
        startYear: 2000,
        endYear: 2005,
        period: '2000-2005'
      },
      {
        id: 2,
        degree: '硕士',
        institution: '清华大学',
        major: '软件工程',
        startYear: 1997,
        endYear: 2000,
        period: '1997-2000'
      },
      {
        id: 3,
        degree: '学士',
        institution: '浙江大学',
        major: '计算机科学与技术',
        startYear: 1993,
        endYear: 1997,
        period: '1993-1997'
      }
    ]
  };
};

onMounted(() => {
  console.log('[PersonalInfo] 组件已挂载');
  // 检查用户是否已登录
  if (auth.isAuthenticated()) {
    console.log('[PersonalInfo] 用户已认证，获取个人资料');
    fetchUserInfo();
  } else {
    console.log('[PersonalInfo] 用户未认证');
    // 开发环境：如果未登录，使用本地模拟数据
    if (process.env.NODE_ENV === 'development') {
      simulateUserData();
      loading.value = false;
    } else {
      // 生产环境：未登录则跳转到登录页
      ElMessage.warning('请先登录');
      loading.value = false;
    }
  }
});
</script>

<style scoped>
.personal-info {
  padding: 20px 0;
}

.page-title {
  margin-bottom: 20px;
  font-weight: 500;
  color: #303133;
}

.info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  padding: 20px 0;
}

.user-avatar {
  margin-right: 30px;
}

.profile-header {
  flex: 1;
}

.profile-header h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 20px;
  color: #303133;
}

.profile-header p {
  margin-top: 0;
  margin-bottom: 15px;
  color: #606266;
}

.section {
  margin-top: 20px;
}

.section h4 {
  margin-bottom: 15px;
  font-weight: 500;
  color: #303133;
}

:deep(.el-timeline-item__node) {
  background-color: #409EFF;
}
</style> 