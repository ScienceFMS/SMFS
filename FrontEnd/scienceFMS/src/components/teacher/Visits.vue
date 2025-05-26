<template>
  <div class="visits">
    <div class="page-header">
      <h2 class="page-title">出访记录</h2>
      <el-button type="primary">
        <el-icon><Plus /></el-icon>新增出访
      </el-button>
    </div>
    
    <el-card class="visits-card">
      <el-empty description="出访记录功能正在开发中，敬请期待！">
        <el-button type="primary">敬请期待</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue';
</script>

<style scoped>
.visits {
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

.visits-card {
  padding: 40px 0;
}
</style> 