package hit.cs.sciencefms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import hit.cs.sciencefms.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 增加登录失败次数
     * @param username 用户名
     * @return 影响行数
     */
    @Update("UPDATE t_user SET login_fail_count = login_fail_count + 1 WHERE username = #{username}")
    int incrementLoginFailCount(@Param("username") String username);
    
    /**
     * 重置登录失败次数
     * @param username 用户名
     * @return 影响行数
     */
    @Update("UPDATE t_user SET login_fail_count = 0 WHERE username = #{username}")
    int resetLoginFailCount(@Param("username") String username);
    
    /**
     * 锁定用户账号
     * @param username 用户名
     * @return 影响行数
     */
    @Update("UPDATE t_user SET status = 1 WHERE username = #{username}")
    int lockUserAccount(@Param("username") String username);
} 