package hit.cs.sciencefms.service;

import hit.cs.sciencefms.dto.TeacherProfileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    
    /**
     * 更新教师基本信息
     * @param profileDTO 教师个人资料DTO
     * @return 更新后的教师个人资料DTO
     */
    TeacherProfileDTO updateTeacherProfile(TeacherProfileDTO profileDTO);
    
    /**
     * 添加教师教育背景
     * @param teacherId 教师ID
     * @param educationDTO 教育背景DTO
     * @return 添加的教育背景DTO（包含ID）
     */
    TeacherProfileDTO.EducationDTO addTeacherEducation(Long teacherId, TeacherProfileDTO.EducationDTO educationDTO);
    
    /**
     * 更新教师教育背景
     * @param educationDTO 教育背景DTO
     * @return 更新后的教育背景DTO
     */
    TeacherProfileDTO.EducationDTO updateTeacherEducation(TeacherProfileDTO.EducationDTO educationDTO);
    
    /**
     * 删除教师教育背景
     * @param educationId 教育背景ID
     * @return 是否删除成功
     */
    boolean deleteTeacherEducation(Long educationId);
    
    /**
     * 上传教师头像
     * @param teacherId 教师ID
     * @param file 头像文件
     * @return 头像URL
     * @throws IOException 如果文件处理出错
     */
    String uploadAvatar(Long teacherId, MultipartFile file) throws IOException;
    
    /**
     * 更新教师头像URL（前端直接上传到静态资源服务器后调用）
     * @param teacherId 教师ID
     * @param avatarUrl 头像URL
     * @return 更新后的头像URL
     * @throws IOException 如果处理出错
     */
    String updateAvatarUrl(Long teacherId, String avatarUrl) throws IOException;
} 