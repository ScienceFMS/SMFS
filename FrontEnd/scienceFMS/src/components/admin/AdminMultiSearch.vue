<template>
  <div class="multi-search-container">
    <h2>多维查询</h2>
    
    <div class="search-filters">
      <div class="filter-group">
        <h3>成果类型</h3>
        <div class="filter-options">
          <label class="filter-option">
            <input type="checkbox" v-model="filters.types.intellectualProperty" />
            知识产权
          </label>
          <label class="filter-option">
            <input type="checkbox" v-model="filters.types.award" />
            奖项
          </label>
          <label class="filter-option">
            <input type="checkbox" v-model="filters.types.researchProject" />
            科研项目
          </label>
        </div>
      </div>
      
      <div class="filter-group">
        <h3>人员筛选</h3>
        <select v-model="filters.teacherId" class="filter-select">
          <option value="">全部教师</option>
          <option v-for="teacher in teachers" :key="teacher.id" :value="teacher.id">
            {{ teacher.realName }}
          </option>
        </select>
      </div>
      
      <div class="filter-group">
        <h3>年份筛选</h3>
        <div class="year-filter">
          <select v-model="filters.startYear" class="filter-select">
            <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
          </select>
          <span>至</span>
          <select v-model="filters.endYear" class="filter-select">
            <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
          </select>
        </div>
      </div>
      
      <div class="filter-group">
        <h3>关键词搜索</h3>
        <input 
          type="text" 
          v-model="filters.keyword" 
          placeholder="请输入搜索关键词" 
          class="filter-input"
        />
      </div>
      
      <div class="filter-actions">
        <button class="search-btn" @click="search">搜索</button>
        <button class="reset-btn" @click="resetFilters">重置</button>
      </div>
    </div>
    
    <div class="search-results">
      <div class="results-header">
        <h3>查询结果 ({{ totalResults }})</h3>
        <div class="results-actions">
          <div class="sort-options">
            <span>排序：</span>
            <select v-model="sortOption" @change="applySorting" class="sort-select">
              <option value="date_desc">日期（新→旧）</option>
              <option value="date_asc">日期（旧→新）</option>
              <option value="name_asc">名称（A-Z）</option>
              <option value="name_desc">名称（Z-A）</option>
            </select>
          </div>
          <button class="export-btn" @click="exportResults">
            导出结果
          </button>
        </div>
      </div>
      
      <div class="results-list" v-if="loading">
        <div class="loading-spinner">数据加载中...</div>
      </div>
      
      <div class="results-list" v-else-if="searchResults.length === 0">
        <div class="no-results">暂无符合条件的数据</div>
      </div>
      
      <div class="results-list" v-else>
        <div 
          v-for="item in searchResults" 
          :key="item.id + '-' + item.entityType" 
          class="result-item"
          :class="getResultItemClass(item.entityType)"
        >
          <div class="result-type-badge" :class="getResultTypeBadgeClass(item.entityType)">
            {{ getResultTypeLabel(item.entityType) }}
          </div>
          
          <div class="result-content">
            <h4 class="result-title">{{ getResultTitle(item) }}</h4>
            <div class="result-meta">
              <div class="meta-item">
                <span class="meta-label">所属教师：</span>
                <span class="meta-value">{{ item.teacherName }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">部门：</span>
                <span class="meta-value">{{ item.department }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">日期：</span>
                <span class="meta-value">{{ getResultDate(item) }}</span>
              </div>
              <div class="meta-item" v-if="item.entityType === 'intellectualProperty'">
                <span class="meta-label">类型：</span>
                <span class="meta-value">{{ item.type === 'PATENT' ? '专利' : '著作权' }} - {{ item.subtype }}</span>
              </div>
              <div class="meta-item" v-if="item.entityType === 'award'">
                <span class="meta-label">等级：</span>
                <span class="meta-value">{{ item.level }}</span>
              </div>
              <div class="meta-item" v-if="item.entityType === 'researchProject'">
                <span class="meta-label">来源：</span>
                <span class="meta-value">{{ item.subtype }}</span>
              </div>
            </div>
          </div>
          
          <div class="result-actions">
            <button class="action-btn" @click="viewDetail(item)">查看详情</button>
          </div>
        </div>
        
        <div class="pagination" v-if="totalPages > 1">
          <button 
            class="page-btn" 
            :disabled="currentPage === 1" 
            @click="changePage(currentPage - 1)"
          >
            上一页
          </button>
          <div class="page-numbers">
            <button 
              v-for="page in pageNumbers" 
              :key="page" 
              class="page-number" 
              :class="{ active: page === currentPage }"
              @click="changePage(page)"
            >
              {{ page }}
            </button>
          </div>
          <button 
            class="page-btn" 
            :disabled="currentPage === totalPages" 
            @click="changePage(currentPage + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
    
    <!-- 详情弹窗 -->
    <div class="dialog-overlay" v-if="showDetailDialog" @click="closeDetailDialog"></div>
    <div class="detail-dialog" v-if="showDetailDialog">
      <div class="dialog-header">
        <h3>详细信息</h3>
        <button class="close-btn" @click="closeDetailDialog"><i class="fas fa-times"></i></button>
      </div>
      <div class="dialog-body" v-if="currentDetail">
        <div class="detail-type-badge" :class="getResultTypeBadgeClass(currentDetail.entityType)">
          {{ getResultTypeLabel(currentDetail.entityType) }}
        </div>
        
        <h4 class="detail-title">{{ getResultTitle(currentDetail) }}</h4>
        
        <div class="detail-section">
          <h5>基本信息</h5>
          <div class="detail-item">
            <span class="detail-label">所属教师：</span>
            <span class="detail-value">{{ currentDetail.teacherName }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">部门：</span>
            <span class="detail-value">{{ currentDetail.department }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">日期：</span>
            <span class="detail-value">{{ getResultDate(currentDetail) }}</span>
          </div>
        </div>
        
        <!-- 知识产权特有字段 -->
        <div class="detail-section" v-if="currentDetail.entityType === 'intellectualProperty'">
          <h5>知识产权信息</h5>
          <div class="detail-item">
            <span class="detail-label">类型：</span>
            <span class="detail-value">{{ currentDetail.type === 'PATENT' ? '专利' : '著作权' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">子类型：</span>
            <span class="detail-value">{{ currentDetail.subtype }}</span>
          </div>
          <div class="detail-item" v-if="currentDetail.applicationNumber">
            <span class="detail-label">申请号：</span>
            <span class="detail-value">{{ currentDetail.applicationNumber }}</span>
          </div>
          <div class="detail-item" v-if="currentDetail.date1">
            <span class="detail-label">申请日期：</span>
            <span class="detail-value">{{ formatDate(currentDetail.date1) }}</span>
          </div>
          <div class="detail-item" v-if="currentDetail.date2">
            <span class="detail-label">授权日期：</span>
            <span class="detail-value">{{ formatDate(currentDetail.date2) }}</span>
          </div>
        </div>
        
        <!-- 奖项特有字段 -->
        <div class="detail-section" v-if="currentDetail.entityType === 'award'">
          <h5>奖项信息</h5>
          <div class="detail-item">
            <span class="detail-label">等级：</span>
            <span class="detail-value">{{ currentDetail.level }}</span>
          </div>
          <div class="detail-item" v-if="currentDetail.organizer">
            <span class="detail-label">颁发单位：</span>
            <span class="detail-value">{{ currentDetail.organizer }}</span>
          </div>
          <div class="detail-item" v-if="currentDetail.mainDate">
            <span class="detail-label">获奖日期：</span>
            <span class="detail-value">{{ formatDate(currentDetail.mainDate) }}</span>
          </div>
        </div>
        
        <!-- 科研项目特有字段 -->
        <div class="detail-section" v-if="currentDetail.entityType === 'researchProject'">
          <h5>项目信息</h5>
          <div class="detail-item">
            <span class="detail-label">类型：</span>
            <span class="detail-value">{{ currentDetail.subtype }}</span>
          </div>
          <div class="detail-item" v-if="currentDetail.projectCode">
            <span class="detail-label">项目编号：</span>
            <span class="detail-value">{{ currentDetail.projectCode }}</span>
          </div>
          <div class="detail-item" v-if="currentDetail.date1">
            <span class="detail-label">开始日期：</span>
            <span class="detail-value">{{ formatDate(currentDetail.date1) }}</span>
          </div>
          <div class="detail-item" v-if="currentDetail.date2">
            <span class="detail-label">结束日期：</span>
            <span class="detail-value">{{ formatDate(currentDetail.date2) }}</span>
          </div>
          <div class="detail-item" v-if="currentDetail.fundingAmount">
            <span class="detail-label">资助金额：</span>
            <span class="detail-value">{{ currentDetail.fundingAmount }} 万元</span>
          </div>
        </div>
        
        <!-- 公共的描述字段 -->
        <div class="detail-section" v-if="currentDetail.description">
          <h5>详细描述</h5>
          <div class="detail-description">
            {{ currentDetail.description }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import * as adminApi from '../../utils/adminApi';

const router = useRouter();
const loading = ref(false);
const searchResults = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalResults = ref(0);
const totalPages = ref(0);
const sortOption = ref('date_desc');
const teachers = ref([]);

// 详情弹窗相关
const showDetailDialog = ref(false);
const currentDetail = ref(null);

const currentYear = new Date().getFullYear();
const yearOptions = computed(() => {
  const years = [];
  for (let year = currentYear; year >= currentYear - 10; year--) {
    years.push(year);
  }
  return years;
});

// 筛选条件
const filters = reactive({
  types: {
    intellectualProperty: true,
    award: true,
    researchProject: true
  },
  teacherId: '',
  startYear: currentYear - 2,
  endYear: currentYear,
  keyword: ''
});

// 计算属性
const pageNumbers = computed(() => {
  const result = [];
  const maxVisible = 5;
  
  if (totalPages.value <= maxVisible) {
    for (let i = 1; i <= totalPages.value; i++) {
      result.push(i);
    }
  } else {
    let start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2));
    let end = Math.min(totalPages.value, start + maxVisible - 1);
    
    if (end - start + 1 < maxVisible) {
      start = Math.max(1, end - maxVisible + 1);
    }
    
    for (let i = start; i <= end; i++) {
      result.push(i);
    }
  }
  
  return result;
});

// 生命周期钩子
onMounted(() => {
  // 加载教师列表
  fetchTeachers();
});

// 获取所有教师列表
const fetchTeachers = async () => {
  try {
    const response = await adminApi.getAllTeachers();
    if (response.code === 200 && response.data) {
      teachers.value = response.data || [];
    } else {
      console.error('获取教师列表返回错误:', response.message || '未知错误');
      teachers.value = [];
    }
  } catch (error) {
    console.error('获取教师列表失败:', error);
    teachers.value = [];
  }
};

// 搜索函数
const search = async () => {
  loading.value = true;
  searchResults.value = [];
  
  try {
    // 定义要查询的类型
    const typesToSearch = [];
    if (filters.types.intellectualProperty) typesToSearch.push('intellectualProperty');
    if (filters.types.award) typesToSearch.push('award');
    if (filters.types.researchProject) typesToSearch.push('researchProject');
    
    // 构建查询参数
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      types: typesToSearch.join(','),
      teacherId: filters.teacherId || undefined,
      startYear: filters.startYear,
      endYear: filters.endYear,
      keyword: filters.keyword || undefined,
      sort: sortOption.value
    };
    
    // 发送请求
    const response = await adminApi.searchResults(params);
    
    // 处理返回数据，这个格式是MybatisPlus的Page对象
    if (response.code === 200 && response.data) {
        console.log(response.data);
      searchResults.value = response.data.records || [];
      totalResults.value = response.data.total || 0;
      totalPages.value = response.data.pages || 0;
    } else {
      console.error('搜索返回错误:', response.message || '未知错误');
      searchResults.value = [];
      totalResults.value = 0;
      totalPages.value = 0;
    }
  } catch (error) {
    console.error('搜索失败:', error);
    searchResults.value = [];
    totalResults.value = 0;
    totalPages.value = 0;
  } finally {
    loading.value = false;
  }
};

// 重置筛选条件
const resetFilters = () => {
  filters.types.intellectualProperty = true;
  filters.types.award = true;
  filters.types.researchProject = true;
  filters.teacherId = '';
  filters.startYear = currentYear - 2;
  filters.endYear = currentYear;
  filters.keyword = '';
};

// 应用排序
const applySorting = () => {
  search();
};

// 切换页码
const changePage = (page) => {
  currentPage.value = page;
  search();
};

// 导出结果为Excel
const exportResults = async () => {
  try {
    // 构建导出查询参数
    const typesToSearch = [];
    if (filters.types.intellectualProperty) typesToSearch.push('intellectualProperty');
    if (filters.types.award) typesToSearch.push('award');
    if (filters.types.researchProject) typesToSearch.push('researchProject');
    
    const params = {
      types: typesToSearch.join(','),
      teacherId: filters.teacherId || undefined,
      startYear: filters.startYear,
      endYear: filters.endYear,
      keyword: filters.keyword || undefined,
      sort: sortOption.value
    };
    
    // 调用导出API
    const response = await adminApi.exportSearchResults(params);
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `科研成果查询结果_${new Date().toISOString().split('T')[0]}.xlsx`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    console.error('导出失败:', error);
  }
};

// 查看详情
const viewDetail = (item) => {
  // 在弹窗中显示详情，而不是跳转
  currentDetail.value = item;
  showDetailDialog.value = true;
};

// 关闭详情弹窗
const closeDetailDialog = () => {
  showDetailDialog.value = false;
  currentDetail.value = null;
};

// 辅助函数
const getResultTitle = (item) => {
  if (item.entityType === 'intellectualProperty') return item.title;
  if (item.entityType === 'award') return item.title;
  if (item.entityType === 'researchProject') return item.title;
  return '';
};

const getResultDate = (item) => {
  if (item.entityType === 'intellectualProperty') {
    if (item.date2) return formatDate(item.date2);  // 授权日期
    if (item.date1) return formatDate(item.date1);  // 申请日期
  }
  if (item.entityType === 'award') {
    if (item.mainDate) return formatDate(item.mainDate);
  }
  if (item.entityType === 'researchProject') {
    if (item.date1 && item.date2) {
      return `${formatDate(item.date1)} ~ ${formatDate(item.date2)}`;
    }
  }
  return '';
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN');
};

const getResultItemClass = (type) => {
  return `result-${type}`;
};

const getResultTypeBadgeClass = (type) => {
  return `badge-${type}`;
};

const getResultTypeLabel = (type) => {
  if (type === 'intellectualProperty') return '知识产权';
  if (type === 'award') return '奖项';
  if (type === 'researchProject') return '科研项目';
  return '';
};
</script>

<style scoped>
.multi-search-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 搜索筛选器样式 */
.search-filters {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.filter-group {
  margin-bottom: 15px;
}

.filter-group h3 {
  font-size: 1rem;
  margin-bottom: 8px;
  color: #333;
}

.filter-options {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-option {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.filter-select, .filter-input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.year-filter {
  display: flex;
  align-items: center;
  gap: 10px;
}

.year-filter .filter-select {
  width: auto;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 15px;
}

.search-btn, .reset-btn, .export-btn, .action-btn {
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn {
  background-color: #1890ff;
  color: white;
  border: none;
}

.search-btn:hover {
  background-color: #40a9ff;
}

.reset-btn {
  background-color: white;
  border: 1px solid #ddd;
}

.reset-btn:hover {
  background-color: #f5f5f5;
}

/* 查询结果样式 */
.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.results-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-select {
  padding: 6px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.export-btn {
  background-color: #52c41a;
  color: white;
  border: none;
}

.export-btn:hover {
  background-color: #73d13d;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.result-item {
  display: flex;
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 15px;
  position: relative;
  transition: all 0.3s;
}

.result-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.result-type-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  color: white;
}

.badge-intellectualProperty {
  background-color: #1890ff;
}

.badge-award {
  background-color: #ff7a45;
}

.badge-researchProject {
  background-color: #722ed1;
}

.result-content {
  flex: 1;
}

.result-title {
  font-size: 1.1rem;
  margin-bottom: 10px;
  color: #333;
}

.result-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.9rem;
}

.meta-label {
  color: #666;
}

.meta-value {
  color: #333;
}

.result-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.action-btn {
  background-color: #1890ff;
  color: white;
  border: none;
}

.action-btn:hover {
  background-color: #40a9ff;
}

/* 加载状态和空结果 */
.loading-spinner, .no-results {
  padding: 30px;
  text-align: center;
  color: #666;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  gap: 10px;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:disabled {
  color: #d9d9d9;
  cursor: not-allowed;
}

.page-btn:not(:disabled):hover {
  color: #1890ff;
  border-color: #1890ff;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-number {
  width: 32px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.page-number.active {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.page-number:not(.active):hover {
  color: #1890ff;
  border-color: #1890ff;
}

/* 详情弹窗样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 100;
}

.detail-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #fff;
  border-radius: 8px;
  width: 600px;
  max-width: 90%;
  max-height: 80vh;
  z-index: 101;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.dialog-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #333;
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: #999;
}

.dialog-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.detail-title {
  font-size: 1.4rem;
  margin: 10px 0 20px;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-type-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 0.9rem;
  color: white;
  margin-bottom: 10px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h5 {
  font-size: 1.1rem;
  margin-bottom: 10px;
  color: #333;
  border-left: 4px solid #1890ff;
  padding-left: 10px;
}

.detail-item {
  display: flex;
  margin-bottom: 8px;
}

.detail-label {
  width: 100px;
  font-weight: 500;
  color: #666;
}

.detail-value {
  flex: 1;
  color: #333;
}

.detail-description {
  background-color: #f9f9f9;
  padding: 12px;
  border-radius: 4px;
  white-space: pre-wrap;
  color: #333;
}
</style> 