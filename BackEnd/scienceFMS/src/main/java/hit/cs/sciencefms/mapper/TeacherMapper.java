package hit.cs.sciencefms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import hit.cs.sciencefms.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 教师Mapper接口
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
    
    /**
     * 根据用户名查询教师信息
     * @param username 用户名
     * @return 教师信息
     */
    @Select("SELECT * FROM t_teacher WHERE username = #{username} AND is_deleted = 0")
    Teacher selectByUsername(@Param("username") String username);
} 