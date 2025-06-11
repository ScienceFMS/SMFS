package hit.cs.sciencefms.dto;

import lombok.Data;

/**
 * 密码更新数据传输对象
 */
@Data
public class PasswordUpdateDTO {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 原密码
     */
    private String oldPassword;
    
    /**
     * 新密码
     */
    private String newPassword;
} 