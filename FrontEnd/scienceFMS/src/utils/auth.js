/**
 * 身份验证和权限管理工具
 */

// 检查用户是否已登录
export const isAuthenticated = () => {
  console.log('isAuthenticated', localStorage.getItem('token'), localStorage.getItem('isLoggedIn'));
  return localStorage.getItem('token') !== null && localStorage.getItem('isLoggedIn') === 'true';
};

// 获取当前登录用户信息
export const getCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

// 获取JWT令牌
export const getToken = () => {
  return localStorage.getItem('token');
};

// 检查用户是否具有特定角色
export const hasRole = (role) => {
  const user = getCurrentUser();
  return user && user.role === role;
};

// 检查用户是否为教师
export const isTeacher = () => {
  return hasRole('teacher');
};

// 检查用户是否为管理员
export const isAdmin = () => {
  return hasRole('admin');
};

// 登录并保存用户信息和令牌，user中就是所有信息，好像不需要单独存储teacherID了
export const login = (loginData) => {
  localStorage.setItem('user', JSON.stringify(loginData));
  localStorage.setItem('token', loginData.token);
  localStorage.setItem('isLoggedIn', 'true');

  // 单独存储teacherId（如果存在）
  if (loginData.teacherId) {
    localStorage.setItem('teacherId', loginData.teacherId);
  }
};

// 登出
export const logout = () => {
  localStorage.removeItem('user');
  localStorage.removeItem('token');
  localStorage.removeItem('isLoggedIn');
};

// 权限常量，用于检查特定功能权限
export const PERMISSIONS = {
  VIEW_REPORTS: 'viewReports',         // 查看报表
  MANAGE_RESEARCH: 'manageResearch',   // 管理研究成果
  MANAGE_USERS: 'manageUsers',         // 管理用户
};

// 检查是否有特定权限（基于角色）
export const hasPermission = (permission) => {
  const user = getCurrentUser();
  if (!user) return false;
  
  // 管理员拥有所有权限
  if (user.role === 'admin') return true;
  
  // 教师角色权限
  if (user.role === 'teacher') {
    switch (permission) {
      case PERMISSIONS.VIEW_REPORTS:
        return true; // 教师可以查看报表
      case PERMISSIONS.MANAGE_RESEARCH:
        return true; // 教师可以管理自己的研究成果
      case PERMISSIONS.MANAGE_USERS:
        return false; // 教师不能管理用户
      default:
        return false;
    }
  }
  
  return false;
}; 