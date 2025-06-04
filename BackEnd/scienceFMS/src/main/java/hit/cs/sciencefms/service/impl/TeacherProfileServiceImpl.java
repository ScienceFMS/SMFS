package hit.cs.sciencefms.service.impl;

import hit.cs.sciencefms.dto.TeacherProfileDTO;
import hit.cs.sciencefms.entity.Teacher;
import hit.cs.sciencefms.entity.TeacherEducation;
import hit.cs.sciencefms.entity.User;
import hit.cs.sciencefms.mapper.TeacherEducationMapper;
import hit.cs.sciencefms.mapper.TeacherMapper;
import hit.cs.sciencefms.mapper.UserMapper;
import hit.cs.sciencefms.service.TeacherProfileService;
import hit.cs.sciencefms.util.StaticResourceUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师个人资料服务实现类
 */
@Service
@RequiredArgsConstructor
public class TeacherProfileServiceImpl implements TeacherProfileService {
    
    private static final Logger log = LoggerFactory.getLogger(TeacherProfileServiceImpl.class);
    
    private final TeacherMapper teacherMapper;
    private final TeacherEducationMapper teacherEducationMapper;
    private final UserMapper userMapper;
    
    @Autowired
    private StaticResourceUtil staticResourceUtil;
    
    @Override
    public TeacherProfileDTO getTeacherProfileByUsername(String username) {
        // 查询教师基本信息
        Teacher teacher = teacherMapper.selectByUsername(username);
        if (teacher == null) {
            return null;
        }
        return buildTeacherProfileDTO(teacher);
    }
    
