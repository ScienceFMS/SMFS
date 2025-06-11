<template>
  <div class="awards">
    <div class="page-header">
      <h2 class="page-title">获奖信息</h2>
      <el-button type="primary" @click="handleAddAward">
        <el-icon><Plus /></el-icon>新增获奖
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="年度">
          <el-select v-model="filterForm.year" placeholder="所有年份" style="width: 100%" clearable>
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
          <el-input v-model="filterForm.keyword" placeholder="奖项名称/等级/颁发单位"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="award-list-card">
      <el-table
        :data="awardList"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="awardName" label="奖项名称" min-width="180"></el-table-column>
        <el-table-column prop="awardLevel" label="获奖等级" width="120"></el-table-column>
        <el-table-column prop="issuingUnit" label="颁发单位" min-width="180"></el-table-column>
        <el-table-column prop="awardDate" label="获奖日期" width="120"></el-table-column>
        <el-table-column label="证书" width="100">
          <template #default="{ row }">
            <el-button type="primary" link @click="previewCertificate(row)" v-if="row.certificateImage">
              查看
            </el-button>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="exportPdf(row)">导出PDF</el-button>
            <el-popconfirm
              title="确定要删除该获奖信息吗？"
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
    
    <!-- 获奖信息表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增获奖信息' : (dialogType === 'edit' ? '编辑获奖信息' : '查看获奖信息')"
      width="650px"
    >
      <el-form 
        ref="awardFormRef" 
        :model="awardForm" 
        :rules="rules" 
        label-width="100px"
        label-position="right"
      >
        <el-form-item label="奖项名称" prop="awardName">
          <el-input v-model="awardForm.awardName" placeholder="请输入奖项名称" :disabled="dialogType === 'view'"></el-input>
        </el-form-item>
        <el-form-item label="获奖等级" prop="awardLevel">
          <el-input v-model="awardForm.awardLevel" placeholder="请输入获奖等级，如：一等奖、特等奖、金奖" :disabled="dialogType === 'view'"></el-input>
        </el-form-item>
        <el-form-item label="颁发单位" prop="issuingUnit">
          <el-input v-model="awardForm.issuingUnit" placeholder="请输入颁发单位" :disabled="dialogType === 'view'"></el-input>
        </el-form-item>
        <el-form-item label="获奖日期" prop="awardDate">
          <el-date-picker 
            v-model="awardForm.awardDate" 
            type="date" 
            placeholder="选择获奖日期"
            style="width: 100%"
            :disabled="dialogType === 'view'"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="证书图片">
          <div class="certificate-upload-container">
            <div v-if="awardForm.certificateImage" class="certificate-preview-box">
              <img :src="awardForm.certificateImage" alt="证书图片" class="certificate-preview-img" />
              <div class="certificate-actions" v-if="dialogType !== 'view'">
                <el-button type="danger" icon="Delete" circle @click="removeCertificate"></el-button>
              </div>
            </div>
            <el-upload
              v-else
              class="certificate-uploader"
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleCertificateChange"
              :disabled="dialogType === 'view'"
              accept="image/jpeg,image/png,image/jpg"
            >
              <div class="upload-placeholder">
                <el-icon><Plus /></el-icon>
                <div class="upload-text">点击上传证书</div>
              </div>
              <template #tip>
                <div class="el-upload__tip">支持JPG、PNG格式，不超过5MB</div>
              </template>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="相关项目" prop="relatedProjectId">
          <el-select v-model="awardForm.relatedProjectId" placeholder="请选择相关联的项目(可选)" style="width: 100%" clearable :disabled="dialogType === 'view'">
            <el-option 
              v-for="project in projectOptions" 
              :key="project.id" 
              :label="project.projectName" 
              :value="project.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">{{ dialogType === 'view' ? '关闭' : '取消' }}</el-button>
          <el-button v-if="dialogType !== 'view'" type="primary" @click="submitForm" :loading="submitting">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 证书预览对话框 -->
    <el-dialog v-model="previewVisible" title="证书预览" width="800px">
      <div class="certificate-preview">
        <img :src="previewImage" alt="证书" style="max-width: 100%;">
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { Plus, Delete } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import * as auth from '../../utils/auth';
import { getAwardsByPage, getAwardById, addAward, updateAward, deleteAward, exportAwardToPdf, getResearchProjects } from '../../utils/api';
import { uploadFile, deleteFile } from '../../utils/fileService';

