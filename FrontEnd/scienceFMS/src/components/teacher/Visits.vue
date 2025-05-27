<template>
  <div class="visits">
    <div class="page-header">
      <h2 class="page-title">出访记录管理</h2>
      <el-button type="primary" @click="handleAddVisit">
        <el-icon><Plus /></el-icon>新增出访记录
      </el-button>
    </div>
    
    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="年度" style="min-width: 200px;">
          <el-select v-model="filterForm.year" placeholder="所有年份" style="width: 120px;" clearable>
            <el-option label="所有年份" value=""></el-option>
            <el-option 
              v-for="year in availableYears" 
              :key="year" 
              :label="year + '年'" 
              :value="year">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filterForm.keyword" placeholder="出访目的/地点/成果"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 出访记录列表 -->
    <el-card class="visit-list-card">
      <div class="statistics-bar">
        <el-statistic title="本年度出访次数" :value="yearlyVisitCount">
          <template #suffix>次</template>
        </el-statistic>
      </div>
      
      <el-table
        :data="visitList"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="purpose" label="出访目的" min-width="180"></el-table-column>
        <el-table-column prop="location" label="出访地点" min-width="150"></el-table-column>
        <el-table-column label="出访时间" width="220">
          <template #default="{ row }">
            {{ formatDateRange(row.startDate, row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="reimbursementStatus" label="报销状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getTagType(row.reimbursementStatus)">
              {{ getReimbursementStatusText(row.reimbursementStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="文件" width="200">
          <template #default="{ row }">
            <div class="file-actions">
              <el-button type="primary" link @click="previewFile(row.itineraryFile)" v-if="row.itineraryFile">
                行程单
              </el-button>
              <el-button type="success" link @click="previewFile(row.reportFile)" v-if="row.reportFile">
                成果报告
              </el-button>
              <span v-if="!row.itineraryFile && !row.reportFile">无</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="generateTemplate(row)">报销模板</el-button>
            <el-popconfirm
              title="确定要删除该出访记录吗？"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 出访记录表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
    >
      <el-form 
        ref="visitFormRef" 
        :model="visitForm" 
        :rules="rules" 
        label-width="100px"
        label-position="right"
        :disabled="dialogType === 'view'"
      >
        <el-form-item label="出访目的" prop="purpose">
          <el-input v-model="visitForm.purpose" placeholder="请输入出访目的"></el-input>
        </el-form-item>
        <el-form-item label="出访地点" prop="location">
          <el-input v-model="visitForm.location" placeholder="请输入出访地点"></el-input>
        </el-form-item>
        <el-form-item label="出访时间" required>
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item prop="startDate">
                <el-date-picker 
                  v-model="visitForm.startDate" 
                  type="date" 
                  placeholder="开始日期"
                  style="width: 100%"
                ></el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="endDate">
                <el-date-picker 
                  v-model="visitForm.endDate" 
                  type="date" 
                  placeholder="结束日期"
                  style="width: 100%"
                ></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="出访成果" prop="result">
          <el-input 
            v-model="visitForm.result" 
            type="textarea" 
            :rows="3"
            placeholder="请描述出访成果"
          ></el-input>
        </el-form-item>
        <el-form-item label="报销状态" prop="reimbursementStatus">
          <el-select v-model="visitForm.reimbursementStatus" placeholder="请选择报销状态" style="width: 100%">
            <el-option label="未报销" value="UNREIMBURSED"></el-option>
            <el-option label="报销中" value="IN_PROGRESS"></el-option>
            <el-option label="已报销" value="REIMBURSED"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="报销金额" prop="reimbursementAmount" v-if="visitForm.reimbursementStatus === 'REIMBURSED'">
          <el-input-number 
            v-model="visitForm.reimbursementAmount" 
            :min="0"
            :precision="2"
            :step="100"
            style="width: 100%"
            placeholder="请输入报销金额"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="行程单">
          <el-upload
            :action="uploadItineraryUrl"
            :on-success="handleItineraryUploadSuccess"
            :on-remove="handleItineraryFileRemove"
            :file-list="itineraryFileList"
            :limit="1"
            accept=".pdf,.doc,.docx,.xls,.xlsx"
          >
            <el-button type="primary">上传行程单</el-button>
            <template #tip>
              <div class="el-upload__tip">支持PDF、Word、Excel格式文件</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="成果报告">
          <el-upload
            :action="uploadReportUrl"
            :on-success="handleReportUploadSuccess"
            :on-remove="handleReportFileRemove"
            :file-list="reportFileList"
            :limit="1"
            accept=".pdf,.doc,.docx"
          >
            <el-button type="primary">上传成果报告</el-button>
            <template #tip>
              <div class="el-upload__tip">支持PDF、Word格式文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">{{ dialogType === 'view' ? '关闭' : '取消' }}</el-button>
          <el-button v-if="dialogType !== 'view'" type="primary" @click="submitForm" :loading="submitting">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 文件预览对话框 -->
    <el-dialog v-model="previewVisible" title="文件预览" width="800px">
      <div class="file-preview">
        <iframe v-if="previewUrl" :src="previewUrl" style="width: 100%; height: 500px;"></iframe>
        <div v-else class="preview-error">
          <p>无法直接预览该文件，请<a :href="previewUrl" target="_blank">点击此处</a>下载查看。</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Download } from '@element-plus/icons-vue';
import * as auth from '../../utils/auth';
import axios from 'axios';
import {
  getVisitRecords,
  getVisitRecordById,
  addVisitRecord,
  updateVisitRecord,
  deleteVisitRecord,
  uploadItineraryFile,
  uploadReportFile,
  getVisitYearlyStats,
  getReimbursementTemplate
} from '../../utils/api';

// 分页数据
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);

// 年度统计
const yearlyVisitCount = ref(0);

// 筛选条件
const filterForm = reactive({
  year: '',
  keyword: ''
});

// 出访记录列表
const visitList = ref([]);

// 对话框控制
const dialogVisible = ref(false);
const dialogType = ref('add'); // add, edit, view
const visitFormRef = ref(null);

// 预览控制
const previewVisible = ref(false);
const previewUrl = ref('');

// 计算对话框标题
const dialogTitle = computed(() => {
  if (dialogType.value === 'add') return '新增出访记录';
  if (dialogType.value === 'edit') return '编辑出访记录';
  return '查看出访记录';
});

// 上传URL
const uploadItineraryUrl = 'http://localhost:8082/visits/upload/itinerary';
const uploadReportUrl = 'http://localhost:8082/visits/upload/report';

// 文件列表
const itineraryFileList = ref([]);
const reportFileList = ref([]);

// 表单数据
const visitForm = reactive({
  id: null,
  purpose: '',
  location: '',
  startDate: '',
  endDate: '',
  result: '',
  itineraryFile: '',
  reportFile: '',
  reimbursementStatus: 'UNREIMBURSED',
  reimbursementAmount: null,
  teacherId: null
});

// 表单验证规则
const rules = {
  purpose: [{ required: true, message: '请输入出访目的', trigger: 'blur' }],
  location: [{ required: true, message: '请输入出访地点', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  result: [{ required: false, message: '请描述出访成果', trigger: 'blur' }],
  reimbursementStatus: [{ required: true, message: '请选择报销状态', trigger: 'change' }]
};

// 计算可选年份（当前年份往前推10年）
const availableYears = computed(() => {
  const currentYear = new Date().getFullYear();
  const years = [];
  for (let i = 0; i < 10; i++) {
    years.push(currentYear - i);
  }
  return years;
});

// 格式化日期区间
const formatDateRange = (startDate, endDate) => {
  if (!startDate || !endDate) return '';
  return `${startDate} 至 ${endDate}`;
};

// 获取报销状态文本
const getReimbursementStatusText = (status) => {
  switch (status) {
    case 'UNREIMBURSED': return '未报销';
    case 'IN_PROGRESS': return '报销中';
    case 'REIMBURSED': return '已报销';
    default: return '未知';
  }
};

// 获取标签类型
const getTagType = (status) => {
  switch (status) {
    case 'UNREIMBURSED': return 'info';
    case 'IN_PROGRESS': return 'warning';
    case 'REIMBURSED': return 'success';
    default: return '';
  }
};

// 初始化
onMounted(() => {
  fetchUserInfo();
  fetchVisits();
  fetchYearlyStats();
});

// 获取用户信息
const fetchUserInfo = () => {
  const userInfo = auth.getCurrentUser();
  if (userInfo) {
    visitForm.teacherId = userInfo.teacherId || userInfo.userId;
  }
};

// 获取出访记录列表
const fetchVisits = async () => {
  loading.value = true;
  try {
    // 构建查询参数
    const params = {
      teacherId: visitForm.teacherId,
      page: currentPage.value,
      pageSize: pageSize.value,
      year: filterForm.year || undefined,
      keyword: filterForm.keyword || undefined
    };

    // 调用API获取数据
    const res = await getVisitRecords(params);
    
    if (res.code === 200) {
      visitList.value = res.data.records;
      total.value = res.data.total;
    } else {
      ElMessage.error(res.message || '获取出访记录列表失败');
    }
  } catch (error) {
    console.error('获取出访记录列表失败:', error);
    ElMessage.error('获取出访记录列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取年度统计
const fetchYearlyStats = async () => {
  try {
    const currentYear = new Date().getFullYear();
    const res = await getVisitYearlyStats(visitForm.teacherId, currentYear);
    
    if (res.code === 200) {
      yearlyVisitCount.value = res.data.visitCount;
    } else {
      console.error('获取年度出访统计失败:', res.message);
      yearlyVisitCount.value = 0;
    }
  } catch (error) {
    console.error('获取年度出访统计失败:', error);
    yearlyVisitCount.value = 0;
  }
};

// 查询按钮点击
const handleSearch = () => {
  currentPage.value = 1;
  fetchVisits();
};

// 重置筛选
const resetFilter = () => {
  filterForm.year = '';
  filterForm.keyword = '';
  currentPage.value = 1;
  fetchVisits();
};

// 改变每页大小
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchVisits();
};

// 改变当前页码
const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchVisits();
};

// 预览文件
const previewFile = (fileUrl) => {
  if (fileUrl) {
    previewUrl.value = fileUrl;
    previewVisible.value = true;
  } else {
    ElMessage.warning('文件不存在');
  }
};

// 新增出访记录
const handleAddVisit = () => {
  dialogType.value = 'add';
  resetVisitForm();
  dialogVisible.value = true;
};

// 编辑出访记录
const handleEdit = (row) => {
  dialogType.value = 'edit';
  resetVisitForm();
  
  Object.assign(visitForm, {
    id: row.id,
    purpose: row.purpose,
    location: row.location,
    startDate: row.startDate,
    endDate: row.endDate,
    result: row.result,
    itineraryFile: row.itineraryFile,
    reportFile: row.reportFile,
    reimbursementStatus: row.reimbursementStatus || 'UNREIMBURSED',
    reimbursementAmount: row.reimbursementAmount,
    teacherId: row.teacherId
  });
  
  // 设置文件列表
  if (row.itineraryFile) {
    itineraryFileList.value = [
      {
        name: '行程单',
        url: row.itineraryFile
      }
    ];
  }
  
  if (row.reportFile) {
    reportFileList.value = [
      {
        name: '成果报告',
        url: row.reportFile
      }
    ];
  }
  
  dialogVisible.value = true;
};

// 查看出访记录
const handleView = (row) => {
  dialogType.value = 'view';
  resetVisitForm();
  
  Object.assign(visitForm, row);
  
  // 设置文件列表
  if (row.itineraryFile) {
    itineraryFileList.value = [
      {
        name: '行程单',
        url: row.itineraryFile
      }
    ];
  }
  
  if (row.reportFile) {
    reportFileList.value = [
      {
        name: '成果报告',
        url: row.reportFile
      }
    ];
  }
  
  dialogVisible.value = true;
};

// 删除出访记录
const handleDelete = async (row) => {
  try {
    // 调用删除API
    const res = await deleteVisitRecord(row.id);
    
    // 处理响应
    if (res && res.code === 200) {
      ElMessage.success('删除成功');
      // 刷新数据
      fetchVisits();
      fetchYearlyStats();
    } else {
      ElMessage.error(res?.message || '删除失败');
    }
  } catch (error) {
    console.error('删除出访记录失败:', error);
    ElMessage.error('删除失败');
  }
};

// 生成报销模板
const generateTemplate = (row) => {
  try {
    // 调用API获取报销模板
    getReimbursementTemplate(row.id).then(response => {
      // 处理二进制响应
      const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `出访报销模板_${row.purpose}_${row.id}.xlsx`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      ElMessage.success('报销模板已生成，正在下载...');
    }).catch(error => {
      console.error('生成报销模板失败:', error);
      ElMessage.error('生成报销模板失败');
    });
  } catch (error) {
    console.error('生成报销模板失败:', error);
    ElMessage.error('生成报销模板失败');
  }
};

// 处理行程单上传成功
const handleItineraryUploadSuccess = (response, file) => {
  if (response.code === 200) {
    visitForm.itineraryFile = response.data;
    ElMessage.success('行程单上传成功');
  } else {
    ElMessage.error(response.message || '行程单上传失败');
  }
};

// 处理成果报告上传成功
const handleReportUploadSuccess = (response, file) => {
  if (response.code === 200) {
    visitForm.reportFile = response.data;
    ElMessage.success('成果报告上传成功');
  } else {
    ElMessage.error(response.message || '成果报告上传失败');
  }
};

// 移除行程单文件
const handleItineraryFileRemove = () => {
  visitForm.itineraryFile = '';
  itineraryFileList.value = [];
};

// 移除成果报告文件
const handleReportFileRemove = () => {
  visitForm.reportFile = '';
  reportFileList.value = [];
};

// 重置表单
const resetVisitForm = () => {
  visitForm.id = null;
  visitForm.purpose = '';
  visitForm.location = '';
  visitForm.startDate = '';
  visitForm.endDate = '';
  visitForm.result = '';
  visitForm.itineraryFile = '';
  visitForm.reportFile = '';
  visitForm.reimbursementStatus = 'UNREIMBURSED';
  visitForm.reimbursementAmount = null;
  
  itineraryFileList.value = [];
  reportFileList.value = [];
};

// 提交表单
const submitForm = async () => {
  if (!visitFormRef.value) return;
  
  await visitFormRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    
    submitting.value = true;
    
    try {
      // 确保教师ID存在
      if (!visitForm.teacherId) {
        const userInfo = auth.getCurrentUser();
        visitForm.teacherId = userInfo?.teacherId || userInfo?.userId;
      }
      
      // 验证日期
      if (new Date(visitForm.endDate) < new Date(visitForm.startDate)) {
        ElMessage.error('结束日期不能早于开始日期');
        submitting.value = false;
        return;
      }
      
      // 调用API保存数据
      const apiFunc = dialogType.value === 'add' ? addVisitRecord : updateVisitRecord;
      const res = await apiFunc(visitForm);
      
      if (res && res.code === 200) {
        ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功');
        dialogVisible.value = false;
        fetchVisits();
        fetchYearlyStats();
      } else {
        ElMessage.error(res?.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'));
      }
    } catch (error) {
      console.error('保存出访记录失败:', error);
      ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败');
    } finally {
      submitting.value = false;
    }
  });
};

// 手动上传文件（如果需要）
const uploadFile = async (file, type) => {
  const formData = new FormData();
  formData.append('file', file);
  
  try {
    const uploadFunction = type === 'itinerary' ? uploadItineraryFile : uploadReportFile;
    const res = await uploadFunction(formData);
    
    if (res.code === 200) {
      if (type === 'itinerary') {
        visitForm.itineraryFile = res.data;
        ElMessage.success('行程单上传成功');
      } else {
        visitForm.reportFile = res.data;
        ElMessage.success('成果报告上传成功');
      }
      return true;
    } else {
      ElMessage.error(res.message || '文件上传失败');
      return false;
    }
  } catch (error) {
    console.error('文件上传失败:', error);
    ElMessage.error('文件上传失败');
    return false;
  }
};

// 导入所需函数
import { nextTick } from 'vue';
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

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
}

.visit-list-card {
  margin-bottom: 20px;
}

.statistics-bar {
  margin-bottom: 20px;
  padding: 10px 0;
  border-bottom: 1px dashed #ebeef5;
}

.file-actions {
  display: flex;
  gap: 8px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.file-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.preview-error {
  text-align: center;
  color: #909399;
}

.upload-disabled .el-upload {
  cursor: not-allowed;
}

/* 上传组件在预览模式下的样式 */
.el-upload-list--picture-card {
  margin-bottom: 10px;
}
</style> 