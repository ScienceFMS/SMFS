package hit.cs.sciencefms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hit.cs.sciencefms.entity.Teacher;
import hit.cs.sciencefms.mapper.TeacherMapper;
import hit.cs.sciencefms.service.TeacherService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教师服务实现类
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    
    @Override
    public Teacher getTeacherById(Long id) {
        return this.getById(id);
    }
    
    @Override
    public List<Teacher> getAllTeachers() {
        return this.list();
    }
} 