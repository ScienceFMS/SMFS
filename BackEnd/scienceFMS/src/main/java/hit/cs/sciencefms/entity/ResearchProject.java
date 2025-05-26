package hit.cs.sciencefms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 科研项目实体类
 */
@Data
@TableName("t_research_project")
public class ResearchProject {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 项目来源，国家级、省级、横向等等
     */
    private String source;
    
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
     * 项目附件(存储URL，多个以逗号分隔)
     */
    private String attachments;
    
    /**
     * 教师ID(关联教师表)
     */
    private Long teacherId;
    
    /**
     * 项目角色(如：负责人、参与者)
     */
    private String role;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否删除(0-未删除，1-已删除)
     */
    @TableLogic
    private Integer isDeleted;
} 