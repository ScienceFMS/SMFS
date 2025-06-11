package hit.cs.sciencefms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import hit.cs.sciencefms.entity.Award;
import java.util.List;

/**
 * 获奖信息服务接口
 */
public interface AwardService extends IService<Award> {
    
    /**
     * 根据教师ID获取所有获奖信息
     * 
     * @param teacherId 教师ID
     * @return 获奖信息列表
     */
    List<Award> getAwardsByTeacherId(Long teacherId);
    
    /**
     * 分页查询教师的获奖信息
     * 
     * @param teacherId 教师ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param year 年份筛选（可选）
     * @param keyword 关键词搜索
     * @return 分页结果
     */
    Page<Award> pageAwardsByTeacherId(Long teacherId, int page, int pageSize, Integer year, String keyword);
    
    /**
     * 添加获奖信息
     * 
     * @param award 获奖信息
     * @return 是否成功
     */
    boolean addAward(Award award);
    
    /**
     * 更新获奖信息
     * 
     * @param award 获奖信息
     * @return 是否成功
     */
    boolean updateAward(Award award);
    
    /**
     * 删除获奖信息
     * 
     * @param id 获奖信息ID
     * @return 是否成功
     */
    boolean deleteAward(Long id);
    
    /**
     * 根据ID获取获奖信息
     * 
     * @param id 获奖信息ID
     * @return 获奖信息
     */
    Award getAwardById(Long id);
} 