<template>
  <div class="user-management-container">
    <h2>用户管理</h2>
    
    <div class="action-bar">
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索用户名/姓名/部门" 
          class="search-input"
        />
        <button class="search-btn" @click="searchUsers">搜索</button>
      </div>
      
      <div class="filters">
        <select v-model="roleFilter" @change="searchUsers" class="filter-select">
          <option value="">所有角色</option>
          <option value="teacher">教师</option>
          <option value="admin">管理员</option>
        </select>
        
        <select v-model="statusFilter" @change="searchUsers" class="filter-select">
          <option value="">所有状态</option>
          <option value="0">正常</option>
          <option value="1">锁定</option>
        </select>
      </div>
      
      <button class="add-btn" @click="showAddUserDialog">添加用户</button>
    </div>
    
    <div class="users-table-container">
      <table class="users-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>姓名</th>
            <th>角色</th>
            <th>邮箱</th>
            <th>部门</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading" class="loading-row">
            <td colspan="8">数据加载中...</td>
          </tr>
          <tr v-else-if="users.length === 0" class="empty-row">
            <td colspan="8">暂无用户数据</td>
          </tr>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.realName }}</td>
            <td>
              <span :class="getRoleBadgeClass(user.role)">
                {{ getRoleLabel(user.role) }}
              </span>
            </td>
            <td>{{ user.email }}</td>
            <td>{{ user.department || '-' }}</td>
            <td>
              <span :class="getStatusBadgeClass(user.status)">
                {{ getStatusLabel(user.status) }}
              </span>
            </td>
            <td>
              <div class="actions">
                <button class="edit-btn" @click="editUser(user)" title="编辑">
                  <i class="fas fa-edit"></i>
                </button>
                <button 
                  class="toggle-status-btn" 
                  :class="user.status === 0 ? 'lock-btn' : 'unlock-btn'"
                  @click="toggleUserStatus(user)" 
                  :title="user.status === 0 ? '锁定' : '解锁'"
                >
                  <i :class="user.status === 0 ? 'fas fa-lock' : 'fas fa-unlock'"></i>
                </button>
                <button class="reset-pwd-btn" @click="resetPassword(user)" title="重置密码">
                  <i class="fas fa-key"></i>
                </button>
                <button class="delete-btn" @click="confirmDeleteUser(user)" title="删除">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
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
    
    <!-- 添加/编辑用户对话框 -->
    <div class="dialog-overlay" v-if="showDialog" @click="closeDialog"></div>
    <div class="dialog" v-if="showDialog">
      <div class="dialog-header">
        <h3>{{ isEditing ? '编辑用户' : '添加用户' }}</h3>
        <button class="close-btn" @click="closeDialog"><i class="fas fa-times"></i></button>
      </div>
      
      <div class="dialog-body">
        <div class="form-group">
          <label>用户名</label>
          <input 
            type="text" 
            v-model="userForm.username" 
            :disabled="isEditing" 
            class="form-input"
          />
        </div>
        
        <div class="form-group">
          <label>姓名</label>
          <input type="text" v-model="userForm.realName" class="form-input" />
        </div>
        
        <div class="form-group">
          <label>角色</label>
          <select v-model="userForm.role" class="form-select">
            <option value="teacher">教师</option>
            <option value="admin">管理员</option>
          </select>
        </div>
        
        <div class="form-group">
          <label>邮箱</label>
          <input type="email" v-model="userForm.email" class="form-input" />
        </div>
        
        <div class="form-group">
          <label>手机号</label>
          <input type="tel" v-model="userForm.phone" class="form-input" />
        </div>
        
        <div class="form-group">
          <label>部门</label>
          <input type="text" v-model="userForm.department" class="form-input" />
        </div>
        
        <div class="form-group" v-if="!isEditing">
          <label>密码</label>
          <input type="password" v-model="userForm.password" class="form-input" />
        </div>
      </div>
      
      <div class="dialog-footer">
        <button class="cancel-btn" @click="closeDialog">取消</button>
        <button class="save-btn" @click="saveUser">保存</button>
      </div>
    </div>
    
    <!-- 确认对话框 -->
    <div class="dialog-overlay" v-if="showConfirmDialog" @click="closeConfirmDialog"></div>
    <div class="confirm-dialog" v-if="showConfirmDialog">
      <div class="confirm-dialog-content">
        <div class="confirm-dialog-icon">
          <i class="fas fa-exclamation-triangle"></i>
        </div>
        <h3>{{ confirmDialogTitle }}</h3>
        <p>{{ confirmDialogMessage }}</p>
        <div class="confirm-dialog-actions">
          <button class="cancel-btn" @click="closeConfirmDialog">取消</button>
          <button class="confirm-btn" @click="confirmAction">确认</button>
        </div>
      </div>
    </div>
    
    <!-- 消息提示 -->
    <div 
      class="message-toast" 
      v-if="showMessage"
      :class="{ success: messageType === 'success', error: messageType === 'error' }"
    >
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue';
import * as adminApi from '../../utils/adminApi';

