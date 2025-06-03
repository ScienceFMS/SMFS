<template>
  <div class="research-summary">
    <h2>科研成果摘要</h2>
    
    <el-card class="summary-card">
      <div class="card-header">
        <h3>科研成果信息</h3>
        <el-button type="primary" @click="generateSummary">生成摘要</el-button>
      </div>
      
      <el-form label-position="top">
        <el-form-item label="科研成果详情">
          <el-input
            v-model="researchDetails"
            type="textarea"
            :rows="8"
            placeholder="请输入您的科研成果详细信息..."
          />
        </el-form-item>
      </el-form>
      
      <el-divider content-position="center">生成结果</el-divider>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>
      
      <div v-else-if="summary" class="summary-result">
        <h4>摘要结果</h4>
        <div class="summary-content">{{ summary }}</div>
        <div class="action-buttons">
          <el-button type="primary" @click="copySummary">复制</el-button>
          <el-button type="success" @click="downloadSummary">下载</el-button>
        </div>
      </div>
      
      <div v-else class="empty-result">
        <el-empty description="暂无摘要，请输入科研成果并点击生成" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

const researchDetails = ref('');
const summary = ref('');
const loading = ref(false);

const generateSummary = async () => {
  if (!researchDetails.value.trim()) {
    ElMessage.warning('请先输入科研成果详情');
    return;
  }
  
  loading.value = true;
  try {
    // 模拟调用大模型API生成摘要
    // 实际项目中，这里应该调用后端API，后端再调用大模型
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // 模拟返回结果
    summary.value = `这是基于您提供的科研成果生成的摘要：\n\n${researchDetails.value.substring(0, 100)}...\n\n该研究具有重要的学术价值和应用前景，主要创新点包括...（此处为模拟数据）`;
    
    ElMessage.success('摘要生成成功');
  } catch (error) {
    console.error('生成摘要失败:', error);
    ElMessage.error('生成摘要失败，请稍后重试');
  } finally {
    loading.value = false;
  }
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
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
}
</style> 