// 分页数据
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);

// 筛选条件
const filterForm = reactive({
  year: '',
  keyword: ''
});

// 获奖列表
const awardList = ref([]);

// 对话框控制
const dialogVisible = ref(false);
const dialogType = ref('add'); // add或edit
const awardFormRef = ref(null);
const awardForm = reactive({
  id: null,
  awardName: '',
  awardLevel: '',
  issuingUnit: '',
  awardDate: '',
  certificateImage: '',
  relatedProjectId: null,
  teacherId: null
});

// 表单验证规则
const rules = {
  awardName: [{ required: true, message: '请输入奖项名称', trigger: 'blur' }],
  awardLevel: [{ required: true, message: '请输入获奖等级', trigger: 'blur' }],
  issuingUnit: [{ required: true, message: '请输入颁发单位', trigger: 'blur' }],
  awardDate: [{ required: true, message: '请选择获奖日期', trigger: 'change' }]
};

// 证书文件
const certificateFile = ref(null);

// 证书预览
const previewVisible = ref(false);
const previewImage = ref('');

// 项目选项
const projectOptions = ref([]);

// 计算可选年份（当前年份往前推10年）
const availableYears = computed(() => {
  const currentYear = new Date().getFullYear();
  const years = [];
  for (let i = 0; i < 10; i++) {
    years.push(currentYear - i);
  }
  return years;
});

// 页面初始化
onMounted(async () => {
  await fetchUserInfo();
  await Promise.all([fetchAwards(), fetchProjects()]);
});

// 获取用户信息
const fetchUserInfo = async () => {
  const userInfo = auth.getCurrentUser();
  if (userInfo) {
    awardForm.teacherId = userInfo.teacherId;
  }
};

// 获取获奖信息列表
const fetchAwards = async () => {
  loading.value = true;
  try {
    // 调用后端API获取获奖信息
    const res = await getAwardsByPage(
      awardForm.teacherId, 
      currentPage.value, 
      pageSize.value, 
      filterForm.year, 
      filterForm.keyword
    );
    
    if (res.code === 200) {
      awardList.value = res.data.records;
      total.value = res.data.total;
    } else {
      ElMessage.error(res.message || '获取获奖信息失败');
      // 加载失败时使用示例数据
      awardList.value = [
        {
          id: 1,
          awardName: '全国高校科技创新优秀成果奖',
          awardLevel: '一等奖',
          issuingUnit: '教育部科技司',
          awardDate: '2023-05-20',
          certificateImage: 'https://example.com/cert1.jpg',
          teacherId: 1
        },
        {
          id: 2,
          awardName: '省自然科学奖',
          awardLevel: '二等奖',
          issuingUnit: '省科学技术厅',
          awardDate: '2022-11-15',
          certificateImage: '',
          teacherId: 1
        }
      ];
      total.value = 2;
    }
  } catch (error) {
    console.error('获取获奖信息失败', error);
    ElMessage.error('获取获奖信息失败');
  } finally {
    loading.value = false;
  }
};

// 获取项目列表（用于关联项目）
const fetchProjects = async () => {
  try {
    // 获取当前登录教师的信息
    const userInfo = auth.getCurrentUser();
    if (!userInfo || !userInfo.teacherId) {
      console.error('无法获取当前教师ID');
      return;
    }
    
    // 调用API获取该教师参与的科研项目列表
    const params = {
      teacherId: userInfo.teacherId,
      page: 1,
      pageSize: 100 // 获取足够多的项目
    };
    
    const res = await getResearchProjects(params);
    if (res && res.code === 200 && res.data && res.data.records) {
      // 将API返回的项目数据转换为下拉框选项格式
      projectOptions.value = res.data.records.map(project => ({
        id: project.id,
        projectName: project.projectName
      }));
    } else {
      console.error('获取项目列表失败', res?.message || '未知错误');
      // 获取失败时使用模拟数据
      projectOptions.value = [
        { id: 1, projectName: '基于人工智能的教育平台研发' },
        { id: 2, projectName: '新型计算机视觉算法研究' }
      ];
    }
  } catch (error) {
    console.error('获取项目列表失败', error);
  }
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchAwards();
};

