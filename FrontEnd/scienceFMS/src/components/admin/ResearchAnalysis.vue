<template>
  <div class="research-analysis-container">
    <h2 class="title">科研成果分析</h2>
    
    <!-- 筛选条件 -->
    <div class="filter-section">
      <div class="filter-header">
        <h3>时间范围筛选</h3>
      </div>
      <div class="time-range-filter">
        <el-form :model="filterForm" :inline="true">
          <el-form-item label="起始年份">
            <el-date-picker
              v-model="filterForm.startYear"
              type="year"
              placeholder="选择起始年份"
              value-format="YYYY"
              format="YYYY年"
              :disabled="loading"
            />
          </el-form-item>
          <el-form-item label="结束年份">
            <el-date-picker
              v-model="filterForm.endYear"
              type="year"
              placeholder="选择结束年份"
              value-format="YYYY"
              format="YYYY年"
              :disabled="loading"
            />
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              :loading="loading" 
              @click="fetchResearchAnalysis"
            >
              分析
            </el-button>
            <el-button 
              type="success" 
              :disabled="!analysisResult || loading" 
              @click="exportToPdf"
            >
              导出为PDF
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    
    <!-- 分析结果显示 -->
    <div class="analysis-result" id="analysis-result-container">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="15" animated />
        <div class="loading-text">正在分析科研数据，这可能需要一些时间...</div>
      </div>
      
      <div v-else-if="!analysisResult" class="empty-result">
        <el-empty description="暂无分析数据，请选择时间范围并点击分析按钮"/>
      </div>
      
      <div v-else class="result-content">
        <div class="result-header">
          <h3>{{ filterForm.startYear }} - {{ filterForm.endYear }} 科研成果分析报告</h3>
          <div class="result-time">生成时间：{{ formatDate(new Date()) }}</div>
        </div>
        
        <!-- 使用v-html渲染大模型分析结果，因为结果可能包含格式化文本 -->
        <div class="result-body markdown-content" v-html="formattedAnalysisResult"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import * as adminApi from '../../utils/adminApi';
import { marked } from 'marked';
import html2pdf from 'html2pdf.js';

// 当前年份
const currentYear = new Date().getFullYear();

// 筛选表单
const filterForm = ref({
  startYear: '2020',
  endYear: currentYear.toString()
});

// 分析结果和加载状态
const analysisResult = ref('');
const loading = ref(false);

// 格式化分析结果为HTML（支持markdown解析）
const formattedAnalysisResult = computed(() => {
  if (!analysisResult.value) return '';
  
  // 使用marked库将markdown转换为HTML
  try {
    return marked(analysisResult.value);
  } catch (error) {
    console.error('Markdown解析错误:', error);
    return analysisResult.value;
  }
});

// 获取科研分析数据
const fetchResearchAnalysis = async () => {
  // 表单验证
  if (!filterForm.value.startYear || !filterForm.value.endYear) {
    ElMessage.warning('请选择起始年份和结束年份');
    return;
  }
  
  // 验证年份范围
  const startYear = parseInt(filterForm.value.startYear, 10);
  const endYear = parseInt(filterForm.value.endYear, 10);
  
  if (startYear > endYear) {
    ElMessage.warning('起始年份不能大于结束年份');
    return;
  }
  
  loading.value = true;
  analysisResult.value = '';
  
  try {
    const params = {
      startYear: startYear,
      endYear: endYear
    };
    
    const response = await adminApi.getResearchAnalysis(params);
    
    if (response.code === 200 && response.data) {
      analysisResult.value = response.data;
      ElMessage.success('分析完成');
    } else {
      ElMessage.error('获取分析结果失败：' + (response.message || '未知错误'));
    }
  } catch (error) {
    console.error('获取分析数据出错:', error);
    ElMessage.error('分析出错：' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

// 格式化日期
const formatDate = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// 导出为PDF
const exportToPdf = () => {
  if (!analysisResult.value) {
    ElMessage.warning('没有分析结果可导出');
    return;
  }
  
  const element = document.getElementById('analysis-result-container');
  
  const opt = {
    margin: 10,
    filename: `科研分析报告_${filterForm.value.startYear}-${filterForm.value.endYear}.pdf`,
    image: { type: 'jpeg', quality: 0.98 },
    html2canvas: { scale: 2 },
    jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
  };
  
  ElMessage.info('正在生成PDF，请稍候...');
  
  // 使用html2pdf库将内容导出为PDF
  html2pdf().from(element).set(opt).save().then(() => {
    ElMessage.success('PDF已生成');
  }).catch(err => {
    console.error('PDF生成失败:', err);
    ElMessage.error('PDF生成失败');
  });
};

// 页面加载时执行
onMounted(() => {
  // 初始不自动加载数据，等待用户点击分析按钮
});
</script>

<style scoped>
.research-analysis-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title {
  margin-bottom: 20px;
  font-size: 1.8rem;
  color: #303133;
}

/* 筛选区域样式 */
.filter-section {
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.filter-header {
  margin-bottom: 15px;
}

.filter-header h3 {
  font-size: 1.2rem;
  margin: 0;
  color: #606266;
}

.time-range-filter {
  display: flex;
  align-items: center;
}

/* 分析结果区域 */
.analysis-result {
  background-color: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  min-height: 500px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.loading-text {
  margin-top: 20px;
  color: #909399;
  font-size: 14px;
}

.empty-result {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.result-content {
  padding: 10px;
}

.result-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.result-header h3 {
  margin: 0 0 10px 0;
  font-size: 1.4rem;
  color: #303133;
}

.result-time {
  font-size: 0.9rem;
  color: #909399;
}

.result-body {
  font-size: 1rem;
  line-height: 1.6;
  color: #606266;
}

/* Markdown 内容样式 */
:deep(.markdown-content) {
  line-height: 1.7;
}

:deep(.markdown-content h1),
:deep(.markdown-content h2),
:deep(.markdown-content h3) {
  margin-top: 25px;
  margin-bottom: 15px;
  color: #303133;
}

:deep(.markdown-content h1) {
  font-size: 1.8rem;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
}

:deep(.markdown-content h2) {
  font-size: 1.5rem;
}

:deep(.markdown-content h3) {
  font-size: 1.3rem;
}

:deep(.markdown-content p) {
  margin-bottom: 15px;
}

:deep(.markdown-content ul),
:deep(.markdown-content ol) {
  margin-bottom: 15px;
  padding-left: 25px;
}

:deep(.markdown-content li) {
  margin-bottom: 5px;
}

:deep(.markdown-content blockquote) {
  border-left: 4px solid #dcdfe6;
  padding: 0 15px;
  color: #909399;
  margin: 15px 0;
}

:deep(.markdown-content code) {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 2px 5px;
  font-family: monospace;
}

:deep(.markdown-content pre) {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  overflow: auto;
  margin: 15px 0;
}

:deep(.markdown-content table) {
  width: 100%;
  border-collapse: collapse;
  margin: 15px 0;
}

:deep(.markdown-content th),
:deep(.markdown-content td) {
  padding: 8px;
  border: 1px solid #ebeef5;
  text-align: left;
}

:deep(.markdown-content th) {
  background-color: #f5f7fa;
}
</style> 