package hit.cs.sciencefms.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 知识产权数据传输对象
 */
@Data
public class IntellectualPropertyDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 名称
     */
    private String title;
    
    /**
     * 类型(专利/著作权) PATENT/COPYRIGHT
     */
    private String type;
    
    /**
     * 子类型(如发明专利、实用新型专利等)
     */
    private String subtype;
    
    /**
     * 登记/授权号
     */
    private String authNumber;
    
    /**
     * 申请日期（专利=申请日，著作权=登记日）
     */
    private LocalDate applyDate;
    
    /**
     * 授权日期
     */
    private LocalDate authDate;
    
    /**
     * 发明人排名（专利时有效）
     */
    private Integer inventorRank;
    
    /**
     * 教师ID
     */
    private Long teacherId;
} 