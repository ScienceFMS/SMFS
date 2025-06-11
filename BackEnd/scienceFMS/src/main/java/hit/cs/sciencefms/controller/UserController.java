package hit.cs.sciencefms.controller;

import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.PasswordUpdateDTO;
import hit.cs.sciencefms.dto.UserInfoUpdateDTO;
import hit.cs.sciencefms.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 修改密码
     * @param dto 密码修改DTO
     * @return 操作结果
     */
    @PutMapping("/password")
    public Result<Boolean> updatePassword(@RequestBody PasswordUpdateDTO dto) {
        log.info("修改密码: {}", dto.getUsername());
        return userService.updatePassword(dto);
    }
    
    /**
     * 更新用户基本信息（邮箱、手机号）
     * @param dto 用户信息DTO
     * @return 操作结果
     */
    @PutMapping("/info")
    public Result<Boolean> updateUserInfo(@RequestBody UserInfoUpdateDTO dto) {
        log.info("更新用户信息: {}", dto.getUsername());
        return userService.updateUserInfo(dto);
    }
} 