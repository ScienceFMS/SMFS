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

/**
 * 获取教师的获奖信息列表（分页）
 * @param {Number} teacherId - 教师ID
 * @param {Number} page - 页码
 * @param {Number} pageSize - 每页条数
 * @param {Number} year - 年份筛选（可选）
 * @param {String} keyword - 关键词搜索（可选）
 * @returns {Promise}
 */
export const getAwardsByPage = (teacherId, page, pageSize, year, keyword) => {
  return api.get('/awards/page', { 
    params: { 
      teacherId, 
      page, 
      pageSize, 
      year: year || undefined,
      keyword: keyword || undefined 
    } 
  });
};

/**
 * 获取单个获奖信息详情
 * @param {Number} id - 获奖ID
 * @returns {Promise}
 */
export const getAwardById = (id) => {
  return api.get(`/awards/${id}`);
};

/**
 * 添加获奖信息
 * @param {Object} award - 获奖信息
 * @returns {Promise}
 */
export const addAward = (award) => {
  return api.post('/awards', award);
};

/**
 * 更新获奖信息
 * @param {Object} award - 获奖信息
 * @returns {Promise}
 */
export const updateAward = (award) => {
  return api.put('/awards', award);
};

/**
 * 删除获奖信息
 * @param {Number} id - 获奖ID
 * @returns {Promise}
 */
export const deleteAward = (id) => {
  return api.delete(`/awards/${id}`);
};

/**
 * 导出获奖信息为PDF
 * @param {Number} id - 获奖ID
 * @returns {Promise}
 */
export const exportAwardToPdf = (id) => {
  return api.get(`/awards/export/${id}`);
};

// 导出api实例
export default api; 