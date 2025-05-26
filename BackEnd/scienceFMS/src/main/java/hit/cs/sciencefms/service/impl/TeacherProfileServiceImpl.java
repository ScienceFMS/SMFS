package hit.cs.sciencefms.service.impl;

import hit.cs.sciencefms.dto.TeacherProfileDTO;
import hit.cs.sciencefms.entity.Teacher;
import hit.cs.sciencefms.entity.TeacherEducation;
import hit.cs.sciencefms.mapper.TeacherEducationMapper;
import hit.cs.sciencefms.mapper.TeacherMapper;
import hit.cs.sciencefms.service.TeacherProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师个人资料服务实现类
 */
@Service
@RequiredArgsConstructor
public class TeacherProfileServiceImpl implements TeacherProfileService {
    
    private final TeacherMapper teacherMapper;
    private final TeacherEducationMapper teacherEducationMapper;
    
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
} 