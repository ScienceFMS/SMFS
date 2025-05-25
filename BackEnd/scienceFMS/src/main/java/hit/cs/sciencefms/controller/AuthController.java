package hit.cs.sciencefms.controller;

import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.LoginRequest;
import hit.cs.sciencefms.dto.LoginResponse;
import hit.cs.sciencefms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin // 允许跨域请求
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }
    
    /**
     * 检查登录状态
     *
     * @return 状态结果
     */
    @GetMapping("/checkLogin")
    public Result<String> checkLogin() {
        // 这个接口会被拦截器/过滤器处理，如果能访问到就说明已登录
        return Result.success("已登录");
    }
} 