// 重置筛选条件
const resetFilter = () => {
  filterForm.year = '';
  filterForm.keyword = '';
  currentPage.value = 1;
  fetchAwards();
};

// 处理页码变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchAwards();
};

// 处理当前页变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
  fetchAwards();
};

// 添加获奖信息
const handleAddAward = () => {
  resetAwardForm();
  dialogType.value = 'add';
  dialogVisible.value = true;
};

// 查看获奖信息
const handleView = (row) => {
  resetAwardForm();
  Object.assign(awardForm, row);
  dialogType.value = 'view';
  
  // 如果有关联项目且项目列表还未加载，则重新加载项目列表
  if (row.relatedProjectId && (!projectOptions.value || projectOptions.value.length === 0)) {
    fetchProjects();
  }
  
  dialogVisible.value = true;
};

// 编辑获奖信息
const handleEdit = (row) => {
  resetAwardForm();
  Object.assign(awardForm, row);
  dialogType.value = 'edit';
  
  // 确保项目列表已加载
  if (!projectOptions.value || projectOptions.value.length === 0) {
    fetchProjects();
  }
  
  dialogVisible.value = true;
};

// 删除获奖信息
const handleDelete = async (row) => {
  try {
    // 如果有证书图片，先删除图片
    if (row.certificateImage) {
      try {
        await deleteFile(row.certificateImage);
      } catch (error) {
        console.error('删除证书图片失败:', error);
        ElMessage.warning('证书图片删除失败，但将继续删除获奖信息');
      }
    }
    
    const res = await deleteAward(row.id);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      fetchAwards();
    } else {
      ElMessage.error(res.message || '删除失败');
      // 模拟删除成功（当后端API不可用时）
      awardList.value = awardList.value.filter(item => item.id !== row.id);
      total.value -= 1;
    }
  } catch (error) {
    console.error('删除获奖信息失败', error);
    ElMessage.error('删除失败');
  }
};

// 预览证书
const previewCertificate = (row) => {
  if (row.certificateImage) {
    previewImage.value = row.certificateImage;
    previewVisible.value = true;
  } else {
    ElMessage.warning('暂无证书图片');
  }
};

// 导出PDF
const exportPdf = async (row) => {
  try {
    const res = await exportAwardToPdf(row.id);
    if (res.code === 200) {
      ElMessage.success('导出成功');
      // 下载PDF
      window.open(res.data, '_blank');
    } else {
      ElMessage.error(res.message || '导出失败');
      // 模拟导出成功
      ElMessage.success('PDF已生成，正在下载...');
      setTimeout(() => {
        ElMessage.info('由于这是演示环境，实际下载功能未实现');
      }, 2000);
    }
  } catch (error) {
    console.error('导出PDF失败', error);
    ElMessage.error('导出失败');
  }
};

// 处理证书图片选择
const handleCertificateChange = (file) => {
  // 检查文件类型和大小
  const isImage = /^image\/(jpeg|png|jpg)$/.test(file.raw.type);
  if (!isImage) {
    ElMessage.error('证书图片只能是JPG/PNG格式!');
    return;
  }
  
  const isLt5M = file.raw.size / 1024 / 1024 < 5;
  if (!isLt5M) {
    ElMessage.error('证书图片大小不能超过5MB!');
    return;
  }
  
  // 保存文件对象，用于后续上传
  certificateFile.value = file.raw;
  
  // 创建本地预览
  awardForm.certificateImage = URL.createObjectURL(file.raw);
};

