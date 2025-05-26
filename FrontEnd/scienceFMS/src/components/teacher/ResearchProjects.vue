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
      :title="dialogType === 'add' ? '新增项目' : '编辑项目'"
      width="680px"
    >
      <el-form 
        ref="projectFormRef" 
        :model="projectForm" 
        :rules="rules" 
        label-width="100px"
        label-position="right"
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
          <el-upload
            action="/upload"
            :on-success="handleUploadSuccess"
            :on-remove="handleFileRemove"
            :file-list="fileList"
            multiple
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">支持PDF、Word、Excel等格式，不超过10MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { Plus } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getResearchProjects, getResearchProjectById, addResearchProject, updateResearchProject, deleteResearchProject } from '../../utils/api';
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
const fileList = ref([]);

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
  // 实现查看项目详情的逻辑
  const status = calculateProjectStatus(row);
  ElMessageBox.alert(`
    <div style="text-align:left">
      <p><strong>项目名称：</strong>${row.projectName}</p>
      <p><strong>项目编号：</strong>${row.projectCode}</p>
      <p><strong>项目类型：</strong>${row.projectType}</p>
      <p><strong>项目状态：</strong>${status}</p>
      <p><strong>项目经费：</strong>${(row.funding / 10000).toFixed(2)}万元</p>
      <p><strong>开始日期：</strong>${row.startDate}</p>
      <p><strong>结束日期：</strong>${row.endDate}</p>
      <p><strong>个人角色：</strong>${row.role || '未设置'}</p>
    </div>
  `, '项目详情', {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '关闭'
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
      
      // 设置附件列表
      if (project.attachments) {
        const attachmentUrls = project.attachments.split(',');
        fileList.value = attachmentUrls.map((url, index) => {
          const fileName = url.substring(url.lastIndexOf('/') + 1);
          return { name: fileName, url: url, uid: index };
        });
      }
      
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
const handleDelete = (row) => {
  loading.value = true;
  deleteResearchProject(row.id).then(response => {
    if (response.code === 200) {
      ElMessage.success('删除成功');
      loadProjectData(); // 重新加载数据
    } else {
      ElMessage.error(response.message || '删除失败');
    }
  }).catch(error => {
    console.error('删除项目失败', error);
    ElMessage.error('删除失败，请稍后重试');
  }).finally(() => {
    loading.value = false;
  });
};

// 提交表单
const submitForm = () => {
  if (!projectFormRef.value) return;
  
  projectFormRef.value.validate(valid => {
    if (valid) {
      submitting.value = true;
      
      // 处理附件
      if (fileList.value.length > 0) {
        const attachments = fileList.value.map(file => file.url || file.response.url).join(',');
        projectForm.attachments = attachments;
      }
      
      // 直接提交表单数据，无需转换
      const apiCall = dialogType.value === 'add' 
        ? addResearchProject(projectForm) 
        : updateResearchProject(projectForm);
      
      apiCall.then(response => {
        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功');
          dialogVisible.value = false;
          loadProjectData(); // 重新加载数据
        } else {
          ElMessage.error(response.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'));
        }
      }).catch(error => {
        console.error(dialogType.value === 'add' ? '添加项目失败' : '更新项目失败', error);
        ElMessage.error(dialogType.value === 'add' ? '添加失败，请稍后重试' : '更新失败，请稍后重试');
      }).finally(() => {
        submitting.value = false;
      });
    }
  });
};

// 处理文件上传成功
const handleUploadSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    ElMessage.success('文件上传成功');
    file.url = response.data; // 假设后端返回的URL在data字段中
  } else {
    ElMessage.error(response.message || '文件上传失败');
  }
};

// 处理文件移除
const handleFileRemove = (file, fileList) => {
  // 如果需要实现移除服务器文件功能，在这里调用删除文件的API
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
  fileList.value = [];
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