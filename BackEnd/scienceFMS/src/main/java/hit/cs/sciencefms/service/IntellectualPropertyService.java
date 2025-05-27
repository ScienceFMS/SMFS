package hit.cs.sciencefms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import hit.cs.sciencefms.entity.IntellectualProperty;

import java.util.List;

/**
 * 知识产权服务接口
 */
public interface IntellectualPropertyService extends IService<IntellectualProperty> {
    
    /**
     * 根据教师ID获取所有知识产权
     * 
     * @param teacherId 教师ID
     * @return 知识产权列表
     */
    List<IntellectualProperty> getPropertiesByTeacherId(Long teacherId);
    
    /**
     * 分页查询教师的知识产权
     * 
     * @param teacherId 教师ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param type 类型筛选（可选，专利/著作权）
     * @param keyword 关键词搜索
     * @return 分页结果
     */
    Page<IntellectualProperty> pagePropertiesByTeacherId(Long teacherId, int page, int pageSize, String type, String keyword);
    
    /**
     * 添加知识产权
     * 
     * @param property 知识产权信息
     * @return 是否成功
     */
    boolean addProperty(IntellectualProperty property);
    
    /**
     * 更新知识产权
     * 
     * @param property 知识产权信息
     * @return 是否成功
     */
    boolean updateProperty(IntellectualProperty property);
    
    /**
     * 删除知识产权
     * 
     * @param id 知识产权ID
     * @return 是否成功
     */
    boolean deleteProperty(Long id);
    
    /**
     * 根据ID获取知识产权
     * 
     * @param id 知识产权ID
     * @return 知识产权信息
     */
    IntellectualProperty getPropertyById(Long id);
} 