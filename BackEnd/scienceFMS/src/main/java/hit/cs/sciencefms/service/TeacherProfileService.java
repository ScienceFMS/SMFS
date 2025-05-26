package hit.cs.sciencefms.service;

import hit.cs.sciencefms.dto.TeacherProfileDTO;

/**
 * 教师个人资料服务接口
 */
public interface TeacherProfileService {
    
    /**
     * 根据用户名获取教师个人资料（包含教育背景）
     * @param username 用户名
     * @return 教师个人资料DTO
     */
    TeacherProfileDTO getTeacherProfileByUsername(String username);
    
    /**
     * 根据教师ID获取教师个人资料（包含教育背景）
     * @param teacherId 教师ID
     * @return 教师个人资料DTO
     */
    TeacherProfileDTO getTeacherProfileById(Long teacherId);
    
} 