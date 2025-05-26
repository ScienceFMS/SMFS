package hit.cs.sciencefms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 科研项目数据传输对象
 */
@Data
public class ResearchProjectDTO {
    
    /**
     * 项目ID
     */
    private Long id;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 项目编号
     */
    private String projectCode;
    
    /**
     * 项目类型，对应source
     */
    private String projectType;
    
    /**
     * 项目状态
     */
    private String status;
    
    /**
     * 项目经费
     */
    private BigDecimal funding;
    
    /**
     * 项目开始日期
     */
    private LocalDate startDate;
    
    /**
     * 项目结束日期
     */
    private LocalDate endDate;
    
    /**
     * 项目附件
     */
    private String attachments;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 项目角色
     */
    private String role;
} 