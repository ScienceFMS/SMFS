package hit.cs.sciencefms.controller;

import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.entity.Teacher;
import hit.cs.sciencefms.service.TeacherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 教师信息控制器
 */
@RestController
@RequestMapping("/admin/teachers")
public class TeacherController {
    
    @Resource
    private TeacherService teacherService;
    
    /**
     * 获取所有教师列表
     * 
     * @return 教师列表
     */
    @GetMapping
    public Result<List<Teacher>> getAllTeachers() {
        List<Teacher> teachers = teacherService.getAllTeachers();
        return Result.success(teachers);
    }
    
    /**
     * 根据ID获取教师信息
     * 
     * @param id 教师ID
     * @return 教师信息
     */
    @GetMapping("/{id}")
    public Result<Teacher> getTeacherById(@PathVariable("id") Long id) {
        Teacher teacher = teacherService.getTeacherById(id);
        if (teacher == null) {
            return Result.fail("教师不存在");
        }
        return Result.success(teacher);
    }
} 