package hit.cs.sciencefms.dto;

import lombok.Data;

/**
 * 登录请求数据传输对象
 */
@Data
public class LoginRequest {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 角色：teacher-教师，admin-管理员
     */
    private String role;
} 