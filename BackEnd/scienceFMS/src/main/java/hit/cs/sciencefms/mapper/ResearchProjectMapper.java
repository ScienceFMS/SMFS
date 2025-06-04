package hit.cs.sciencefms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import hit.cs.sciencefms.entity.ResearchProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 科研项目Mapper接口
 */
@Mapper
public interface ResearchProjectMapper extends BaseMapper<ResearchProject> {

    /**
     * 根据教师ID查询科研项目
     * @param teacherId 教师ID
     * @return 科研项目列表
     */
    @Select("SELECT * FROM t_research_project WHERE teacher_id = #{teacherId} AND is_deleted = 0 ORDER BY start_date DESC")
    List<ResearchProject> findByTeacherId(@Param("teacherId") Long teacherId);
} 