<template>
  <div class="awards">
    <div class="page-header">
      <h2 class="page-title">获奖信息</h2>
      <el-button type="primary">
        <el-icon><Plus /></el-icon>新增获奖
      </el-button>
    </div>
    
    <el-card class="awards-card">
      <el-empty description="获奖信息功能正在开发中，敬请期待！">
        <el-button type="primary">敬请期待</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue';
</script>

<style scoped>
.awards {
  padding: 20px 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-weight: 500;
  color: #303133;
}

.awards-card {
  padding: 40px 0;
}
</style>
