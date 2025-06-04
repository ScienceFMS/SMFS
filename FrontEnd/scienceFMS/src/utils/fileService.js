/**
 * 文件服务工具
 * 用于直接与静态资源服务器通信，处理文件上传和删除
 */
import { v4 as uuidv4 } from 'uuid';
import api from './api';

// 静态资源服务器配置
const STATIC_SERVER = {
  BASE_URL: 'http://120.46.48.74:9000',
  UPLOAD_URL: 'http://120.46.48.74:9000/upload-delete/upload',
  DELETE_URL: 'http://120.46.48.74:9000/upload-delete/delete'
};

/**
 * 生成唯一文件名
 * @param {File} file - 原始文件
 * @param {String} prefix - 文件前缀，如avatar, document等
 * @returns {String} 唯一文件名
 */
const generateUniqueFilename = (file, prefix = '') => {
  // 获取原始文件扩展名
  const originalName = file.name;
  const extension = originalName.substring(originalName.lastIndexOf('.'));
  
  // 生成时间戳，格式：yyyyMMddHHmmss
  const now = new Date();
  const timestamp = now.getFullYear() +
    ('0' + (now.getMonth() + 1)).slice(-2) +
    ('0' + now.getDate()).slice(-2) +
    ('0' + now.getHours()).slice(-2) +
    ('0' + now.getMinutes()).slice(-2) +
    ('0' + now.getSeconds()).slice(-2);
  
  // 生成UUID的前8位
  const shortUuid = uuidv4().split('-')[0];
  
  // 组合：前缀_时间戳_UUID前8位.扩展名
  return `${prefix}_${timestamp}_${shortUuid}${extension}`;
};

/**
 * 上传文件到静态资源服务器
 * @param {File} file - 要上传的文件
 * @param {String} fileType - 文件类型前缀，如avatar, document等
 * @returns {Promise<Object>} 包含文件URL的对象
 */
export const uploadFile = async (file, fileType = 'file') => {
  if (!file) {
    throw new Error('文件不能为空');
  }
  
  // 创建唯一文件名
  const uniqueFilename = generateUniqueFilename(file, fileType);
  
  // 创建FormData对象
  const formData = new FormData();
  
  // 使用新文件名创建File对象
  const renamedFile = new File([file], uniqueFilename, { type: file.type });
  formData.append('file', renamedFile);
  
  try {
    // 直接发送请求到静态资源服务器
    const response = await fetch(STATIC_SERVER.UPLOAD_URL, {
      method: 'POST',
      body: formData
    });
    
    if (!response.ok) {
      throw new Error(`上传失败: ${response.statusText}`);
    }
    
    const result = await response.json();
    
    if (!result.success) {
      throw new Error(result.message || '上传失败');
    }
    
    // 返回完整的文件URL
    const fileUrl = `${STATIC_SERVER.BASE_URL}/${result.fileName}`;
    return {
      success: true,
      url: fileUrl,
      fileName: result.fileName
    };
  } catch (error) {
    console.error('文件上传错误:', error);
    return {
      success: false,
      message: error.message || '文件上传失败'
    };
  }
};

/**
 * 从静态资源服务器删除文件
 * @param {String} fileUrl - 文件的完整URL
 * @returns {Promise<Object>} 操作结果
 */
export const deleteFile = async (fileUrl) => {
  if (!fileUrl) {
    return { success: false, message: '文件URL不能为空' };
  }
  
  try {
    // 从URL中提取文件名
    const fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    const deleteUrl = `${STATIC_SERVER.DELETE_URL}/${fileName}`;
    
    // 发送删除请求
    const response = await fetch(deleteUrl);
    
    if (!response.ok) {
      throw new Error(`删除失败: ${response.statusText}`);
    }
    
    const result = await response.json();
    
    return {
      success: result.success,
      message: result.message
    };
  } catch (error) {
    console.error('文件删除错误:', error);
    return {
      success: false,
      message: error.message || '文件删除失败'
    };
  }
};

/**
 * 上传文件并通知后端更新数据库
 * @param {File} file - 要上传的文件
 * @param {String} fileType - 文件类型前缀
 * @param {String} apiEndpoint - 后端API端点
 * @param {Object} additionalData - 要发送给后端的额外数据
 * @returns {Promise<Object>} 操作结果
 */
export const uploadAndUpdateDatabase = async (file, fileType, apiEndpoint, additionalData = {}) => {
  try {
    // 1. 先上传到静态资源服务器
    const uploadResult = await uploadFile(file, fileType);
    
    if (!uploadResult.success) {
      return uploadResult;
    }
    
    // 2. 通知后端更新数据库
    const updateData = {
      fileUrl: uploadResult.url,
      fileName: uploadResult.fileName,
      ...additionalData
    };
    
    const dbUpdateResult = await api.post(apiEndpoint, updateData);
    
    return {
      success: true,
      url: uploadResult.url,
      ...dbUpdateResult
    };
  } catch (error) {
    console.error('上传并更新数据库失败:', error);
    return {
      success: false,
      message: error.message || '操作失败'
    };
  }
};

export default {
  uploadFile,
  deleteFile,
  uploadAndUpdateDatabase
}; 