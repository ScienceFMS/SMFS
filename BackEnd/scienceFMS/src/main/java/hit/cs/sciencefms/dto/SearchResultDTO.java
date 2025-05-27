package hit.cs.sciencefms.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 多维查询结果DTO
 * 整合了不同类型的科研成果，用于统一展示
 */
@Data
public class SearchResultDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 实体类型（intellectualProperty/award/researchProject）
     */
    private String entityType;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 教师姓名（冗余，便于前端展示）
     */
    private String teacherName;
    
    /**
     * 教师部门
     */
    private String department;
    
    /**
     * 标题/名称（对应各实体的主要名称字段）
     */
    private String title;
    
    /**
     * 日期（用于排序的主要日期）
     */
    private LocalDate mainDate;
    
    /**
     * 详细类型，如专利、著作权、国家级奖项等
     */
    private String type;
    
    /**
     * 子类型，如发明专利、软件著作权等
     */
    private String subtype;
    
    /**
     * 相关日期1（如专利申请日期、项目开始日期等）
     */
    private LocalDate date1;
    
    /**
     * 相关日期2（如专利授权日期、项目结束日期等）
     */
    private LocalDate date2;
    
    /**
     * 相关金额（如项目经费）
     */
    private BigDecimal amount;
    
    /**
     * 相关单位（如颁发单位）
     */
    private String organization;
    
    /**
     * 级别（如获奖等级）
     */
    private String level;
    
    /**
     * 编号（如项目编号、专利号）
     */
    private String code;
    
    /**
     * 角色（如项目负责人/参与者）
     */
    private String role;
} 