package hit.cs.sciencefms.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * 教师个人资料数据传输对象
 */
@Data
public class TeacherProfileDTO {
    
    /**
     * 教师基本信息
     */
    private Long id;
    private String username;
    private String realName;
    private String gender;
    private LocalDate birthDate;
    private String title;
    private String department;
    private String position;
    private String researchArea;
    private String email;
    private String phone;
    private String officeLocation;
    private String avatarUrl;
    
    /**
     * 教育背景
     */
    private List<EducationDTO> educationList;
    
    /**
     * 教育经历DTO
     */
    @Data
    public static class EducationDTO {
        private Long id;
        private String degree;
        private String institution;
        private String major;
        private Integer startYear;
        private Integer endYear;
        
        // 前端显示用，组合起止年份
        public String getPeriod() {
            return startYear + "-" + endYear;
        }
    }
} 