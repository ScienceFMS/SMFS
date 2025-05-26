<template>
  <div class="patents">
    <div class="page-header">
      <h2 class="page-title">专利著作</h2>
      <el-button type="primary">
        <el-icon><Plus /></el-icon>新增专利/著作
      </el-button>
    </div>
    
    <el-card class="patents-card">
      <el-empty description="专利著作功能正在开发中，敬请期待！">
        <el-button type="primary">敬请期待</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue';
</script>

<style scoped>
.patents {
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

.patents-card {
  padding: 40px 0;
}
</style> 