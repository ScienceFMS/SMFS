<template>
  <div class="research-summary">
    <h2>科研成果摘要</h2>
    
    <el-card class="summary-card">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>
      
      <div v-else-if="summary" class="summary-result">
        <h4>教师科研成果摘要</h4>
        <div class="summary-content">{{ summary }}</div>
        <div class="action-buttons">
          <el-button type="primary" @click="copySummary">复制</el-button>
          <el-button type="success" @click="downloadSummary">下载</el-button>
          <el-button type="warning" @click="refreshSummary">刷新</el-button>
        </div>
      </div>
      
      <div v-else class="empty-result">
        <el-empty description="暂无科研成果摘要，请点击生成按钮" />
        <div class="generate-button">
          <el-button type="primary" @click="generateSummary">生成摘要</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getResearchSummary } from '../../utils/api';

const summary = ref('');
const loading = ref(false);

// 组件挂载时不再自动获取摘要

// 获取当前教师的科研成果摘要
const fetchResearchSummary = async () => {
  loading.value = true;
  try {
    // 从localStorage获取用户信息
    const teacherId = localStorage.getItem('teacherId');
    const response = await getResearchSummary(teacherId);
    
    if (response.code === 200) {
      summary.value = response.data;
      ElMessage.success('科研成果摘要获取成功');
    } else {
      throw new Error(response.message || '获取摘要失败');
    }
  } catch (error) {
    console.error('获取科研成果摘要失败:', error);
    ElMessage.error(error.message || '获取科研成果摘要失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 生成摘要
const generateSummary = () => {
  fetchResearchSummary();
};

// 刷新摘要
const refreshSummary = () => {
  fetchResearchSummary();
};

const copySummary = () => {
  if (summary.value) {
    navigator.clipboard.writeText(summary.value)
      .then(() => ElMessage.success('摘要已复制到剪贴板'))
      .catch(() => ElMessage.error('复制失败，请手动复制'));
  }
};

const downloadSummary = () => {
  if (summary.value) {
    const blob = new Blob([summary.value], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = '科研成果摘要.txt';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    ElMessage.success('摘要已下载');
  }
};
</script>

<style scoped>
.research-summary {
  padding: 20px;
}

.summary-card {
  margin-top: 20px;
}

.loading-container {
  padding: 20px 0;
}

.summary-result {
  background-color: #f8f9fa;
  border-radius: 4px;
  padding: 15px;
  margin-top: 10px;
}

.summary-content {
  white-space: pre-line;
  line-height: 1.6;
  margin: 15px 0;
}

.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 15px;
}

.empty-result {
  padding: 30px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.generate-button, .refresh-button {
  margin-top: 20px;
}
</style> 