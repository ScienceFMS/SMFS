# 静态资源服务器 API 文档

## 基本信息

- **基础URL**: `http://120.46.48.74:9000`
- **最大上传文件大小**: 100MB

## 1. 获取文件

### 请求

```
GET /{文件名}
```

### 参数

| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| 文件名 | string | 是 | 要获取的文件名，包括扩展名 |

### 示例

```
GET http://120.46.48.74:9000/example.jpg
```

### 响应

- **成功**: 返回文件内容，状态码 200
- **失败**: 返回 404 Not Found（文件不存在）

## 2. 上传文件

### 请求

```
POST /upload-delete/upload
```

### 请求头

```
Content-Type: multipart/form-data
```

### 请求体

使用 `multipart/form-data` 格式，包含文件数据。

### 示例

使用 cURL:
```bash
curl -F "file=@/path/to/local/file.jpg" http://120.46.48.74:9000/upload-delete/upload
```

使用 HTML 表单:
```html
<form action="http://120.46.48.74:9000/upload-delete/upload" method="post" enctype="multipart/form-data">
  <input type="file" name="file">
  <button type="submit">上传</button>
</form>
```

使用 JavaScript:
```javascript
const formData = new FormData();
formData.append('file', fileObject); // fileObject 是 File 对象

fetch('http://120.46.48.74:9000/upload-delete/upload', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => console.log(data));
```

### 响应

成功:
```json
{
  "success": true,
  "message": "文件上传成功",
  "fileName": "example.jpg",
  "url": "/example.jpg"
}
```

失败:
```json
{
  "success": false,
  "message": "错误信息",
  "error": "详细错误信息"
}
```

可能的错误信息:
- "方法不允许，请使用POST"
- "请使用multipart/form-data格式上传文件"
- "无法解析表单边界"
- "无法解析表单数据：未找到边界"
- "未找到文件名"
- "保存文件失败"

## 3. 删除文件

### 请求

```
GET /upload-delete/delete/{文件名}
```

### 参数

| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| 文件名 | string | 是 | 要删除的文件名，包括扩展名 |

### 示例

```
GET http://120.46.48.74:9000/upload-delete/delete/example.jpg
```

使用 cURL:
```bash
curl http://120.46.48.74:9000/upload-delete/delete/example.jpg
```

使用 JavaScript:
```javascript
fetch('http://120.46.48.74:9000/upload-delete/delete/example.jpg')
  .then(response => response.json())
  .then(data => console.log(data));
```

### 响应

成功:
```json
{
  "success": true,
  "message": "文件 example.jpg 已成功删除"
}
```

失败:
```json
{
  "success": false,
  "message": "错误信息"
}
```

可能的错误信息:
- "未指定文件名"
- "非法的文件路径"
- "文件不存在"
- "删除文件失败"

## 注意事项

1. 所有文件都存储在服务器的 `/var/www/data/` 目录中
2. 当前配置允许所有 IP 地址访问服务
3. 服务支持所有文件类型，包括图片、文档、视频等
4. 上传的文件名会保持原样，如果存在同名文件会被覆盖
5. 目前没有身份验证机制，生产环境中应添加适当的身份验证

## 服务管理

启动服务:
```bash
cd /var/www/delete-service
./start-delete-service.sh
```

停止服务:
```bash
cd /var/www/delete-service
./stop-delete-service.sh
```
