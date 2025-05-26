<template>
  <div class="settings">
    <div class="page-header">
      <h2 class="page-title">设置</h2>
    </div>
    
    <el-card class="settings-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="个人设置" name="personal">
          <h3>账号设置</h3>
          <el-form :model="personalForm" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="personalForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="修改密码">
              <el-button type="primary">修改密码</el-button>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="personalForm.email"></el-input>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="personalForm.phone"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="savePersonalSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="通知设置" name="notification">
          <h3>通知偏好</h3>
          <el-form :model="notificationForm" label-width="180px">
            <el-form-item label="接收系统通知">
              <el-switch v-model="notificationForm.systemNotify"></el-switch>
            </el-form-item>
            <el-form-item label="接收项目截止提醒">
              <el-switch v-model="notificationForm.deadlineNotify"></el-switch>
            </el-form-item>
            <el-form-item label="接收科研成果更新提醒">
              <el-switch v-model="notificationForm.achievementNotify"></el-switch>
            </el-form-item>
            <el-form-item label="接收邮件通知">
              <el-switch v-model="notificationForm.emailNotify"></el-switch>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveNotificationSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="系统设置" name="system">
          <h3>系统偏好</h3>
          <el-form :model="systemForm" label-width="100px">
            <el-form-item label="语言">
              <el-select v-model="systemForm.language">
                <el-option label="简体中文" value="zh-CN"></el-option>
                <el-option label="English" value="en-US"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="主题">
              <el-select v-model="systemForm.theme">
                <el-option label="默认主题" value="default"></el-option>
                <el-option label="暗色主题" value="dark"></el-option>
                <el-option label="高对比度" value="high-contrast"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="表格密度">
              <el-radio-group v-model="systemForm.tableDensity">
                <el-radio label="default">默认</el-radio>
                <el-radio label="medium">中等</el-radio>
                <el-radio label="small">紧凑</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSystemSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';

// 当前激活的标签页
const activeTab = ref('personal');

// 个人设置表单
const personalForm = reactive({
  username: '张教授',
  email: 'zhang@example.edu.cn',
  phone: '1391234567'
});

// 通知设置表单
const notificationForm = reactive({
  systemNotify: true,
  deadlineNotify: true,
  achievementNotify: true,
  emailNotify: false
});

// 系统设置表单
const systemForm = reactive({
  language: 'zh-CN',
  theme: 'default',
  tableDensity: 'default'
});

// 保存个人设置
const savePersonalSettings = () => {
  ElMessage.success('个人设置保存成功');
};

// 保存通知设置
const saveNotificationSettings = () => {
  ElMessage.success('通知设置保存成功');
};

// 保存系统设置
const saveSystemSettings = () => {
  ElMessage.success('系统设置保存成功');
};
</script>

<style scoped>
.settings {
  padding: 20px 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-weight: 500;
  color: #303133;
}

.settings-card {
  margin-bottom: 20px;
}

h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-weight: 500;
  color: #303133;
}
</style> 