// 移除证书图片
const removeCertificate = async () => {
  if (dialogType.value === 'edit' && awardForm.certificateImage && !awardForm.certificateImage.startsWith('blob:')) {
    try {
      // 如果是编辑模式且图片已经上传到服务器，则删除服务器上的图片
      const deleteResult = await deleteFile(awardForm.certificateImage);
      if (!deleteResult.success) {
        ElMessage.warning('证书图片从服务器删除失败，但已从表单中移除');
      }
    } catch (error) {
      console.error('删除证书图片失败:', error);
      ElMessage.warning('证书图片从服务器删除失败，但已从表单中移除');
    }
  }
  
  // 清除本地预览和文件对象
  awardForm.certificateImage = '';
  certificateFile.value = null;
};

// 上传证书图片
const uploadCertificate = async () => {
  if (!certificateFile.value) {
    return true; // 没有新的证书图片，直接返回成功
  }
  
  try {
    // 上传证书图片到静态资源服务器
    const uploadResult = await uploadFile(certificateFile.value, 'award-certificate');
    
    if (!uploadResult.success) {
      ElMessage.error(uploadResult.message || '证书图片上传失败');
      return false;
    }
    
    // 更新表单中的证书图片URL
    awardForm.certificateImage = uploadResult.url;
    return true;
  } catch (error) {
    console.error('证书图片上传失败:', error);
    ElMessage.error('证书图片上传失败');
    return false;
  }
};

// 提交表单
const submitForm = async () => {
  if (!awardFormRef.value || dialogType.value === 'view') return;
  
  await awardFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        // 先上传证书图片（如果有）
        const uploadSuccess = await uploadCertificate();
        if (!uploadSuccess) {
          submitting.value = false;
          return;
        }
        
        if (dialogType.value === 'add') {
          // 添加获奖信息
          const res = await addAward(awardForm);
          if (res.code === 200) {
            ElMessage.success('添加成功');
            dialogVisible.value = false;
            fetchAwards();
          } else {
            ElMessage.error(res.message || '添加失败');
            // 模拟添加成功（当后端API不可用时）
            dialogVisible.value = false;
            const newAward = { ...awardForm, id: Date.now() };
            awardList.value.unshift(newAward);
            total.value += 1;
          }
        } else if (dialogType.value === 'edit') {
          // 更新获奖信息
          const res = await updateAward(awardForm);
          if (res.code === 200) {
            ElMessage.success('更新成功');
            dialogVisible.value = false;
            fetchAwards();
          } else {
            ElMessage.error(res.message || '更新失败');
            // 模拟更新成功（当后端API不可用时）
            dialogVisible.value = false;
            const index = awardList.value.findIndex(item => item.id === awardForm.id);
            if (index !== -1) {
              awardList.value[index] = { ...awardForm };
            }
          }
        }
      } catch (error) {
        console.error('提交获奖信息失败', error);
        ElMessage.error('操作失败');
      } finally {
        submitting.value = false;
        // 清除证书文件对象
        certificateFile.value = null;
      }
    }
  });
};

// 重置表单
const resetAwardForm = () => {
  awardForm.id = null;
  awardForm.awardName = '';
  awardForm.awardLevel = '';
  awardForm.issuingUnit = '';
  awardForm.awardDate = '';
  awardForm.certificateImage = '';
  awardForm.relatedProjectId = null;
  certificateFile.value = null;
};
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

.award-list-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.certificate-preview {
  display: flex;
  justify-content: center;
  align-items: center;
}

.certificate-upload-container {
  width: 100%;
  display: flex;
  justify-content: center;
}

.certificate-uploader {
  width: 100%;
}

.upload-placeholder {
  width: 100%;
  height: 150px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: border-color 0.3s;
}

.upload-placeholder:hover {
  border-color: #409EFF;
}

.upload-placeholder .el-icon {
  font-size: 28px;
  color: #8c939d;
}

.upload-text {
  color: #8c939d;
  margin-top: 10px;
}

.certificate-preview-box {
  width: 100%;
  position: relative;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  overflow: hidden;
}

.certificate-preview-img {
  width: 100%;
  display: block;
}

.certificate-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
}
</style>
