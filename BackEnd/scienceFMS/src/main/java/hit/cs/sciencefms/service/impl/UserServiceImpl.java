package hit.cs.sciencefms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.LoginRequest;
import hit.cs.sciencefms.dto.LoginResponse;
import hit.cs.sciencefms.entity.User;
import hit.cs.sciencefms.mapper.UserMapper;
import hit.cs.sciencefms.service.UserService;
import hit.cs.sciencefms.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import hit.cs.sciencefms.mapper.TeacherMapper;
import hit.cs.sciencefms.entity.Teacher;
import hit.cs.sciencefms.dto.PasswordUpdateDTO;
import hit.cs.sciencefms.dto.UserInfoUpdateDTO;
import java.time.LocalDateTime;

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
    private TeacherMapper teacherMapper;
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

        log.info("JWT token: {}", token);
        
        // 构建登录响应
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(user.getId());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setRole(user.getRole());
        loginResponse.setRealName(user.getRealName());
        loginResponse.setToken(token);

        // 根据角色查询特定ID
        if ("teacher".equals(user.getRole())) {
            // 查询教师表
            Teacher teacher = teacherMapper.selectOne(
                new LambdaQueryWrapper<Teacher>()
                    .eq(Teacher::getUsername, user.getUsername())
            );
            if (teacher != null) {
                loginResponse.setTeacherId(teacher.getId());
            }
        } else if ("admin".equals(user.getRole())) {
            // // 查询管理员表,等之后要用了再扩充，这个其实不需要
            // Admin admin = adminMapper.selectOne(
            //     new LambdaQueryWrapper<Admin>()
            //         .eq(Admin::getUsername, user.getUsername())
            // );
            // if (admin != null) {
            //     loginResponse.setAdminId(admin.getId());
            // }
        }
        
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
        // 用户不存在，直接返回错误信息
        if (user == null) {
            return Result.fail("用户名或密码错误");
        }
        
        // 增加登录失败计数
        userMapper.incrementLoginFailCount(username);
        
        // 检查是否达到锁定阈值
        user = findByUsername(username); // 重新查询获取最新的失败次数
        if (user.getLoginFailCount() >= MAX_LOGIN_FAIL_COUNT) {
            // 锁定账号
            userMapper.lockUserAccount(username);
            log.warn("用户 {} 登录失败次数过多，账号已被锁定", username);
            return Result.accountLocked();
        }
        
        return Result.fail("用户名或密码错误");
    }
    
    @Override
    public Result<Boolean> updatePassword(PasswordUpdateDTO dto) {
        // 参数校验
        if (dto.getUsername() == null || dto.getOldPassword() == null || dto.getNewPassword() == null) {
            return Result.fail("参数不完整");
        }
        
        // 查询用户
        User user = findByUsername(dto.getUsername());
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        // 校验原密码
        if (!user.getPassword().equals(dto.getOldPassword())) {
            return Result.fail("原密码错误");
        }
        
        // 更新密码
        user.setPassword(dto.getNewPassword());
        user.setUpdateTime(LocalDateTime.now());
        
        int result = userMapper.updateById(user);
        return result > 0 ? Result.success(true) : Result.fail("修改密码失败");
    }
    
    @Override
    public Result<Boolean> updateUserInfo(UserInfoUpdateDTO dto) {
        // 参数校验
        if (dto.getUsername() == null) {
            return Result.fail("用户名不能为空");
        }
        
        // 查询用户
        User user = findByUsername(dto.getUsername());
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        // 更新用户信息
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setUpdateTime(LocalDateTime.now());
        
        // 更新t_user表
        int userResult = userMapper.updateById(user);
        
        // 如果是教师角色，同步更新t_teacher表
        if ("teacher".equals(user.getRole())) {
            Teacher teacher = teacherMapper.selectOne(
                new LambdaQueryWrapper<Teacher>()
                    .eq(Teacher::getUsername, user.getUsername())
            );
            
            if (teacher != null) {
                teacher.setEmail(dto.getEmail());
                teacher.setPhone(dto.getPhone());
                teacher.setUpdateTime(LocalDateTime.now());
                teacherMapper.updateById(teacher);
            }
        }
        
        return userResult > 0 ? Result.success(true) : Result.fail("更新用户信息失败");
    }
} 