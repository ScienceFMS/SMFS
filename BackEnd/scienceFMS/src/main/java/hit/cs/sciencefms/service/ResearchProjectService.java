package hit.cs.sciencefms.service;

import hit.cs.sciencefms.entity.ResearchProject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 科研项目服务接口
 */
public interface ResearchProjectService extends IService<ResearchProject> {
    
    /**
     * 根据教师ID查询科研项目列表
     * 
     * @param teacherId 教师ID
     * @return 科研项目列表
     */
    List<ResearchProject> getProjectsByTeacherId(Long teacherId);
    
    /**
     * 分页查询教师的科研项目
     * 
     * @param teacherId 教师ID
     * @param page 页码
     * @param pageSize 每页条数
     * @param status 项目状态（可选）
     * @param type 项目类型（可选）
     * @param keyword 关键词（可选）
     * @return 分页结果
     */
    Page<ResearchProject> pageProjectsByTeacherId(Long teacherId, int page, int pageSize, String status, String type, String keyword);
    
    /**
     * 新增科研项目
     * 
     * @param project 科研项目信息
     * @return 是否成功
     */
    boolean addProject(ResearchProject project);
    
    /**
     * 更新科研项目信息
     * 
     * @param project 科研项目信息
     * @return 是否成功
     */
    boolean updateProject(ResearchProject project);
    
    /**
     * 删除科研项目
     * 
     * @param id 项目ID
     * @return 是否成功
     */
    boolean deleteProject(Long id);
    
    /**
     * 根据项目ID获取项目详情
     * 
     * @param id 项目ID
     * @return 项目详情
     */
    ResearchProject getProjectById(Long id);
} 