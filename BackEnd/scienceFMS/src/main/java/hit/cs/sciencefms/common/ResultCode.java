package hit.cs.sciencefms.common;

import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
public enum ResultCode {
    
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 失败
     */
    FAIL(400, "操作失败"),
    
    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权或授权已过期"),
    
    /**
     * 账号或密码错误
     */
    ACCOUNT_PWD_ERROR(1001, "账号或密码错误"),
    
    /**
     * 账号锁定
     */
    ACCOUNT_LOCKED(1002, "账号已锁定，请联系管理员");
    
    private final Integer code;
    
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
} 