<template>
  <div class="settings">
    <div class="page-header">
      <h2 class="page-title">设置</h2>
    </div>
    
    <el-card class="settings-card">
      <el-tabs v-model="activeTab">
          <h3>账号设置</h3>
          <el-form :model="personalForm" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="personalForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="修改密码">
              <el-button type="primary" @click="showPasswordDialog = true">修改密码</el-button>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="personalForm.email"></el-input>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="personalForm.phone"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="savePersonalSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
      </el-tabs>
    </el-card>
    
    <!-- 修改密码对话框 -->
    <el-dialog
      title="修改密码"
      v-model="showPasswordDialog"
      width="400px"
    >
      <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showPasswordDialog = false">取消</el-button>
          <el-button type="primary" @click="updateUserPassword">确认修改</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getTeacherProfile, updatePassword, updateUserInfo } from '../../utils/api';
import router from '../../router';

// 当前激活的标签页
const activeTab = ref('personal');

// 个人设置表单
const personalForm = reactive({
  username: '',
  email: '',
  phone: ''
});

// 显示密码修改对话框
const showPasswordDialog = ref(false);

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 密码表单校验规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      }, 
      trigger: 'blur' 
    }
  ]
};

const passwordFormRef = ref(null);

// 页面加载时获取用户信息
onMounted(async () => {
  try {
    const res = await getTeacherProfile();
    if (res.success && res.data) {
      personalForm.username = res.data.username;
      personalForm.email = res.data.email;
      personalForm.phone = res.data.phone;
    }
  } catch (error) {
    console.error('获取教师资料失败:', error);
    ElMessage.error('获取教师资料失败');
  }
});

// 保存个人设置
const savePersonalSettings = async () => {
  try {
    const res = await updateUserInfo({
      username: personalForm.username,
      email: personalForm.email,
      phone: personalForm.phone
    });
    
    // 判断后端返回结果
    if (res.code === 200) {
      ElMessage.success('个人设置保存成功');
    } else {
      ElMessage.error(res.message || '保存个人设置失败');
    }
  } catch (error) {
    console.error('保存个人设置失败:', error);
    ElMessage.error('保存个人设置失败');
  }
};

// 修改密码
const updateUserPassword = async () => {
  try {
    // 表单验证
    await passwordFormRef.value.validate();
    
    // 提交请求
    const res = await updatePassword({
      username: personalForm.username,
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    });
    console.log(res);
    
    // 判断后端返回结果
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录');
      
      // 清除登录状态
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('isLoggedIn');
      
      // 延迟跳转到登录页面
      setTimeout(() => {
        router.push('/login');
      }, 1500);
    } else {
      ElMessage.error(res.message || '修改密码失败');
    }
  } catch (error) {
    console.error('修改密码失败:', error);
    ElMessage.error(error.message || '修改密码失败');
  }
};
</script>

<style scoped>
.settings {
  padding: 20px 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-weight: 500;
  color: #303133;
}

.settings-card {
  margin-bottom: 20px;
}

h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-weight: 500;
  color: #303133;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 