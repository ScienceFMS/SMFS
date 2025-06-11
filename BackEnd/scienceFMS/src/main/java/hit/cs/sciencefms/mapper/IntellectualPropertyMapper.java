package hit.cs.sciencefms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import hit.cs.sciencefms.entity.IntellectualProperty;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 知识产权Mapper接口
 */
@Mapper
public interface IntellectualPropertyMapper extends BaseMapper<IntellectualProperty> {

    /**
     * 根据教师ID查询知识产权
     * @param teacherId 教师ID
     * @return 知识产权列表
     */
    @Select("SELECT * FROM t_intellectual_property WHERE teacher_id = #{teacherId} AND is_deleted = 0 ORDER BY auth_date DESC")
    List<IntellectualProperty> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 根据时间范围查询知识产权
     * @param startYear 开始年份
     * @param endYear 结束年份
     * @return 知识产权列表
     */
    @Select("SELECT * FROM t_intellectual_property WHERE EXTRACT(YEAR FROM auth_date) >= #{startYear} AND EXTRACT(YEAR FROM auth_date) <= #{endYear} AND is_deleted = 0 ORDER BY auth_date DESC")
    List<IntellectualProperty> findByTimeRange(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);
} 