    @Override
    public TeacherProfileDTO getTeacherProfileById(Long teacherId) {
        // 查询教师基本信息
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            return null;
        }
        return buildTeacherProfileDTO(teacher);
    }
    
    @Override
    @Transactional
    public TeacherProfileDTO updateTeacherProfile(TeacherProfileDTO profileDTO) {
        // 查询教师是否存在
        Teacher teacher = teacherMapper.selectById(profileDTO.getId());
        if (teacher == null) {
            return null;
        }
        
        // 更新教师基本信息
        teacher.setRealName(profileDTO.getRealName());
        teacher.setGender(profileDTO.getGender());
        teacher.setBirthDate(profileDTO.getBirthDate());
        teacher.setTitle(profileDTO.getTitle());
        teacher.setDepartment(profileDTO.getDepartment());
        teacher.setPosition(profileDTO.getPosition());
        teacher.setResearchArea(profileDTO.getResearchArea());
        teacher.setEmail(profileDTO.getEmail());
        teacher.setPhone(profileDTO.getPhone());
        teacher.setOfficeLocation(profileDTO.getOfficeLocation());
        teacher.setAvatarUrl(profileDTO.getAvatarUrl());
        teacher.setUpdateTime(LocalDateTime.now());
        
        // 更新数据库
        teacherMapper.updateById(teacher);
        
        // 同步更新User表
        User user = userMapper.selectById(teacher.getId());
        if (user != null) {
            user.setRealName(profileDTO.getRealName());
            user.setEmail(profileDTO.getEmail());
            user.setPhone(profileDTO.getPhone());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
        
        // 返回更新后的完整数据
        return buildTeacherProfileDTO(teacher);
    }
    
    @Override
    @Transactional
    public TeacherProfileDTO.EducationDTO addTeacherEducation(Long teacherId, TeacherProfileDTO.EducationDTO educationDTO) {
        // 检查教师是否存在
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            return null;
        }
        
        // 创建新的教育背景记录
        TeacherEducation education = new TeacherEducation();
        education.setTeacherId(teacherId);
        education.setDegree(educationDTO.getDegree());
        education.setInstitution(educationDTO.getInstitution());
        education.setMajor(educationDTO.getMajor());
        education.setStartYear(educationDTO.getStartYear());
        education.setEndYear(educationDTO.getEndYear());
        education.setCreateTime(LocalDateTime.now());
        education.setUpdateTime(LocalDateTime.now());
        education.setIsDeleted(0);
        
        // 插入数据库
        teacherEducationMapper.insert(education);
        
        // 返回新增的教育背景（带ID）
        return convertToEducationDTO(education);
    }
    
    @Override
    @Transactional
    public TeacherProfileDTO.EducationDTO updateTeacherEducation(TeacherProfileDTO.EducationDTO educationDTO) {
        // 查询教育背景是否存在
        TeacherEducation education = teacherEducationMapper.selectById(educationDTO.getId());
        if (education == null) {
            return null;
        }
        
        // 更新教育背景信息
        education.setDegree(educationDTO.getDegree());
        education.setInstitution(educationDTO.getInstitution());
        education.setMajor(educationDTO.getMajor());
        education.setStartYear(educationDTO.getStartYear());
        education.setEndYear(educationDTO.getEndYear());
        education.setUpdateTime(LocalDateTime.now());
        
        // 更新数据库
        teacherEducationMapper.updateById(education);
        
        // 返回更新后的教育背景
        return convertToEducationDTO(education);
    }
    
    @Override
    @Transactional
    public boolean deleteTeacherEducation(Long educationId) {
        // 查询教育背景是否存在
        TeacherEducation education = teacherEducationMapper.selectById(educationId);
        if (education == null) {
            return false;
        }
        
        // 逻辑删除
        return teacherEducationMapper.deleteById(educationId) > 0;
    }
    
    /**
     * 构建教师个人资料DTO
     * @param teacher 教师实体
     * @return 教师个人资料DTO
     */
    private TeacherProfileDTO buildTeacherProfileDTO(Teacher teacher) {
        TeacherProfileDTO dto = new TeacherProfileDTO();
        
        // 设置教师基本信息
        dto.setId(teacher.getId());
        dto.setUsername(teacher.getUsername());
        dto.setRealName(teacher.getRealName());
        dto.setGender(teacher.getGender());
        dto.setBirthDate(teacher.getBirthDate());
        dto.setTitle(teacher.getTitle());
        dto.setDepartment(teacher.getDepartment());
        dto.setPosition(teacher.getPosition());
        dto.setResearchArea(teacher.getResearchArea());
        dto.setEmail(teacher.getEmail());
        dto.setPhone(teacher.getPhone());
        dto.setOfficeLocation(teacher.getOfficeLocation());
        dto.setAvatarUrl(teacher.getAvatarUrl());
        
        // 查询教育背景信息
        List<TeacherEducation> educationList = teacherEducationMapper.selectByTeacherId(teacher.getId());
        if (educationList != null && !educationList.isEmpty()) {
            List<TeacherProfileDTO.EducationDTO> educationDTOList = educationList.stream()
                    .map(this::convertToEducationDTO)
                    .collect(Collectors.toList());
            dto.setEducationList(educationDTOList);
        } else {
            dto.setEducationList(new ArrayList<>());
        }
        
        return dto;
    }
    
    /**
     * 将教育背景实体转换为DTO
     * @param education 教育背景实体
     * @return 教育背景DTO
     */
    private TeacherProfileDTO.EducationDTO convertToEducationDTO(TeacherEducation education) {
        TeacherProfileDTO.EducationDTO dto = new TeacherProfileDTO.EducationDTO();
        dto.setId(education.getId());
        dto.setDegree(education.getDegree());
        dto.setInstitution(education.getInstitution());
        dto.setMajor(education.getMajor());
        dto.setStartYear(education.getStartYear());
        dto.setEndYear(education.getEndYear());
        return dto;
    }

    //想法是当用户上传一个图片时，后端给该图片生成唯一的id，然后上传到服务器上，并把访问服务器图片的url
    //保存到数据库中，然后前端在获取教师头像时，直接从数据库中获取url，然后访问服务器图片
    @Override
    @Transactional
    public String uploadAvatar(Long teacherId, MultipartFile file) throws IOException {
        // 1. 查询教师是否存在
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new IllegalArgumentException("教师不存在");
        }
        
        // 2. 处理文件上传
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        
        String fileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("文件名为空");
        }
        
        // 3. 如果教师已有头像，先删除旧头像
        String oldAvatarUrl = teacher.getAvatarUrl();
        if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty() && oldAvatarUrl.startsWith("http")) {
            // 尝试删除旧头像
            boolean deleted = staticResourceUtil.deleteFile(oldAvatarUrl);
            if (deleted) {
                log.info("旧头像已删除: {}", oldAvatarUrl);
            } else {
                log.warn("旧头像删除失败: {}", oldAvatarUrl);
            }
        }
        
        // 4. 上传新头像到静态资源服务器
        String avatarUrl = staticResourceUtil.uploadFile(file, "avatar");
        
        // 5. 更新教师头像URL到数据库
        teacher.setAvatarUrl(avatarUrl);
        teacher.setUpdateTime(LocalDateTime.now());
        teacherMapper.updateById(teacher);
        log.info("教师头像URL已更新: teacherId={}, url={}", teacherId, avatarUrl);
        
        // 6. 返回头像访问URL，前端可以直接使用此URL显示头像
        return avatarUrl;
    }
    
    /**
     * 更新教师头像URL（前端直接上传到静态资源服务器后调用）
     * @param teacherId 教师ID
     * @param avatarUrl 头像URL
     * @return 更新后的头像URL
     * @throws IOException 如果处理出错
     */
    @Override
    @Transactional
    public String updateAvatarUrl(Long teacherId, String avatarUrl) throws IOException {
        // 1. 查询教师是否存在
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new IllegalArgumentException("教师不存在");
        }
        
        // 2. 验证URL
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            throw new IllegalArgumentException("头像URL不能为空");
        }
        
        // 3. 如果教师已有头像，尝试删除旧头像
        String oldAvatarUrl = teacher.getAvatarUrl();
        if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty() && oldAvatarUrl.startsWith("http") 
                && !oldAvatarUrl.equals(avatarUrl)) {
            try {
                // 尝试删除旧头像
                boolean deleted = staticResourceUtil.deleteFile(oldAvatarUrl);
                if (deleted) {
                    log.info("旧头像已删除: {}", oldAvatarUrl);
                } else {
                    log.warn("旧头像删除失败: {}", oldAvatarUrl);
                }
            } catch (Exception e) {
                log.error("删除旧头像时出错", e);
                // 继续执行，不影响新头像的设置
            }
        }
        
        // 4. 更新教师头像URL到数据库
        teacher.setAvatarUrl(avatarUrl);
        teacher.setUpdateTime(LocalDateTime.now());
        teacherMapper.updateById(teacher);
        log.info("教师头像URL已更新: teacherId={}, url={}", teacherId, avatarUrl);
        
        // 5. 返回头像访问URL
        return avatarUrl;
    }
} 