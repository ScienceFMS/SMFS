<template>
  <div class="personal-info">
    <h2 class="page-title">个人基本信息</h2>
    
    <el-card class="info-card" v-loading="loading">
      <div class="card-header">
        <div class="user-avatar">
          <el-avatar :size="80" :src="userInfo.avatarUrl" icon="User"></el-avatar>
        </div>
        <div class="profile-header">
          <h3>{{ userInfo.realName }}</h3>
          <p>{{ userInfo.department }} | {{ userInfo.title }}</p>
          <el-button type="primary" size="small" @click="openEditDialog">编辑资料</el-button>
        </div>
      </div>
      
      <el-divider></el-divider>
      
      <el-descriptions title="基本信息" :column="2" border>
        <el-descriptions-item label="工号">{{ userInfo.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ userInfo.realName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ userInfo.gender }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ formatDate(userInfo.birthDate) }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ userInfo.title }}</el-descriptions-item>
        <el-descriptions-item label="所属院系">{{ userInfo.department }}</el-descriptions-item>
        <el-descriptions-item label="研究方向">{{ userInfo.researchArea }}</el-descriptions-item>
        <el-descriptions-item label="电子邮箱">{{ userInfo.email }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ userInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="办公地点">{{ userInfo.officeLocation }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider></el-divider>
      
      <div class="section">
        <div class="section-header">
        <h4>教育背景</h4>
          <el-button 
            type="success" 
            size="small" 
            @click="openAddEducationDialog"
            class="add-education-btn"
          >添加</el-button>
        </div>
        <el-timeline v-if="userInfo.educationList && userInfo.educationList.length > 0">
          <el-timeline-item
            v-for="(edu, index) in userInfo.educationList"
            :key="index"
            :timestamp="edu.period"
            placement="top"
          >
            <el-card>
              <div class="education-item">
                <div class="education-content">
              <h4>{{ edu.degree }} - {{ edu.institution }}</h4>
              <p>{{ edu.major }}</p>
                </div>
                <div class="education-actions">
                  <el-button type="primary" size="small" icon="Edit" @click="editEducation(edu)" circle></el-button>
                  <el-button type="danger" size="small" icon="Delete" @click="confirmDeleteEducation(edu)" circle></el-button>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无教育背景信息"></el-empty>
      </div>
    </el-card>
    
    <!-- 编辑基本信息对话框 -->
    <el-dialog
      title="编辑个人资料"
      v-model="editDialogVisible"
      width="650px"
      @close="resetForm"
    >
      <el-form :model="editForm" label-width="100px" :rules="rules" ref="editFormRef">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <!-- 头像上传 -->
            <div class="avatar-upload-container">
              <el-avatar :size="100" :src="editForm.avatarUrl || ''" icon="User" class="preview-avatar"></el-avatar>
              <div class="avatar-upload-actions">
                <el-upload
                  class="avatar-uploader"
                  action="#"
                  :http-request="uploadAvatar"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                >
                  <el-button type="primary" size="small">更换头像</el-button>
                </el-upload>
              </div>
            </div>
            <el-divider></el-divider>
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="editForm.realName" placeholder="请输入姓名"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="editForm.gender">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker 
                v-model="editForm.birthDate" 
                type="date" 
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="职称" prop="title">
              <el-select v-model="editForm.title" placeholder="请选择职称" style="width: 100%">
                <el-option label="教授" value="教授"></el-option>
                <el-option label="副教授" value="副教授"></el-option>
                <el-option label="讲师" value="讲师"></el-option>
                <el-option label="助教" value="助教"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="所属院系" prop="department">
              <el-input v-model="editForm.department" placeholder="请输入所属院系"></el-input>
            </el-form-item>
            <el-form-item label="职位" prop="position">
              <el-input v-model="editForm.position" placeholder="请输入职位"></el-input>
            </el-form-item>
            <el-form-item label="研究方向" prop="researchArea">
              <el-input 
                v-model="editForm.researchArea" 
                placeholder="请输入研究方向，多个方向请用逗号分隔"
                type="textarea"
                :rows="2"
              ></el-input>
            </el-form-item>
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="editForm.email" placeholder="请输入电子邮箱"></el-input>
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="editForm.phone" placeholder="请输入联系电话"></el-input>
            </el-form-item>
            <el-form-item label="办公地点" prop="officeLocation">
              <el-input v-model="editForm.officeLocation" placeholder="请输入办公地点"></el-input>
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm" :loading="submitting">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 添加/编辑教育背景对话框 -->
    <el-dialog
      :title="educationForm.id ? '编辑教育背景' : '添加教育背景'"
      v-model="educationDialogVisible"
      width="550px"
    >
      <el-form :model="educationForm" label-width="100px" :rules="educationRules" ref="educationFormRef">
        <el-form-item label="学位" prop="degree">
          <el-select v-model="educationForm.degree" placeholder="请选择学位" style="width: 100%">
            <el-option label="博士" value="博士"></el-option>
            <el-option label="硕士" value="硕士"></el-option>
            <el-option label="学士" value="学士"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="院校" prop="institution">
          <el-input v-model="educationForm.institution" placeholder="请输入院校名称"></el-input>
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="educationForm.major" placeholder="请输入专业名称"></el-input>
        </el-form-item>
        <el-form-item label="起止年份" prop="years">
          <el-row :gutter="10">
            <el-col :span="11">
              <el-form-item prop="startYear">
                <el-input-number v-model="educationForm.startYear" :min="1950" :max="2050" placeholder="开始年份" style="width: 100%"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="2" class="text-center">
              <span>至</span>
            </el-col>
            <el-col :span="11">
              <el-form-item prop="endYear">
                <el-input-number v-model="educationForm.endYear" :min="1950" :max="2050" placeholder="结束年份" style="width: 100%"></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="educationDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEducationForm" :loading="submitting">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { User, Plus, Edit, Delete, Upload } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getTeacherProfile, updateTeacherProfile, addTeacherEducation, updateTeacherEducation, deleteTeacherEducation, updateAvatarUrl } from '../../utils/api';
import { uploadFile } from '../../utils/fileService';
import * as auth from '../../utils/auth';

const loading = ref(true);
const submitting = ref(false);
const editDialogVisible = ref(false);
const educationDialogVisible = ref(false);
const activeTab = ref('basic');
const editFormRef = ref(null);
const educationFormRef = ref(null);

const userInfo = ref({
  id: '',
  realName: '',
  gender: '',
  birthDate: '',
  title: '',
  department: '',
  position: '',
  researchArea: '',
  email: '',
  phone: '',
  officeLocation: '',
  avatarUrl: '',
  educationList: []
});

// 编辑表单数据
const editForm = ref({
  id: '',
  username: '',
  realName: '',
  gender: '',
  birthDate: '',
  title: '',
  department: '',
  position: '',
  researchArea: '',
  email: '',
  phone: '',
  officeLocation: '',
  avatarUrl: ''
});

// 教育背景表单
const educationForm = ref({
  id: null,
  degree: '',
  institution: '',
  major: '',
  startYear: null,
  endYear: null
});

// 表单校验规则
const rules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  email: [
    { required: true, message: '请输入电子邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
};

// 教育背景表单校验规则
const educationRules = {
  degree: [{ required: true, message: '请选择学位', trigger: 'change' }],
  institution: [{ required: true, message: '请输入院校名称', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业名称', trigger: 'blur' }],
  startYear: [{ required: true, message: '请输入开始年份', trigger: 'blur' }],
  endYear: [
    { required: true, message: '请输入结束年份', trigger: 'blur' },
    { validator: validateYears, trigger: 'blur' }
  ]
};

// 校验年份
function validateYears(rule, value, callback) {
  if (educationForm.value.startYear && value && educationForm.value.startYear > value) {
    callback(new Error('结束年份必须大于开始年份'));
  } else {
    callback();
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  
  try {
    const date = new Date(dateStr);
    return date.toLocaleDateString('zh-CN');
  } catch (error) {
    return dateStr;
  }
};

// 获取教师个人资料
const fetchUserInfo = async () => {
  loading.value = true;
  console.log('[PersonalInfo] 开始请求教师个人资料...');
  try {
    const response = await getTeacherProfile();
    console.log('[PersonalInfo] 获取个人资料响应:', response);
    if (response.success) {
      userInfo.value = response.data;
    } else {
      ElMessage.error('获取个人信息失败');
    }
  } catch (error) {
    console.error('[PersonalInfo] 获取用户信息失败:', error);
    ElMessage.error('获取个人信息失败，请稍后重试');
    
    // 开发环境：如果API未连接，使用本地模拟数据
    if (process.env.NODE_ENV === 'development') {
      simulateUserData();
    }
  } finally {
    loading.value = false;
  }
};

// 模拟用户数据（仅用于开发环境）
const simulateUserData = () => {
  console.log('[PersonalInfo] 使用模拟数据');
  userInfo.value = {
    id: '10001',
    username: 'teacher1',
    realName: '张教授',
    gender: '男',
    birthDate: '1975-01-15',
    title: '教授',
    department: '计算机科学与技术学院',
    position: '教授',
    researchArea: '人工智能、大数据分析',
    email: 'zhang@example.edu.cn',
    phone: '13911111111',
    officeLocation: '科研楼A座301室',
    avatarUrl: '',
    educationList: [
      {
        id: 1,
        degree: '博士',
        institution: '北京大学',
        major: '计算机科学',
        startYear: 2000,
        endYear: 2005,
        period: '2000-2005'
      },
      {
        id: 2,
        degree: '硕士',
        institution: '清华大学',
        major: '软件工程',
        startYear: 1997,
        endYear: 2000,
        period: '1997-2000'
      },
      {
        id: 3,
        degree: '学士',
        institution: '浙江大学',
        major: '计算机科学与技术',
        startYear: 1993,
        endYear: 1997,
        period: '1993-1997'
      }
    ]
  };
};

// 打开编辑对话框
const openEditDialog = () => {
  // 复制用户信息到编辑表单
  Object.assign(editForm.value, userInfo.value);
  editDialogVisible.value = true;
};

// 重置表单
const resetForm = () => {
  if (editFormRef.value) {
    editFormRef.value.resetFields();
  }
};

// 提交编辑表单
const submitEditForm = async () => {
  if (!editFormRef.value) return;
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        const response = await updateTeacherProfile(editForm.value);
        if (response.success) {
          ElMessage.success('个人信息更新成功');
          // 更新页面数据
          userInfo.value = response.data;
          editDialogVisible.value = false;
        } else {
          ElMessage.error(response.message || '更新失败');
        }
      } catch (error) {
        console.error('[PersonalInfo] 更新个人信息失败:', error);
        ElMessage.error('更新失败，请稍后重试');
        
        // 开发环境：模拟更新成功
        if (process.env.NODE_ENV === 'development') {
          ElMessage.success('模拟更新成功');
          Object.assign(userInfo.value, editForm.value);
          editDialogVisible.value = false;
        }
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 打开添加教育背景对话框
const openAddEducationDialog = () => {
  // 重置表单
  educationForm.value = {
    id: null,
    degree: '',
    institution: '',
    major: '',
    startYear: null,
    endYear: null
  };
  educationDialogVisible.value = true;
};

// 编辑教育背景
const editEducation = (education) => {
  // 复制教育背景信息到表单
  Object.assign(educationForm.value, education);
  educationDialogVisible.value = true;
};

// 提交教育背景表单
const submitEducationForm = async () => {
  if (!educationFormRef.value) return;
  
  await educationFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        let response;
        
        // 根据是否有ID判断是添加还是更新
        if (educationForm.value.id) {
          // 更新
          response = await updateTeacherEducation(educationForm.value.id, educationForm.value);
        } else {
          // 添加
          response = await addTeacherEducation(userInfo.value.id, educationForm.value);
        }
        
        if (response.success) {
          ElMessage.success(response.message);
          // 刷新教育背景列表
          await fetchUserInfo();
          educationDialogVisible.value = false;
        } else {
          ElMessage.error(response.message || '操作失败');
        }
      } catch (error) {
        console.error('[PersonalInfo] 教育背景操作失败:', error);
        ElMessage.error('操作失败，请稍后重试');
        
        // 开发环境：模拟操作成功
        if (process.env.NODE_ENV === 'development') {
          ElMessage.success('模拟操作成功');
          
          if (educationForm.value.id) {
            // 更新现有记录
            const index = userInfo.value.educationList.findIndex(item => item.id === educationForm.value.id);
            if (index !== -1) {
              const updatedEducation = { ...educationForm.value };
              updatedEducation.period = `${updatedEducation.startYear}-${updatedEducation.endYear}`;
              userInfo.value.educationList[index] = updatedEducation;
            }
          } else {
            // 添加新记录
            const newEducation = { ...educationForm.value };
            newEducation.id = Date.now(); // 模拟ID
            newEducation.period = `${newEducation.startYear}-${newEducation.endYear}`;
            userInfo.value.educationList.unshift(newEducation);
          }
          
          educationDialogVisible.value = false;
        }
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 确认删除教育背景
const confirmDeleteEducation = (education) => {
  ElMessageBox.confirm(
    `确定要删除 ${education.institution} 的 ${education.degree} 学历记录吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    deleteEducationRecord(education.id);
  }).catch(() => {
    // 取消删除
  });
};

// 删除教育背景记录
const deleteEducationRecord = async (educationId) => {
  try {
    const response = await deleteTeacherEducation(educationId);
    if (response.success) {
      ElMessage.success(response.message);
      // 刷新教育背景列表
      await fetchUserInfo();
    } else {
      ElMessage.error(response.message || '删除失败');
    }
  } catch (error) {
    console.error('[PersonalInfo] 删除教育背景失败:', error);
    ElMessage.error('删除失败，请稍后重试');
    
    // 开发环境：模拟删除成功
    if (process.env.NODE_ENV === 'development') {
      ElMessage.success('模拟删除成功');
      userInfo.value.educationList = userInfo.value.educationList.filter(item => item.id !== educationId);
    }
  }
};

// 头像上传前的验证
const beforeAvatarUpload = (file) => {
  // 检查文件类型
  const isImage = /^image\/(jpeg|png|gif|jpg)$/.test(file.type);
  if (!isImage) {
    ElMessage.error('头像只能是图片格式（JPG、PNG、GIF）!');
    return false;
  }
  
  // 检查文件大小（限制为2MB）
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过2MB!');
    return false;
  }
  
  return true;
};

// 上传头像
const uploadAvatar = async (options) => {
  const { file } = options;
  
  try {
    submitting.value = true;
    
    // 1. 先上传到静态资源服务器
    const uploadResult = await uploadFile(file, 'avatar');
    
    if (!uploadResult.success) {
      ElMessage.error(uploadResult.message || '头像上传失败');
      return;
    }
    
    // 2. 通知后端更新数据库
    const updateResult = await updateAvatarUrl({
      teacherId: editForm.value.id,
      fileUrl: uploadResult.url,
      fileName: uploadResult.fileName
    });
    
    if (updateResult.success) {
      // 更新头像URL
      editForm.value.avatarUrl = uploadResult.url;
      ElMessage.success('头像上传成功');
    } else {
      ElMessage.error(updateResult.message || '头像URL更新失败');
    }
  } catch (error) {
    console.error('[PersonalInfo] 头像上传失败:', error);
    ElMessage.error('头像上传失败，请稍后重试');
    
    // 开发环境：模拟上传成功
    if (process.env.NODE_ENV === 'development') {
      // 创建本地预览URL
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        editForm.value.avatarUrl = reader.result;
        ElMessage.success('模拟头像上传成功');
      };
    }
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  console.log('[PersonalInfo] 组件已挂载');
  // 检查用户是否已登录
  if (auth.isAuthenticated()) {
    console.log('[PersonalInfo] 用户已认证，获取个人资料');
    fetchUserInfo();
  } else {
    console.log('[PersonalInfo] 用户未认证');
    // 开发环境：如果未登录，使用本地模拟数据
    if (process.env.NODE_ENV === 'development') {
      simulateUserData();
      loading.value = false;
    } else {
      // 生产环境：未登录则跳转到登录页
      ElMessage.warning('请先登录');
      loading.value = false;
    }
  }
});
</script>

<style scoped>
.personal-info {
  padding: 20px 0;
}

.page-title {
  margin-bottom: 20px;
  font-weight: 500;
  color: #303133;
}

.info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  padding: 20px 0;
}

.user-avatar {
  margin-right: 30px;
}

.profile-header {
  flex: 1;
}

.profile-header h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 20px;
  color: #303133;
}

.profile-header p {
  margin-top: 0;
  margin-bottom: 15px;
  color: #606266;
}

.section {
  margin-top: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h4 {
  margin: 0;
  font-weight: 500;
  color: #303133;
}

.education-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.education-content {
  flex: 1;
}

.education-content h4 {
  margin-top: 0;
  margin-bottom: 5px;
}

.education-content p {
  margin: 0;
  color: #606266;
}

.education-actions {
  display: flex;
  gap: 10px;
}

.text-center {
  text-align: center;
  line-height: 32px;
}

:deep(.el-timeline-item__node) {
  background-color: #409EFF;
}

.add-education-btn {
  border-radius: 20px;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  text-align: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.add-education-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.preview-avatar {
  margin-bottom: 15px;
  border: 2px solid #ebeef5;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.avatar-upload-actions {
  display: flex;
  gap: 10px;
}
</style> 