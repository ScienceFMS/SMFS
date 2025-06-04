package hit.cs.sciencefms.dto;

import lombok.Data;

/**
 * 上传头像请求类
 * 用于前端直接上传文件到静态资源服务器后，通知后端更新数据库中的URL
 */
@Data
public class UploadAvatarRequest {
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 文件名
     */
    private String fileName;
} 