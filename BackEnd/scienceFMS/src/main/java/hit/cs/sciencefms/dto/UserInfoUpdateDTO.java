package hit.cs.sciencefms.dto;

import lombok.Data;

/**
 * 用户信息更新数据传输对象
 */
@Data
public class UserInfoUpdateDTO {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
} 