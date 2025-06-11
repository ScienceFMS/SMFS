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

/**
 * 获取教师的知识产权列表（分页）
 * @param {Object} params - 查询参数，包含teacherId、page、pageSize、type、keyword等
 * @returns {Promise}
 */
export const getIntellectualProperties = (params) => {
  return api.get('/intellectual-property/page', { params });
};

/**
 * 获取单个知识产权详情
 * @param {Number} id - 知识产权ID
 * @returns {Promise}
 */
export const getIntellectualPropertyById = (id) => {
  return api.get(`/intellectual-property/${id}`);
};

/**
 * 添加知识产权
 * @param {Object} property - 知识产权信息
 * @returns {Promise}
 */
export const addIntellectualProperty = (property) => {
  return api.post('/intellectual-property', property);
};

/**
 * 更新知识产权
 * @param {Object} property - 知识产权信息
 * @returns {Promise}
 */
export const updateIntellectualProperty = (property) => {
  return api.put('/intellectual-property', property);
};

/**
 * 删除知识产权
 * @param {Number} id - 知识产权ID
 * @returns {Promise}
 */
export const deleteIntellectualProperty = (id) => {
  return api.delete(`/intellectual-property/${id}`);
};

/**
 * 批量导出知识产权列表
 * @param {Object} params - 导出参数，可包含筛选条件
 * @returns {Promise}
 */
export const exportIntellectualProperties = (params) => {
  return api.get('/intellectual-property/export', { 
    params,
    responseType: 'blob'  // 指定响应类型为blob
  });
};

/**
 * 出访记录相关API
 */

/**
 * 获取教师的出访记录列表（分页）
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export const getVisitRecords = (params) => {
  return api.get('/visits/page', { params });
};

/**
 * 获取单个出访记录
 * @param {Number} id - 出访记录ID
 * @returns {Promise}
 */
export const getVisitRecordById = (id) => {
  return api.get(`/visits/${id}`);
};

/**
 * 添加出访记录
 * @param {Object} data - 出访记录数据
 * @returns {Promise}
 */
export const addVisitRecord = (data) => {
  return api.post('/visits', data);
};

/**
 * 更新出访记录
 * @param {Object} data - 出访记录数据
 * @returns {Promise}
 */
export const updateVisitRecord = (data) => {
  return api.put('/visits', data);
};

/**
 * 删除出访记录
 * @param {Number} id - 出访记录ID
 * @returns {Promise}
 */
export const deleteVisitRecord = (id) => {
  return api.delete(`/visits/${id}`);
};

/**
 * 上传行程单文件
 * @param {FormData} formData - 包含文件的表单数据
 * @returns {Promise}
 */
export const uploadItineraryFile = (formData) => {
  return api.post('/visits/upload/itinerary', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

/**
 * 上传成果报告文件
 * @param {FormData} formData - 包含文件的表单数据
 * @returns {Promise}
 */
export const uploadReportFile = (formData) => {
  return api.post('/visits/upload/report', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

/**
 * 获取出访记录年度统计
 * @param {Number} teacherId - 教师ID
 * @param {Number} year - 年份
 * @returns {Promise}
 */
export const getVisitYearlyStats = (teacherId, year) => {
  return api.get('/visits/stats/year', {
    params: { teacherId, year }
  });
};

/**
 * 获取报销模板
 * @param {Number} id - 出访记录ID
 * @returns {Promise}
 */
export const getReimbursementTemplate = (id) => {
  return api.get(`/visits/${id}/reimbursement-template`, {
    responseType: 'blob'
  });
};

/**
 * 修改用户密码
 * @param {Object} data - 包含用户名和新旧密码
 * @returns {Promise}
 */
export const updatePassword = (data) => {
  return api.put('/user/password', data);
};

/**
 * 更新用户基本信息（邮箱、手机号）
 * @param {Object} data - 用户信息
 * @returns {Promise}
 */
export const updateUserInfo = (data) => {
  return api.put('/user/info', data);
};

/**
 * 更新教师个人资料
 * @param {Object} profileData - 教师个人资料
 * @returns {Promise}
 */
export const updateTeacherProfile = (profileData) => {
  return api.put('/teacher/profile/update', profileData);
};

/**
 * 添加教师教育背景
 * @param {Number} teacherId - 教师ID
 * @param {Object} educationData - 教育背景信息
 * @returns {Promise}
 */
export const addTeacherEducation = (teacherId, educationData) => {
  return api.post(`/teacher/profile/${teacherId}/education`, educationData);
};

/**
 * 更新教师教育背景
 * @param {Number} educationId - 教育背景ID
 * @param {Object} educationData - 教育背景信息
 * @returns {Promise}
 */
export const updateTeacherEducation = (educationId, educationData) => {
  return api.put(`/teacher/profile/education/${educationId}`, educationData);
};

/**
 * 删除教师教育背景
 * @param {Number} educationId - 教育背景ID
 * @returns {Promise}
 */
export const deleteTeacherEducation = (educationId) => {
  return api.delete(`/teacher/profile/education/${educationId}`);
};

/**
 * 上传教师头像
 * @param {FormData} formData - 包含头像文件和教师ID的表单数据
 * @returns {Promise}
 */
export const uploadAvatar = (formData) => {
  return api.post('/teacher/profile/upload-avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

/**
 * 更新教师头像URL（前端直接上传到静态资源服务器后调用）
 * @param {Object} data - 包含teacherId和fileUrl的对象
 * @returns {Promise}
 */
export const updateAvatarUrl = (data) => {
  return api.post('/teacher/profile/update-avatar-url', data);
};

/**
 * 获取教师的科研成果摘要
 * @param {Number} teacherId - 教师ID
 * @returns {Promise}
 */
export const getResearchSummary = (teacherId) => {
  return api.get(`/teacher/research-summary/generate/${teacherId}`);
};

// 导出api实例
export default api; 