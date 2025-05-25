package hit.cs.sciencefms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.common.ResultCode;
import hit.cs.sciencefms.dto.LoginRequest;
import hit.cs.sciencefms.dto.LoginResponse;
import hit.cs.sciencefms.entity.User;
import hit.cs.sciencefms.mapper.UserMapper;
import hit.cs.sciencefms.service.UserService;
import hit.cs.sciencefms.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    /**
     * 登录失败锁定阈值
     */
    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public Result<LoginResponse> login(LoginRequest loginRequest) {
        log.info("用户登录: {}", loginRequest.getUsername());
        
        // 参数校验
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return Result.fail("用户名和密码不能为空");
        }
        
        // 查询用户
        User user = findByUsername(loginRequest.getUsername());
        
        // 用户不存在
        if (user == null) {
            return handleLoginFailure(loginRequest.getUsername(), null);
        }
        
        // 检查用户是否被锁定
        if (isAccountLocked(user)) {
            return Result.accountLocked();
        }
        
        // 检查角色是否匹配
        if (!user.getRole().equals(loginRequest.getRole())) {
            return handleLoginFailure(loginRequest.getUsername(), user);
        }
        
        // 校验密码
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return handleLoginFailure(loginRequest.getUsername(), user);
        }
        
        // 登录成功，重置登录失败次数
        userMapper.resetLoginFailCount(user.getUsername());
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构建登录响应
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(user.getId());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setRole(user.getRole());
        loginResponse.setRealName(user.getRealName());
        loginResponse.setToken(token);
        
        return Result.success(loginResponse);
    }
    
    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean isAccountLocked(User user) {
        return user != null && user.getStatus() != null && user.getStatus() == 1;
    }
    
    @Override
    public Result<LoginResponse> handleLoginFailure(String username, User user) {
        if (user != null) {
            // 增加登录失败次数
            userMapper.incrementLoginFailCount(username);
            
            // 重新查询用户获取最新失败次数
            user = findByUsername(username);
            
            // 检查是否需要锁定账号
            if (user.getLoginFailCount() >= MAX_LOGIN_FAIL_COUNT) {
                userMapper.lockUserAccount(username);
                log.warn("用户账号已锁定: {}", username);
                return Result.accountLocked();
            }
        }
        
        return Result.fail(ResultCode.ACCOUNT_PWD_ERROR.getCode(), 
                ResultCode.ACCOUNT_PWD_ERROR.getMessage());
    }
} 