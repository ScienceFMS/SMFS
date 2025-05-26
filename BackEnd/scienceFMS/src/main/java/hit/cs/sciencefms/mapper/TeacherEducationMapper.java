package hit.cs.sciencefms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import hit.cs.sciencefms.entity.TeacherEducation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 教师教育背景Mapper接口
 */
@Mapper
public interface TeacherEducationMapper extends BaseMapper<TeacherEducation> {
    
    /**
     * 根据教师ID查询教育背景列表，按照结束年份降序排列
     * @param teacherId 教师ID
     * @return 教育背景列表
     */
    @Select("SELECT * FROM t_teacher_education WHERE teacher_id = #{teacherId} AND is_deleted = 0 ORDER BY end_year DESC")
    List<TeacherEducation> selectByTeacherId(@Param("teacherId") Long teacherId);
} 