// 状态变量
const loading = ref(true);
const users = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalUsers = ref(0);
const totalPages = ref(0);
const searchKeyword = ref('');
const roleFilter = ref('');
const statusFilter = ref('');

// 对话框相关
const showDialog = ref(false);
const isEditing = ref(false);
const userForm = reactive({
  id: null,
  username: '',
  realName: '',
  role: 'teacher',
  email: '',
  phone: '',
  department: '',
  password: '',
  status: 0
});

// 确认对话框相关
const showConfirmDialog = ref(false);
const confirmDialogTitle = ref('');
const confirmDialogMessage = ref('');
const confirmAction = ref(() => {});

// 消息提示相关
const showMessage = ref(false);
const message = ref('');
const messageType = ref('success');

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
  fetchUsers();
});

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await adminApi.getSystemUsers({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined,
      role: roleFilter.value || undefined,
      status: statusFilter.value || undefined
    });
    
    if (response.code === 200 && response.data) {
      users.value = response.data.records || [];
      totalUsers.value = response.data.total || 0;
      totalPages.value = response.data.pages || 0;
    } else {
      showToast('获取用户列表失败: ' + (response.message || '未知错误'), 'error');
      users.value = [];
      totalUsers.value = 0;
      totalPages.value = 0;
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    showToast('获取用户列表失败', 'error');
    users.value = [];
    totalUsers.value = 0;
    totalPages.value = 0;
  } finally {
    loading.value = false;
  }
};

// 搜索用户
const searchUsers = () => {
  currentPage.value = 1;  // 重置到第一页
  fetchUsers();
};

// 切换页码
const changePage = (page) => {
  currentPage.value = page;
  fetchUsers();
};

// 显示添加用户对话框
const showAddUserDialog = () => {
  isEditing.value = false;
  resetUserForm();
  showDialog.value = true;
};

// 编辑用户
const editUser = (user) => {
  isEditing.value = true;
  
  // 复制用户数据到表单
  userForm.id = user.id;
  userForm.username = user.username;
  userForm.realName = user.realName;
  userForm.role = user.role;
  userForm.email = user.email;
  userForm.phone = user.phone;
  userForm.department = user.department || '';
  userForm.status = user.status;
  userForm.password = ''; // 不显示密码
  
  showDialog.value = true;
};

// 保存用户
const saveUser = async () => {
  // 表单验证
  if (!userForm.username) {
    showToast('用户名不能为空', 'error');
    return;
  }
  
  if (!userForm.realName) {
    showToast('姓名不能为空', 'error');
    return;
  }
  
  if (!isEditing.value && !userForm.password) {
    showToast('密码不能为空', 'error');
    return;
  }
  
  try {
    if (isEditing.value) {
      // 更新用户
      await adminApi.updateUser(userForm);
      showToast('用户更新成功', 'success');
    } else {
      // 添加用户
      await adminApi.addUser(userForm);
      showToast('用户添加成功', 'success');
    }
    
    // 关闭对话框并刷新列表
    closeDialog();
    fetchUsers();
  } catch (error) {
    console.error('保存用户失败:', error);
    showToast('保存用户失败', 'error');
  }
};

// 关闭对话框
const closeDialog = () => {
  showDialog.value = false;
  resetUserForm();
};

// 重置用户表单
const resetUserForm = () => {
  userForm.id = null;
  userForm.username = '';
  userForm.realName = '';
  userForm.role = 'teacher';
  userForm.email = '';
  userForm.phone = '';
  userForm.department = '';
  userForm.password = '';
  userForm.status = 0;
};

// 切换用户状态
const toggleUserStatus = (user) => {
  const newStatus = user.status === 0 ? 1 : 0;
  const action = newStatus === 1 ? '锁定' : '解锁';
  
  confirmDialogTitle.value = `${action}用户`;
  confirmDialogMessage.value = `确定要${action}用户 "${user.realName}" 吗？`;
  confirmAction.value = async () => {
    try {
      await adminApi.updateUserStatus(user.id, newStatus);
      user.status = newStatus; // 直接更新本地状态
      showToast(`用户${action}成功`, 'success');
      closeConfirmDialog();
    } catch (error) {
      console.error(`${action}用户失败:`, error);
      showToast(`${action}用户失败`, 'error');
    }
  };
  
  showConfirmDialog.value = true;
};

// 重置密码
const resetPassword = (user) => {
  confirmDialogTitle.value = '重置密码';
  confirmDialogMessage.value = `确定要重置用户 "${user.realName}" 的密码吗？`;
  confirmAction.value = async () => {
    try {
      await adminApi.resetUserPassword(user.id);
      showToast('密码重置成功', 'success');
      closeConfirmDialog();
    } catch (error) {
      console.error('重置密码失败:', error);
      showToast('重置密码失败', 'error');
    }
  };
  
  showConfirmDialog.value = true;
};

