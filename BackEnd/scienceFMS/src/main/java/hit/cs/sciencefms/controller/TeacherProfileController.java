package hit.cs.sciencefms.controller;

import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.TeacherProfileDTO;
import hit.cs.sciencefms.dto.UploadAvatarRequest;
import hit.cs.sciencefms.service.TeacherProfileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger log = LoggerFactory.getLogger(TeacherProfileController.class);
    
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
    
    /**
     * 更新教师基本信息
     * @param profileDTO 教师个人资料DTO
     * @return 更新后的教师个人资料
     * @apiNote 如果更新了realName、email或phone字段，会同步更新User表中的对应数据
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateTeacherProfile(@RequestBody TeacherProfileDTO profileDTO) {
        TeacherProfileDTO updatedProfile = teacherProfileService.updateTeacherProfile(profileDTO);
        if (updatedProfile == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "教师信息更新成功");
        result.put("data", updatedProfile);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 添加教师教育背景
     * @param teacherId 教师ID
     * @param educationDTO 教育背景DTO
     * @return 添加的教育背景
     */
    @PostMapping("/{teacherId}/education")
    public ResponseEntity<?> addTeacherEducation(
            @PathVariable Long teacherId,
            @RequestBody TeacherProfileDTO.EducationDTO educationDTO) {
        
        TeacherProfileDTO.EducationDTO newEducation = 
                teacherProfileService.addTeacherEducation(teacherId, educationDTO);
        
        if (newEducation == null) {
            return ResponseEntity.badRequest().body("添加教育背景失败");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "教育背景添加成功");
        result.put("data", newEducation);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新教师教育背景
     * @param educationId 教育背景ID
     * @param educationDTO 教育背景DTO
     * @return 更新后的教育背景
     */
    @PutMapping("/education/{educationId}")
    public ResponseEntity<?> updateTeacherEducation(
            @PathVariable Long educationId,
            @RequestBody TeacherProfileDTO.EducationDTO educationDTO) {
        
        // 确保ID一致
        educationDTO.setId(educationId);
        
        TeacherProfileDTO.EducationDTO updatedEducation = 
                teacherProfileService.updateTeacherEducation(educationDTO);
        
        if (updatedEducation == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "教育背景更新成功");
        result.put("data", updatedEducation);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除教师教育背景
     * @param educationId 教育背景ID
     * @return 操作结果
     */
    @DeleteMapping("/education/{educationId}")
    public ResponseEntity<?> deleteTeacherEducation(@PathVariable Long educationId) {
        boolean success = teacherProfileService.deleteTeacherEducation(educationId);
        
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("success", true);
            result.put("message", "教育背景删除成功");
        } else {
            result.put("success", false);
            result.put("message", "教育背景删除失败");
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新教师头像URL（新方式，前端直接上传到静态资源服务器后调用此接口）
     */
    @PostMapping("/update-avatar-url")
    public Result<Map<String, String>> updateAvatarUrl(@RequestBody UploadAvatarRequest request) {
        try {
            String avatarUrl = teacherProfileService.updateAvatarUrl(request.getTeacherId(), request.getFileUrl());
            Map<String, String> result = new HashMap<>();
            result.put("url", avatarUrl);
            return Result.success(result);
        } catch (Exception e) {
            log.error("头像URL更新失败", e);
            return Result.fail("头像URL更新失败: " + e.getMessage());
        }
    }
} 