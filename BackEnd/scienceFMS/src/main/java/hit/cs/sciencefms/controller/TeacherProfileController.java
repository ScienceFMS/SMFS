package hit.cs.sciencefms.controller;

import hit.cs.sciencefms.dto.TeacherProfileDTO;
import hit.cs.sciencefms.service.TeacherProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 教师个人资料控制器
 */
@RestController
@RequestMapping("/teacher/profile")
@RequiredArgsConstructor
public class TeacherProfileController {
    
    private final TeacherProfileService teacherProfileService;
    
    /**
     * 获取当前登录教师的个人资料
     * @param username 用户名
     * @return 教师个人资料
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@RequestParam(required = false) String username, 
                                        @RequestParam(required = false) Long userId) {
        TeacherProfileDTO profile = null;
        
        // 优先使用用户名查询
        if (username != null && !username.isEmpty()) {
            profile = teacherProfileService.getTeacherProfileByUsername(username);
        } 
        // 其次使用用户ID查询
        else if (userId != null) {
            profile = teacherProfileService.getTeacherProfileById(userId);
        } 
        // 如果都没提供，返回错误
        else {
            return ResponseEntity.badRequest().body("必须提供用户名或用户ID");
        }
        
        // 查询结果处理
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", profile);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据ID获取教师个人资料
     * @param teacherId 教师ID
     * @return 教师个人资料
     */
    @GetMapping("/{teacherId}")
    public ResponseEntity<?> getTeacherProfile(@PathVariable Long teacherId) {
        TeacherProfileDTO profile = teacherProfileService.getTeacherProfileById(teacherId);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", profile);
        return ResponseEntity.ok(result);
    }
} 