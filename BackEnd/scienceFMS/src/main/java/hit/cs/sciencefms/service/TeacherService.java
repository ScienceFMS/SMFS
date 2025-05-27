package hit.cs.sciencefms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import hit.cs.sciencefms.entity.Teacher;

import java.util.List;

/**
 * 教师服务接口
 */
public interface TeacherService extends IService<Teacher> {
    
    /**
     * 根据ID获取教师信息
     * 
     * @param id 教师ID
     * @return 教师信息
     */
    Teacher getTeacherById(Long id);
    
    /**
     * 获取所有教师列表
     * 
     * @return 教师列表
     */
    List<Teacher> getAllTeachers();
} 