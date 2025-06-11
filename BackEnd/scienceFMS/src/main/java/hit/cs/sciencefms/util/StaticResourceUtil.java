package hit.cs.sciencefms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * 静态资源工具类，用于与静态资源服务器交互
 */
@Component
public class StaticResourceUtil {
    
    private static final Logger log = LoggerFactory.getLogger(StaticResourceUtil.class);
    
    // base-url: http://120.46.48.74:9000/1.jpg  获取
    @Value("${file.upload.static-server.base-url}")
    private String staticServerBaseUrl;
    
    // 上传文件：http://120.46.48.74:9000/upload-delete/upload
    @Value("${file.upload.static-server.upload-url}")
    private String staticServerUploadUrl;
    
    // 删除文件：http://120.46.48.74:9000/upload-delete/delete/1.jpg
    @Value("${file.upload.static-server.delete-url}")
    private String staticServerDeleteUrl;
    
    private final RestTemplate restTemplate;
    
    public StaticResourceUtil() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * 上传文件到静态资源服务器
     * @param file 要上传的文件
     * @param fileType 文件类型（用于生成前缀，如avatar、document等）
     * @return 上传成功后的访问URL
     * @throws IOException 如果上传失败
     */
    public String uploadFile(MultipartFile file, String fileType) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("文件为空");
        }
        
        try {
            // 准备请求头
            HttpHeaders headers = new HttpHeaders();
            // 注意：虽然我们设置了boundary参数，但Spring RestTemplate会在内部生成自己的boundary
            // 并替换我们设置的值。这里设置主要是告诉RestTemplate这是一个multipart请求
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // 打印请求头，查看Spring实际生成的boundary
            log.info("设置请求头前: {}", headers);
            
            // 准备请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            
            // 添加文件，确保文件名唯一
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            // 生成时间戳前缀，格式：yyyyMMddHHmmss
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            
            // 生成唯一文件名：fileType_时间戳_UUID前8位.扩展名
            String uniqueFilename = fileType + "_" + timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;
            
            // 创建包含原始文件内容的资源
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return uniqueFilename;
                }
            };
            
            body.add("file", fileResource);
            
            // 创建请求实体
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // 打印最终的请求头信息
            log.info("发送请求前的最终请求头: {}", requestEntity.getHeaders());
            log.info("请求URL: {}", staticServerUploadUrl);
            log.info("请求体内容类型: {}", body.keySet());
            
            // 发送请求
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    staticServerUploadUrl,
                    requestEntity,
                    Map.class
            );
            
            // 处理响应
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && Boolean.TRUE.equals(responseBody.get("success"))) {
                String fileName = (String) responseBody.get("fileName");
                // 构建完整URL
                String fileUrl = staticServerBaseUrl + "/" + fileName;
                log.info("文件上传成功: {}", fileUrl);
                return fileUrl;
            } else {
                String errorMessage = responseBody != null ? (String) responseBody.get("message") : "未知错误";
                log.error("文件上传失败: {}", errorMessage);
                throw new IOException("文件上传失败: " + errorMessage);
            }
        } catch (Exception e) {
            log.error("文件上传过程中发生错误", e);
            throw new IOException("文件上传失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从静态资源服务器删除文件
     * @param fileUrl 文件的完整URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            log.warn("尝试删除空URL");
            return false;
        }
        
        try {
            // 从URL中提取文件名
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            
            // 构建删除URL
            String deleteUrl = staticServerDeleteUrl + "/" + fileName;
            
            // 发送删除请求
            ResponseEntity<Map> response = restTemplate.getForEntity(deleteUrl, Map.class);
            
            // 处理响应
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && Boolean.TRUE.equals(responseBody.get("success"))) {
                log.info("文件删除成功: {}", fileName);
                return true;
            } else {
                String errorMessage = responseBody != null ? (String) responseBody.get("message") : "未知错误";
                log.error("文件删除失败: {}", errorMessage);
                return false;
            }
        } catch (Exception e) {
            log.error("文件删除过程中发生错误", e);
            return false;
        }
    }
} 