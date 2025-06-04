package hit.cs.sciencefms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import hit.cs.sciencefms.entity.VisitRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 出访记录Mapper接口
 */
@Mapper
public interface VisitRecordMapper extends BaseMapper<VisitRecord> {

    /**
     * 根据教师ID查询出访记录
     * @param teacherId 教师ID
     * @return 出访记录列表
     */
    @Select("SELECT * FROM t_visit_record WHERE teacher_id = #{teacherId} AND is_deleted = 0 ORDER BY start_date DESC")
    List<VisitRecord> findByTeacherId(@Param("teacherId") Long teacherId);
} 