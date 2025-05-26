package hit.cs.sciencefms.dto;

import lombok.Data;

/**
 * 登录响应数据传输对象
 */
@Data
public class LoginResponse {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 角色：teacher-教师，admin-管理员
     */
    private String role;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * JWT令牌
     */
    private String token;

    // 新增字段
    private Long teacherId;
    private Long adminId;
} 