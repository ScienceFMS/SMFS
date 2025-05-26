package hit.cs.sciencefms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 获奖信息实体类
 */
@Data
@TableName("t_award")
public class Award {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 相关项目ID，这一项我们先不考虑，因为教师录入的时候，可能没有相关项目，所以先不考虑
     */
    private Long relatedProjectId;
    
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