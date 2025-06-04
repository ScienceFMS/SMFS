<template>
  <div class="research-projects">
    <div class="page-header">
      <h2 class="page-title">科研项目</h2>
      <el-button type="primary" @click="handleAddProject">
        <el-icon><Plus /></el-icon>新增项目
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="项目状态">
          <el-select v-model="filterForm.status" placeholder="所有状态" style="width: 100%" clearable>
            <el-option label="所有状态" value=""></el-option>
            <el-option label="进行中" value="进行中"></el-option>
            <el-option label="已结题" value="已结题"></el-option>
            <el-option label="未开始" value="未开始"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="项目类型">
          <el-select v-model="filterForm.type" placeholder="所有类型" style="width: 100%" clearable>
            <el-option label="所有类型" value=""></el-option>
            <el-option label="国家级" value="国家级"></el-option>
            <el-option label="省部级" value="省部级"></el-option>
            <el-option label="省自然科学基金" value="省自然科学基金"></el-option>
            <el-option label="横向" value="横向"></el-option>
            <el-option label="校级" value="校级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input v-model="filterForm.keyword" placeholder="请输入关键词"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="project-list-card">
      <el-table
        :data="projectList"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="projectName" label="项目名称" min-width="200"></el-table-column>
        <el-table-column prop="projectCode" label="项目编号" width="180"></el-table-column>
        <el-table-column prop="projectType" label="项目类型" width="120"></el-table-column>
        <el-table-column prop="funding" label="经费(万元)" width="100">
          <template #default="{ row }">
            {{ row.funding ? (row.funding / 10000).toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120"></el-table-column>
        <el-table-column prop="endDate" label="结束日期" width="120"></el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(calculateProjectStatus(row))">
              {{ calculateProjectStatus(row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="附件" width="100">
          <template #default="{ row }">
            <el-button 
              v-if="row.attachments" 
              type="primary" 
              link 
              @click="downloadFile(row.attachments)"
            >
              <el-icon><Download /></el-icon> 下载
            </el-button>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm
              title="确定要删除该项目吗？"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      
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
    
    <!-- 项目表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增项目' : (dialogType === 'edit' ? '编辑项目' : '查看项目')"
      width="680px"
    >
      <el-form 
        ref="projectFormRef" 
        :model="projectForm" 
        :rules="rules" 
        label-width="100px"
        label-position="right"
        :disabled="dialogType === 'view'"
      >
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="projectForm.projectName" placeholder="请输入项目名称"></el-input>
        </el-form-item>
        <el-form-item label="项目编号" prop="projectCode">
          <el-input v-model="projectForm.projectCode" placeholder="请输入项目编号"></el-input>
        </el-form-item>
        <el-form-item label="项目类型" prop="projectType">
          <el-select v-model="projectForm.projectType" placeholder="请选择项目类型" style="width: 100%">
            <el-option label="国家级" value="国家级"></el-option>
            <el-option label="省部级" value="省部级"></el-option>
            <el-option label="省自然科学基金" value="省自然科学基金"></el-option>
            <el-option label="横向" value="横向"></el-option>
            <el-option label="校级" value="校级"></el-option>
          </el-select>
        </el-form-item>
        <!-- 项目状态不需要手动选择，将根据日期自动计算 -->
        <el-form-item label="项目经费" prop="funding">
          <el-input-number 
            v-model="projectForm.funding" 
            :min="0" 
            :precision="2" 
            :step="1000" 
            style="width: 100%"
            placeholder="请输入项目经费(元)"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker 
            v-model="projectForm.startDate" 
            type="date" 
            placeholder="选择开始日期"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker 
            v-model="projectForm.endDate" 
            type="date" 
            placeholder="选择结束日期"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="个人角色" prop="role">
          <el-select v-model="projectForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="负责人" value="负责人"></el-option>
            <el-option label="主要成员" value="主要成员"></el-option>
            <el-option label="参与者" value="参与者"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="附件">
          <div class="file-upload-container">
            <div v-if="projectForm.attachments" class="file-preview-box">
              <div class="file-info">
                <el-icon><Document /></el-icon>
                <span class="file-name">{{ getFileName(projectForm.attachments) }}</span>
              </div>
              <div class="file-actions" v-if="dialogType !== 'view'">
                <el-button type="danger" icon="Delete" circle @click="removeAttachment"></el-button>
              </div>
            </div>
            <el-upload
              v-else
              class="file-uploader"
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleAttachmentChange"
              :disabled="dialogType === 'view'"
              accept=".pdf,.doc,.docx,.xls,.xlsx,.zip,.rar"
            >
              <el-button type="primary">上传附件</el-button>
              <template #tip>
                <div class="el-upload__tip">支持PDF、Word、Excel、压缩包等格式，不超过10MB</div>
              </template>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">{{ dialogType === 'view' ? '关闭' : '取消' }}</el-button>
          <el-button v-if="dialogType !== 'view'" type="primary" @click="submitForm" :loading="submitting">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 文件预览对话框 -->
    <el-dialog v-model="previewVisible" title="文件预览" width="500px">
      <div class="file-preview">
        <div class="preview-info">
          <h3>{{ previewFileName }}</h3>
          <p>点击下载查看文件</p>
          <el-button type="primary" @click="downloadFile(previewUrl)">
            <el-icon><Download /></el-icon> 下载文件
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { Plus, Download, Document } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getResearchProjects, getResearchProjectById, addResearchProject, updateResearchProject, deleteResearchProject } from '../../utils/api';
import { uploadFile, deleteFile } from '../../utils/fileService';
import * as auth from '../../utils/auth';

// 分页数据
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);

// 筛选条件
const filterForm = reactive({
  status: '',
  type: '',
  keyword: ''
});

// 项目表单
const projectForm = reactive({
  id: null,
  projectName: '',
  projectCode: '',
  projectType: '',  // 项目类型：国家级、省部级等
  funding: 0,
  startDate: '',
  endDate: '',
  role: '',    // 个人角色：负责人、参与者等
  attachments: '',
  teacherId: null
});

// 表单规则
const rules = {
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { min: 3, max: 100, message: '长度在 3 到 100 个字符', trigger: 'blur' }
  ],
  projectCode: [
    { required: true, message: '请输入项目编号', trigger: 'blur' }
  ],
  projectType: [
    { required: true, message: '请选择项目类型', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ],
  role: [
    { required: true, message: '请选择个人角色', trigger: 'change' }
  ]
};

// 项目列表数据
const projectList = ref([]);
const dialogVisible = ref(false);
const dialogType = ref('add');
const projectFormRef = ref(null);

// 文件相关
const attachmentFile = ref(null);
const previewVisible = ref(false);
const previewUrl = ref('');
const previewFileName = ref('');

// 计算项目状态
const calculateProjectStatus = (project) => {
  if (!project.startDate || !project.endDate) {
    return '未知';
  }

  const now = new Date();
  const startDate = new Date(project.startDate);
  const endDate = new Date(project.endDate);

  if (now < startDate) {
    return '未开始';
  } else if (now > endDate) {
    return '已结题';
  } else {
    return '进行中';
  }
};

// 根据项目状态获取标签类型
const getStatusType = (status) => {
  const typeMap = {
    '进行中': 'success',
    '已结题': 'info',
    '未开始': 'warning',
    '未知': 'danger'
  };
  return typeMap[status] || 'info';
};

// 获取文件名
const getFileName = (fileUrl) => {
  if (!fileUrl) return '';
  const parts = fileUrl.split('/');
  return parts[parts.length - 1];
};

// 处理新增项目
const handleAddProject = () => {
  dialogType.value = 'add';
  resetForm();
  dialogVisible.value = true;
  
  // 获取当前登录的教师ID
  const user = auth.getCurrentUser();
  if (user) {
    projectForm.teacherId = user.teacherId;
  }
};

// 查询
const handleSearch = () => {
  currentPage.value = 1;
  loadProjectData();
};

// 重置筛选条件
const resetFilter = () => {
  filterForm.status = '';
  filterForm.type = '';
  filterForm.keyword = '';
  // 重置后重新加载数据
  loadProjectData();
};

// 查看项目详情
const handleView = (row) => {
  dialogType.value = 'view';
  resetForm();
  
  // 获取完整的项目数据
  getResearchProjectById(row.id).then(response => {
    if (response.code === 200) {
      const project = response.data;
      Object.keys(projectForm).forEach(key => {
        projectForm[key] = project[key];
      });
      
      dialogVisible.value = true;
    } else {
      ElMessage.error(response.message || '获取项目详情失败');
    }
  }).catch(error => {
    console.error('获取项目详情失败', error);
    ElMessage.error('获取项目详情失败，请稍后重试');
  });
};

// 编辑项目
const handleEdit = (row) => {
  dialogType.value = 'edit';
  resetForm();
  
  // 这里需要请求完整的项目数据
  getResearchProjectById(row.id).then(response => {
    if (response.code === 200) {
      const project = response.data;
      Object.keys(projectForm).forEach(key => {
        projectForm[key] = project[key];
      });
      
      dialogVisible.value = true;
    } else {
      ElMessage.error(response.message || '获取项目详情失败');
    }
  }).catch(error => {
    console.error('获取项目详情失败', error);
    ElMessage.error('获取项目详情失败，请稍后重试');
  });
};

// 删除项目
const handleDelete = async (row) => {
  try {
    // 如果有附件，先删除附件
    if (row.attachments) {
      try {
        await deleteFile(row.attachments);
      } catch (error) {
        console.error('删除附件失败:', error);
        ElMessage.warning('附件删除失败，但将继续删除项目');
      }
    }
    
    loading.value = true;
    const response = await deleteResearchProject(row.id);
    if (response.code === 200) {
      ElMessage.success('删除成功');
      loadProjectData(); // 重新加载数据
    } else {
      ElMessage.error(response.message || '删除失败');
    }
  } catch (error) {
    console.error('删除项目失败', error);
    ElMessage.error('删除失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 处理附件选择
const handleAttachmentChange = (file) => {
  // 检查文件类型
  const validTypes = ['.pdf', '.doc', '.docx', '.xls', '.xlsx', '.zip', '.rar'];
  const isValidType = validTypes.some(type => file.name.toLowerCase().endsWith(type));
  
  if (!isValidType) {
    ElMessage.error('请上传PDF、Word、Excel或压缩包格式的文件');
    return;
  }
  
  // 检查文件大小（限制为10MB）
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB');
    return;
  }
  
  // 保存文件对象，用于后续上传
  attachmentFile.value = file.raw;
  
  // 创建本地预览
  projectForm.attachments = URL.createObjectURL(file.raw);
};

// 移除附件
const removeAttachment = async () => {
  if (dialogType.value === 'edit' && projectForm.attachments && !projectForm.attachments.startsWith('blob:')) {
    try {
      // 如果是编辑模式且文件已经上传到服务器，则删除服务器上的文件
      const deleteResult = await deleteFile(projectForm.attachments);
      if (!deleteResult.success) {
        ElMessage.warning('附件从服务器删除失败，但已从表单中移除');
      }
    } catch (error) {
      console.error('删除附件失败:', error);
      ElMessage.warning('附件从服务器删除失败，但已从表单中移除');
    }
  }
  
  // 清除本地预览和文件对象
  projectForm.attachments = '';
  attachmentFile.value = null;
};

// 上传附件
const uploadAttachment = async () => {
  if (!attachmentFile.value) {
    return true; // 没有新的附件，直接返回成功
  }
  
  try {
    // 上传附件到静态资源服务器
    const uploadResult = await uploadFile(attachmentFile.value, 'project-attachment');
    
    if (!uploadResult.success) {
      ElMessage.error(uploadResult.message || '附件上传失败');
      return false;
    }
    
    // 更新表单中的附件URL
    projectForm.attachments = uploadResult.url;
    return true;
  } catch (error) {
    console.error('附件上传失败:', error);
    ElMessage.error('附件上传失败');
    return false;
  }
};

// 预览附件
const previewAttachment = (fileUrl) => {
  if (fileUrl) {
    previewUrl.value = fileUrl;
    previewFileName.value = getFileName(fileUrl);
    previewVisible.value = true;
  } else {
    ElMessage.warning('文件不存在');
  }
};

// 下载文件
const downloadFile = (url) => {
  if (!url) {
    ElMessage.warning('文件链接不存在');
    return;
  }
  
  try {
    console.log(`[ResearchProjects] 开始下载文件: ${url}`);
    ElMessage.success('开始下载文件');
    
    // 创建一个临时链接并点击它来下载文件
    const link = document.createElement('a');
    link.href = url;
    link.target = '_blank';
    link.download = getFileName(url);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    console.error('[ResearchProjects] 下载文件失败:', error);
    ElMessage.error('下载文件失败，请稍后重试');
  }
};

// 提交表单
const submitForm = async () => {
  if (!projectFormRef.value) return;
  
  await projectFormRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    
    submitting.value = true;
    
    try {
      // 先上传附件（如果有）
      const uploadSuccess = await uploadAttachment();
      if (!uploadSuccess) {
        submitting.value = false;
        return;
      }
      
      // 调用API保存数据
      const apiCall = dialogType.value === 'add' 
        ? addResearchProject(projectForm) 
        : updateResearchProject(projectForm);
      
      const response = await apiCall;
      if (response.code === 200) {
        ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功');
        dialogVisible.value = false;
        loadProjectData(); // 重新加载数据
      } else {
        ElMessage.error(response.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'));
      }
    } catch (error) {
      console.error(dialogType.value === 'add' ? '添加项目失败' : '更新项目失败', error);
      ElMessage.error(dialogType.value === 'add' ? '添加失败，请稍后重试' : '更新失败，请稍后重试');
    } finally {
      submitting.value = false;
      // 清除文件对象
      attachmentFile.value = null;
    }
  });
};

// 处理每页显示数量变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  loadProjectData();
};

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
  loadProjectData();
};

// 重置表单
const resetForm = () => {
  if (projectFormRef.value) {
    nextTick(() => {
      projectFormRef.value.resetFields();
    });
  }
  Object.keys(projectForm).forEach(key => {
    if (key !== 'teacherId') {
      projectForm[key] = key === 'funding' ? 0 : '';
    }
  });
  projectForm.id = null;
  attachmentFile.value = null;
};

// 加载项目数据
const loadProjectData = () => {
  console.log('[ResearchProjects] 开始加载项目数据');
  const user = auth.getCurrentUser();
  if (!user || !user.teacherId) {
    console.log('[ResearchProjects] 未获取到teacherId:', user);
    ElMessage.error('未获取到教师信息');
    return;
  }
  
  console.log(`[ResearchProjects] 使用teacherId=${user.teacherId}加载数据`);
  loading.value = true;
  
  // 准备请求参数
  const params = {
    teacherId: user.teacherId,
    page: currentPage.value,
    pageSize: pageSize.value,
    status: filterForm.status,
    type: filterForm.type,
    keyword: filterForm.keyword
  };
  
  console.log('[ResearchProjects] 发送获取项目列表请求:', params);
  getResearchProjects(params).then(response => {
    console.log('[ResearchProjects] 获取项目列表响应:', response);
    if (response.code === 200) {
      const data = response.data;
      projectList.value = data.records;
      total.value = data.total;
      console.log(`[ResearchProjects] 加载了${data.records.length}条记录，共${data.total}条`);
      
      // 检查附件信息是否存在
      projectList.value.forEach(project => {
        if (project.attachments) {
          console.log(`[ResearchProjects] 项目 ${project.projectName} 有附件: ${project.attachments}`);
        }
      });
    } else {
      ElMessage.error(response.message || '获取项目列表失败');
    }
  }).catch(error => {
    console.error('获取项目列表失败', error);
    ElMessage.error('获取项目列表失败，请稍后重试');
  }).finally(() => {
    loading.value = false;
  });
};

onMounted(() => {
  console.log('[ResearchProjects] 组件已挂载');
  loadProjectData();
});
</script>

<style scoped>
.research-projects {
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
  font-size: 22px;
  font-weight: 500;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
}

.filter-form .el-form-item {
  margin-right: 20px;
}

.filter-form .el-select {
  min-width: 150px;
}

.project-list-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  padding-top: 20px;
  text-align: right;
  display: block;
}

.file-upload-container {
  width: 100%;
}

.file-preview-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background-color: #f8f8f8;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-name {
  word-break: break-all;
  color: #606266;
}

.file-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 150px;
}

.preview-info {
  text-align: center;
  padding: 20px;
}

.preview-info h3 {
  margin-bottom: 15px;
  color: #303133;
}

.preview-info p {
  margin-bottom: 20px;
  color: #606266;
}

/* 响应式处理 */
@media (max-width: 768px) {
  .filter-form {
    flex-direction: column;
  }
  
  .el-form-item {
    margin-right: 0;
  }
}
</style> 