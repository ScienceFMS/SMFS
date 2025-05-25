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
 * 出访记录实体类
 */
@Data
@TableName("t_visit_record")
public class VisitRecord {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 出访目的
     */
    private String purpose;
    
    /**
     * 出访地点
     */
    private String location;
    
    /**
     * 开始日期
     */
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    private LocalDate endDate;
    
    /**
     * 出访成果
     */
    private String result;
    
    /**
     * 行程单文件URL
     */
    private String itineraryFile;
    
    /**
     * 成果报告URL
     */
    private String reportFile;
    
    /**
     * 报销状态
     */
    private String reimbursementStatus;
    
    /**
     * 报销金额
     */
    private BigDecimal reimbursementAmount;
    
    /**
     * 教师ID(关联教师表)
     */
    private Long teacherId;
    
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