package hit.cs.sciencefms.service;

import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.LoginRequest;
import hit.cs.sciencefms.dto.LoginResponse;
import hit.cs.sciencefms.dto.PasswordUpdateDTO;
import hit.cs.sciencefms.dto.UserInfoUpdateDTO;
import hit.cs.sciencefms.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    Result<LoginResponse> login(LoginRequest loginRequest);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);
    
    /**
     * 检查用户是否被锁定
     * @param user 用户对象
     * @return 是否锁定
     */
    boolean isAccountLocked(User user);
    
    /**
     * 处理登录失败
     * @param username 用户名
     * @param user 用户对象（可能为null）
     * @return 结果消息
     */
    Result<LoginResponse> handleLoginFailure(String username, User user);
    
    /**
     * 修改用户密码
     * @param dto 密码修改DTO
     * @return 操作结果
     */
    Result<Boolean> updatePassword(PasswordUpdateDTO dto);
    
    /**
     * 更新用户基本信息
     * @param dto 用户信息DTO
     * @return 操作结果
     */
    Result<Boolean> updateUserInfo(UserInfoUpdateDTO dto);
} 