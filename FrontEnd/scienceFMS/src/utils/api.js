/**
 * API请求工具
 */
import axios from 'axios';
import router from '../router';

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
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    if (token) {
      // 添加Authorization请求头
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
    if (error.response) {
      switch (error.response.status) {
        case 401: // 未授权
          // 清除token和用户信息
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          localStorage.removeItem('isLoggedIn');
          // 重定向到登录页
          router.push('/login');
          break;
        case 403: // 禁止访问
          console.error('没有权限访问该资源');
          break;
        case 500: // 服务器错误
          console.error('服务器错误');
          break;
        default:
          console.error(`请求错误: ${error.response.status}`);
      }
    } else {
      console.error('网络错误，请检查您的网络连接');
    }
    return Promise.reject(error);
  }
);

/**
 * 登录请求
 * @param {Object} data - 登录信息
 * @returns {Promise}
 */
export const login = (data) => {
  return api.post('/auth/login', data);
};

/**
 * 检查登录状态
 * @returns {Promise}
 */
export const checkLogin = () => {
  return api.get('/auth/checkLogin');
};

/**
 * 获取当前登录教师的个人资料
 * @returns {Promise}
 */
export const getTeacherProfile = () => {
  // 从localStorage获取用户信息
  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : null;
  
  if (user) {
    // 如果有用户名，优先使用用户名
    if (user.username) {
      return api.get(`/teacher/profile/me?username=${user.username}`);
    } 
    // 否则使用用户ID
    else if (user.userId) {
      return api.get(`/teacher/profile/me?userId=${user.userId}`);
    }
  }
  
  // 如果没有用户信息，仍然发送请求(将在后端处理错误)
  return api.get('/teacher/profile/me');
};

/**
 * 根据ID获取教师个人资料
 * @param {Number} teacherId - 教师ID
 * @returns {Promise}
 */
export const getTeacherProfileById = (teacherId) => {
  return api.get(`/teacher/profile/${teacherId}`);
};

/**
 * 获取教师的科研项目列表（分页）
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export const getResearchProjects = (params) => {
  return api.get('/projects/page', { params });
};

/**
 * 获取单个科研项目详情
 * @param {Number} id - 项目ID
 * @returns {Promise}
 */
export const getResearchProjectById = (id) => {
  return api.get(`/projects/${id}`);
};

/**
 * 添加科研项目
 * @param {Object} project - 项目信息
 * @returns {Promise}
 */
export const addResearchProject = (project) => {
  return api.post('/projects/add', project);
};

/**
 * 更新科研项目
 * @param {Object} project - 项目信息
 * @returns {Promise}
 */
export const updateResearchProject = (project) => {
  return api.put('/projects/update', project);
};

/**
 * 删除科研项目
 * @param {Number} id - 项目ID
 * @returns {Promise}
 */
export const deleteResearchProject = (id) => {
  return api.delete(`/projects/${id}`);
};

// 导出api实例
export default api; 