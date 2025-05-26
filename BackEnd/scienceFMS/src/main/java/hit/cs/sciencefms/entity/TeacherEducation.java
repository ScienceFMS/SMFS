package hit.cs.sciencefms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教师教育背景实体类
 */
@Data
@TableName("t_teacher_education")
public class TeacherEducation {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 教师ID(关联教师表)
     */
    private Long teacherId;
    
    /**
     * 学位
     */
    private String degree;
    
    /**
     * 院校
     */
    private String institution;
    
    /**
     * 专业
     */
    private String major;
    
    /**
     * 开始年份
     */
    private Integer startYear;
    
    /**
     * 结束年份
     */
    private Integer endYear;
    
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