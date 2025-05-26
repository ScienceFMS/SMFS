package hit.cs.sciencefms.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 获奖信息数据传输对象
 */
@Data
public class AwardDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 奖项名称
     */
    private String awardName;
    
    /**
     * 获奖等级
     */
    private String awardLevel;
    
    /**
     * 颁发单位
     */
    private String issuingUnit;
    
    /**
     * 获奖日期
     */
    private LocalDate awardDate;
    
    /**
     * 证书图片URL
     */
    private String certificateImage;
    
    /**
     * 相关项目ID
     */
    private Long relatedProjectId;
    
    /**
     * 教师ID
     */
    private Long teacherId;
} 