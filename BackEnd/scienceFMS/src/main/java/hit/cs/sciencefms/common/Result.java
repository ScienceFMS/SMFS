package hit.cs.sciencefms.common;

import lombok.Data;

/**
 * 通用响应结果封装类
 */
@Data
public class Result<T> {
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 成功
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        return result;
    }
    
    /**
     * 成功，带数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }
    
    /**
     * 失败
     */
    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAIL.getCode());
        result.setMessage(ResultCode.FAIL.getMessage());
        return result;
    }
    
    /**
     * 失败，带消息
     */
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAIL.getCode());
        result.setMessage(message);
        return result;
    }
    
    /**
     * 失败，带错误码和消息
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 未授权
     */
    public static <T> Result<T> unauthorized() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.UNAUTHORIZED.getCode());
        result.setMessage(ResultCode.UNAUTHORIZED.getMessage());
        return result;
    }
    
    /**
     * 账号锁定
     */
    public static <T> Result<T> accountLocked() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.ACCOUNT_LOCKED.getCode());
        result.setMessage(ResultCode.ACCOUNT_LOCKED.getMessage());
        return result;
    }
} 