package hit.cs.sciencefms.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 出访记录数据传输对象
 */
@Data
public class VisitRecordDTO {
    
    /**
     * 主键ID
     */
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
     * 教师ID
     */
    private Long teacherId;
} 