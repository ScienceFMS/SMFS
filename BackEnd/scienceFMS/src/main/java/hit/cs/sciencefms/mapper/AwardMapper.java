package hit.cs.sciencefms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import hit.cs.sciencefms.entity.Award;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 获奖信息Mapper接口
 */
@Mapper
public interface AwardMapper extends BaseMapper<Award> {

    /**
     * 根据教师ID查询获奖信息
     * @param teacherId 教师ID
     * @return 获奖信息列表
     */
    @Select("SELECT * FROM t_award WHERE teacher_id = #{teacherId} AND is_deleted = 0 ORDER BY award_date DESC")
    List<Award> findByTeacherId(@Param("teacherId") Long teacherId);
} 