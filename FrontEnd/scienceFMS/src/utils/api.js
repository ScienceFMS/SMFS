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

// 导出api实例
export default api; 