/**
 * 管理员功能API调用模块
 */
import axios from 'axios';
import { getToken } from './auth';

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8082',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = getToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    // 检查是否有响应对象
    if (error.response) {
      console.log('API错误响应:', error.response);
      
      // 处理401错误（未授权/token过期）
      if (error.response.status === 401) {
        console.log('检测到401未授权错误，正在清除凭证并跳转到登录页面');
        // 清除所有认证相关信息
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('isLoggedIn');
        
        // 延迟执行跳转，确保消息能够显示
        setTimeout(() => {
          window.location.href = '/login'; // 使用window.location强制跳转，避免路由问题
        }, 100);
        return Promise.reject(new Error('登录已过期，请重新登录'));
      }
      
      // 处理其他HTTP错误
      switch (error.response.status) {
        case 403: // 禁止访问
          console.error('没有权限访问该资源');
          break;
        case 500: // 服务器错误
          console.error('服务器错误');
          break;
        default:
          console.error(`请求错误: ${error.response.status}`);
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      console.error('未收到响应，可能是网络问题或CORS错误', error.request);
      
      // 检查错误消息是否包含401相关内容
      if (error.message && (
          error.message.includes('401') || 
          error.message.includes('unauthorized') || 
          error.message.includes('Unauthorized')
      )) {
        console.log('检测到可能的401错误，正在清除凭证并跳转到登录页面');
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('isLoggedIn');
        
        setTimeout(() => {
          window.location.href = '/login';
        }, 100);
      }
    } else {
      // 请求设置触发的错误
      console.error('请求错误:', error.message);
    }
    return Promise.reject(error);
  }
);

/**
 * 多维查询科研成果
 * 
 * @param {Object} params - 查询参数
 * @param {String} params.types - 成果类型列表，逗号分隔，如：intellectualProperty,award,researchProject
 * @param {Number} params.teacherId - 教师ID（可选）
 * @param {Number} params.startYear - 起始年份（可选）
 * @param {Number} params.endYear - 结束年份（可选）
 * @param {String} params.keyword - 关键词（可选）
 * @param {String} params.sort - 排序方式（可选，默认按时间降序）
 * @param {Number} params.page - 页码（默认1）
 * @param {Number} params.pageSize - 每页大小（默认10）
 * @returns {Promise}
 */
export const searchResults = (params) => {
  return api.get('/admin/search/search', { params });
};

/**
 * 导出查询结果为Excel
 * 
 * @param {Object} params - 查询参数
 * @param {String} params.types - 成果类型列表，逗号分隔
 * @param {Number} params.teacherId - 教师ID（可选）
 * @param {Number} params.startYear - 起始年份（可选）
 * @param {Number} params.endYear - 结束年份（可选）
 * @param {String} params.keyword - 关键词（可选）
 * @param {String} params.sort - 排序方式（可选）
 * @returns {Promise}
 */
export const exportSearchResults = (params) => {
  // 使用blob响应类型获取Excel文件
  return axios({
    url: 'http://localhost:8082/admin/search/export',
    method: 'GET',
    responseType: 'blob',
    params,
    headers: {
      'Authorization': `Bearer ${getToken()}`
    }
  });
};

/**
 * 获取所有教师列表
 * 
 * @returns {Promise}
 */
export const getAllTeachers = () => {
  return api.get('/admin/teachers');
};

/**
 * 根据ID获取教师信息
 * 
 * @param {Number} id - 教师ID
 * @returns {Promise}
 */
export const getTeacherById = (id) => {
  return api.get(`/admin/teachers/${id}`);
};

// 系统管理相关API

/**
 * 获取系统概览数据
 * 
 * @returns {Promise}
 */
export const getSystemOverview = () => {
  return api.get('/admin/dashboard/overview');
};

/**
 * 获取系统用户列表
 * 
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export const getSystemUsers = (params) => {
  return api.get('/admin/users', { params });
};

/**
 * 获取系统配置
 * 
 * @returns {Promise}
 */
export const getSystemSettings = () => {
  return api.get('/admin/settings');
};

/**
 * 更新系统配置
 * 
 * @param {Object} settings - 系统配置
 * @returns {Promise}
 */
export const updateSystemSettings = (settings) => {
  return api.put('/admin/settings', settings);
};

// 用户管理相关API

/**
 * 添加用户
 * 
 * @param {Object} user - 用户信息
 * @returns {Promise}
 */
export const addUser = (user) => {
  return api.post('/admin/users', user);
};

/**
 * 更新用户信息
 * 
 * @param {Object} user - 用户信息
 * @returns {Promise}
 */
export const updateUser = (user) => {
  return api.put('/admin/users', user);
};

/**
 * 更新用户状态（锁定/解锁）
 * 
 * @param {Number} id - 用户ID
 * @param {Number} status - 状态（0=正常，1=锁定）
 * @returns {Promise}
 */
export const updateUserStatus = (id, status) => {
  return api.put(`/admin/users/${id}/status`, { status });
};

/**
 * 重置用户密码
 * 
 * @param {Number} id - 用户ID
 * @returns {Promise}
 */
export const resetUserPassword = (id) => {
  return api.put(`/admin/users/${id}/resetPassword`);
};

/**
 * 删除用户
 * 
 * @param {Number} id - 用户ID
 * @returns {Promise}
 */
export const deleteUser = (id) => {
  return api.delete(`/admin/users/${id}`);
}; 