// 确认删除用户
const confirmDeleteUser = (user) => {
  confirmDialogTitle.value = '删除用户';
  confirmDialogMessage.value = `确定要删除用户 "${user.realName}" 吗？此操作不可撤销！`;
  confirmAction.value = async () => {
    try {
      await adminApi.deleteUser(user.id);
      showToast('用户删除成功', 'success');
      closeConfirmDialog();
      fetchUsers(); // 刷新列表
    } catch (error) {
      console.error('删除用户失败:', error);
      showToast('删除用户失败', 'error');
    }
  };
  
  showConfirmDialog.value = true;
};

// 关闭确认对话框
const closeConfirmDialog = () => {
  showConfirmDialog.value = false;
  confirmDialogTitle.value = '';
  confirmDialogMessage.value = '';
  confirmAction.value = () => {};
};

// 显示消息提示
const showToast = (msg, type = 'success') => {
  message.value = msg;
  messageType.value = type;
  showMessage.value = true;
  
  setTimeout(() => {
    showMessage.value = false;
  }, 3000);
};

// 获取角色标签
const getRoleLabel = (role) => {
  switch (role) {
    case 'admin':
      return '管理员';
    case 'teacher':
      return '教师';
    default:
      return role;
  }
};

// 获取角色标签样式类
const getRoleBadgeClass = (role) => {
  switch (role) {
    case 'admin':
      return 'role-badge admin-badge';
    case 'teacher':
      return 'role-badge teacher-badge';
    default:
      return 'role-badge';
  }
};

// 获取状态标签
const getStatusLabel = (status) => {
  return status === 0 ? '正常' : '锁定';
};

// 获取状态标签样式类
const getStatusBadgeClass = (status) => {
  return status === 0 ? 'status-badge status-normal' : 'status-badge status-locked';
};
</script>

<style scoped>
.user-management-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  font-size: 1.6rem;
  color: #333;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-box {
  display: flex;
  gap: 10px;
  flex: 1;
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-btn:hover {
  background-color: #40a9ff;
}

.filters {
  display: flex;
  gap: 10px;
}

.filter-select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.add-btn {
  padding: 8px 16px;
  background-color: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.add-btn:hover {
  background-color: #73d13d;
}

/* 用户表格 */
.users-table-container {
  overflow-x: auto;
  margin-bottom: 20px;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th,
.users-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.users-table th {
  background-color: #fafafa;
  font-weight: 500;
}

.users-table tbody tr:hover {
  background-color: #f5f5f5;
}

.loading-row td,
.empty-row td {
  text-align: center;
  padding: 30px;
  color: #999;
}

.role-badge,
.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
}

.admin-badge {
  background-color: #ff7a45;
  color: white;
}

.teacher-badge {
  background-color: #36cfc9;
  color: white;
}

.status-normal {
  background-color: #52c41a;
  color: white;
}

.status-locked {
  background-color: #f5222d;
  color: white;
}

/* 操作按钮 */
.actions {
  display: flex;
  gap: 8px;
}

.edit-btn,
.toggle-status-btn,
.reset-pwd-btn,
.delete-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.edit-btn {
  background-color: #1890ff;
  color: white;
}

.lock-btn {
  background-color: #fa8c16;
  color: white;
}

.unlock-btn {
  background-color: #52c41a;
  color: white;
}

.reset-pwd-btn {
  background-color: #722ed1;
  color: white;
}

.delete-btn {
  background-color: #f5222d;
  color: white;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
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

/* 对话框 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 100;
}

.dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #fff;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  z-index: 101;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
  max-height: 60vh;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-input,
.form-select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
}

.cancel-btn,
.save-btn {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background-color: transparent;
  border: 1px solid #ddd;
}

.save-btn {
  background-color: #1890ff;
  color: white;
  border: none;
}

/* 确认对话框 */
.confirm-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #fff;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
  z-index: 101;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.confirm-dialog-content {
  padding: 24px;
  text-align: center;
}

.confirm-dialog-icon {
  font-size: 3rem;
  color: #faad14;
  margin-bottom: 16px;
}

.confirm-dialog-content h3 {
  margin: 0 0 16px;
  font-size: 1.2rem;
  color: #333;
}

.confirm-dialog-content p {
  margin-bottom: 24px;
  color: #666;
}

.confirm-dialog-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.confirm-dialog-actions button {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.confirm-dialog-actions .cancel-btn {
  background-color: transparent;
  border: 1px solid #ddd;
}

.confirm-dialog-actions .confirm-btn {
  background-color: #ff4d4f;
  color: white;
  border: none;
}

/* 消息提示 */
.message-toast {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 12px 24px;
  border-radius: 4px;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  animation: fadeInOut 3s;
}

.message-toast.success {
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
  color: #52c41a;
}

.message-toast.error {
  background-color: #fff2f0;
  border: 1px solid #ffccc7;
  color: #ff4d4f;
}

@keyframes fadeInOut {
  0% { opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { opacity: 0; }
}
</style> 