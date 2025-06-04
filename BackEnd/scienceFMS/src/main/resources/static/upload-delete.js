const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

// 配置
const config = {
  port: 3000,
  baseDir: '/var/www/data', // 静态资源的基础目录
  allowedIPs: ['*'] // 允许所有IP访问
};

// 创建HTTP服务器
const server = http.createServer((req, res) => {
  // 设置CORS头，允许跨域请求
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  
  // 处理OPTIONS请求（预检请求）
  if (req.method === 'OPTIONS') {
    res.writeHead(200);
    res.end();
    return;
  }
  
  // 解析URL
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;
  
  // 检查IP白名单（如果不是*）
  if (config.allowedIPs[0] !== '*') {
    const clientIP = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
    if (!config.allowedIPs.includes(clientIP)) {
      res.writeHead(403, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: false, message: '没有权限访问此服务' }));
      console.log(`拒绝来自 ${clientIP} 的访问`);
      return;
    }
  }
  
  // 处理删除请求
  if (pathname.startsWith('/delete/')) {
    handleDeleteRequest(req, res, pathname);
  } 
  // 处理上传请求
  else if (pathname === '/upload') {
    handleUploadRequest(req, res);
  } 
  // 其他路径
  else {
    res.writeHead(404, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: false, message: '路径不存在' }));
  }
});

// 处理删除文件请求
function handleDeleteRequest(req, res, pathname) {
  // 获取文件名
  const filename = pathname.substring(8); // 移除 '/delete/' 前缀
  
  if (!filename) {
    res.writeHead(400, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: false, message: '未指定文件名' }));
    return;
  }
  
  // 构建完整的文件路径，并确保它在基础目录内
  const filePath = path.join(config.baseDir, filename);
  
  // 安全检查：确保文件路径在基础目录内
  if (!filePath.startsWith(config.baseDir)) {
    res.writeHead(403, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: false, message: '非法的文件路径' }));
    return;
  }
  
  // 检查文件是否存在
  fs.access(filePath, fs.constants.F_OK, (err) => {
    if (err) {
      res.writeHead(404, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: false, message: '文件不存在' }));
      return;
    }
    
    // 删除文件
    fs.unlink(filePath, (err) => {
      if (err) {
        res.writeHead(500, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: false, message: '删除文件失败', error: err.message }));
        return;
      }
      
      // 成功删除
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, message: `文件 ${filename} 已成功删除` }));
      console.log(`文件 ${filename} 已被删除`);
    });
  });
}

// 处理上传文件请求
function handleUploadRequest(req, res) {
  if (req.method !== 'POST') {
    res.writeHead(405, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: false, message: '方法不允许，请使用POST' }));
    return;
  }
  
  // 获取Content-Type
  const contentType = req.headers['content-type'] || '';
  
  // 检查是否是multipart/form-data
  if (!contentType.startsWith('multipart/form-data')) {
    res.writeHead(400, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: false, message: '请使用multipart/form-data格式上传文件' }));
    return;
  }
  
  // 解析边界
  const boundary = contentType.split('; boundary=')[1];
  if (!boundary) {
    res.writeHead(400, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: false, message: '无法解析表单边界' }));
    return;
  }
  
  // 存储数据
  let data = [];
  
  req.on('data', chunk => {
    data.push(chunk);
  });
  
  req.on('end', () => {
    try {
      // 将数据合并为一个Buffer
      const buffer = Buffer.concat(data);
      
      // 边界字符串
      const boundaryBuffer = Buffer.from(`--${boundary}`, 'utf8');
      const endBoundaryBuffer = Buffer.from(`--${boundary}--`, 'utf8');
      const crlfBuffer = Buffer.from('\r\n', 'utf8');
      const doubleCrlfBuffer = Buffer.from('\r\n\r\n', 'utf8');
      
      let fileName = '';
      let fileData = null;
      let position = 0;
      
      // 查找第一个边界
      position = buffer.indexOf(boundaryBuffer, position);
      if (position === -1) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: false, message: '无法解析表单数据：未找到边界' }));
        return;
      }
      
      // 移动到边界后
      position += boundaryBuffer.length;
      
      // 查找下一个CRLF
      position = buffer.indexOf(crlfBuffer, position);
      if (position === -1) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: false, message: '无法解析表单数据：格式错误' }));
        return;
      }
      
      // 移动到CRLF后
      position += crlfBuffer.length;
      
      // 查找Content-Disposition头
      const headerStart = position;
      const headerEnd = buffer.indexOf(doubleCrlfBuffer, position);
      if (headerEnd === -1) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: false, message: '无法解析表单数据：未找到头部结束' }));
        return;
      }
      
      // 提取头部信息
      const headerData = buffer.slice(headerStart, headerEnd).toString('utf8');
      const filenameMatch = headerData.match(/filename="([^"]+)"/);
      if (!filenameMatch) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: false, message: '未找到文件名' }));
        return;
      }
      
      fileName = filenameMatch[1];
      
      // 移动到头部后
      position = headerEnd + doubleCrlfBuffer.length;
      
      // 查找下一个边界
      const nextBoundaryPos = buffer.indexOf(boundaryBuffer, position);
      if (nextBoundaryPos === -1) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: false, message: '无法解析表单数据：未找到结束边界' }));
        return;
      }
      
      // 提取文件内容（减去末尾的\r\n）
      fileData = buffer.slice(position, nextBoundaryPos - 2);
      
      // 确保目录存在
      if (!fs.existsSync(config.baseDir)) {
        fs.mkdirSync(config.baseDir, { recursive: true });
      }
      
      // 写入文件
      const filePath = path.join(config.baseDir, fileName);
      fs.writeFile(filePath, fileData, (err) => {
        if (err) {
          res.writeHead(500, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: false, message: '保存文件失败', error: err.message }));
          return;
        }
        
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ 
          success: true, 
          message: '文件上传成功',
          fileName: fileName,
          url: `/${fileName}`
        }));
        console.log(`文件 ${fileName} 已上传，大小: ${fileData.length} 字节`);
      });
    } catch (err) {
      res.writeHead(500, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: false, message: '处理上传请求失败', error: err.message }));
      console.error('上传处理错误:', err);
    }
  });
}

// 启动服务器
server.listen(config.port, () => {
  console.log(`文件上传/删除服务运行在 http://localhost:${config.port}`);
  console.log(`删除文件: http://localhost:${config.port}/delete/文件名`);
  console.log(`上传文件: http://localhost:${config.port}/upload`);
});