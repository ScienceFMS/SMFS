package hit.cs.sciencefms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 知识产权(专利/著作权)实体类
 */
@Data
@TableName("t_intellectual_property")
public class IntellectualProperty {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * // 专利时：发明/实用新型/外观设计  
     * // 著作权时：文字/软件/美术等
     */
    private String subtype;
    
    /**
     * 登记/授权号（通用化命名）
     */
    private String authNumber;
    
    /**
     * 申请日期（专利=申请日，著作权=登记日，非必填）
     */
    private LocalDate applyDate;
    
    /**
     * 授权日期（专利=授权日，著作权=登记日，非必填）
     */
    private LocalDate authDate;
    
    /**
     * 发明人排名（专利时，发明人排名，非必填）
     */
    private Integer inventorRank;
    
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