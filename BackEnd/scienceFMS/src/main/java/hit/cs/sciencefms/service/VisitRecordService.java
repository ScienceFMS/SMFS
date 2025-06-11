package hit.cs.sciencefms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import hit.cs.sciencefms.entity.VisitRecord;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

/**
 * 出访记录服务接口
 */
public interface VisitRecordService extends IService<VisitRecord> {
    
    /**
     * 根据教师ID获取所有出访记录
     * 
     * @param teacherId 教师ID
     * @return 出访记录列表
     */
    List<VisitRecord> getVisitRecordsByTeacherId(Long teacherId);
    
    /**
     * 分页查询教师的出访记录
     * 
     * @param teacherId 教师ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param year 年份筛选（可选）
     * @param keyword 关键词搜索
     * @return 分页结果
     */
    Page<VisitRecord> pageVisitRecordsByTeacherId(Long teacherId, int page, int pageSize, Integer year, String keyword);
    
    /**
     * 添加出访记录
     * 
     * @param visitRecord 出访记录
     * @return 是否成功
     */
    boolean addVisitRecord(VisitRecord visitRecord);
    
    /**
     * 更新出访记录
     * 
     * @param visitRecord 出访记录
     * @return 是否成功
     */
    boolean updateVisitRecord(VisitRecord visitRecord);
    
    /**
     * 删除出访记录
     * 
     * @param id 出访记录ID
     * @return 是否成功
     */
    boolean deleteVisitRecord(Long id);
    
    /**
     * 根据ID获取出访记录
     * 
     * @param id 出访记录ID
     * @return 出访记录
     */
    VisitRecord getVisitRecordById(Long id);
    
    /**
     * 生成报销模板
     * 
     * @param visitRecordId 出访记录ID
     * @return 报销模板资源
     */
    Resource generateReimbursementTemplate(Long visitRecordId) throws IOException;
    
    /**
     * 统计教师年度出访次数
     * 
     * @param teacherId 教师ID
     * @param year 年份
     * @return 出访次数
     */
    int countVisitsByYearAndTeacherId(Long teacherId, int year);
} 