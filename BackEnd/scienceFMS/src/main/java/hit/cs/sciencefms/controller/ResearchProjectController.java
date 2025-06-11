package hit.cs.sciencefms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.dto.ResearchProjectDTO;
import hit.cs.sciencefms.entity.ResearchProject;
import hit.cs.sciencefms.service.ResearchProjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 科研项目控制器
 */
@RestController
@RequestMapping("/projects")
public class ResearchProjectController {
    
    @Autowired
    private ResearchProjectService researchProjectService;
    
    /**
     * 根据教师ID获取项目列表
     * 
     * @param teacherId 教师ID
     * @return 项目列表
     */
    @GetMapping("/list/{teacherId}")
    public Result<List<ResearchProjectDTO>> getProjectsByTeacherId(@PathVariable("teacherId") Long teacherId) {
        List<ResearchProject> projectList = researchProjectService.getProjectsByTeacherId(teacherId);
        List<ResearchProjectDTO> dtoList = projectList.stream().map(project -> {
            ResearchProjectDTO dto = new ResearchProjectDTO();
            BeanUtils.copyProperties(project, dto);
            dto.setProjectType(project.getSource());  // source字段存储的是项目类型（国家级/省部级等）
            // 状态根据日期计算，不从数据库字段直接获取
            return dto;
        }).collect(Collectors.toList());
        
        return Result.success(dtoList);
    }
    
    /**
     * 分页查询项目
     * 
     * @param teacherId 教师ID
     * @param page 页码
     * @param pageSize 每页大小
     * @param status 状态
     * @param type 类型
     * @param keyword 关键词
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<Page<ResearchProjectDTO>> pageProjects(
            @RequestParam("teacherId") Long teacherId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        Page<ResearchProject> projectPage = researchProjectService.pageProjectsByTeacherId(
                teacherId, page, pageSize, status, type, keyword);
        
        // 转换为DTO对象
        Page<ResearchProjectDTO> dtoPage = new Page<>(projectPage.getCurrent(), projectPage.getSize(), projectPage.getTotal());
        List<ResearchProjectDTO> dtoList = projectPage.getRecords().stream().map(project -> {
            ResearchProjectDTO dto = new ResearchProjectDTO();
            BeanUtils.copyProperties(project, dto);
            dto.setProjectType(project.getSource());  // source字段存储的是项目类型
            // 状态根据日期计算，在前端实现
            return dto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtoList);
        
        return Result.success(dtoPage);
    }
    
    /**
     * 获取项目详情
     * 
     * @param id 项目ID
     * @return 项目详情
     */
    @GetMapping("/{id}")
    public Result<ResearchProjectDTO> getProject(@PathVariable("id") Long id) {
        ResearchProject project = researchProjectService.getProjectById(id);
        if (project == null) {
            return Result.fail("项目不存在");
        }
        
        ResearchProjectDTO dto = new ResearchProjectDTO();
        BeanUtils.copyProperties(project, dto);
        dto.setProjectType(project.getSource());
        // 状态根据日期计算，在前端实现
        
        return Result.success(dto);
    }
    
    /**
     * 新增项目
     * 
     * @param projectDTO 项目信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<ResearchProjectDTO> addProject(@RequestBody ResearchProjectDTO projectDTO) {
        ResearchProject project = new ResearchProject();
        BeanUtils.copyProperties(projectDTO, project);
        // 特殊字段处理
        project.setSource(projectDTO.getProjectType()); // 项目类型保存到source字段
        // 不再保存status字段，由前端根据日期计算
        
        // 设置创建和更新时间
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        project.setIsDeleted(0);
        
        boolean success = researchProjectService.addProject(project);
        if (success) {
            // 返回创建后的项目信息
            projectDTO.setId(project.getId());
            return Result.success(projectDTO);
        } else {
            return Result.fail("添加失败");
        }
    }
    
    /**
     * 更新项目
     * 
     * @param projectDTO 项目信息
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<Boolean> updateProject(@RequestBody ResearchProjectDTO projectDTO) {
        ResearchProject project = researchProjectService.getProjectById(projectDTO.getId());
        if (project == null) {
            return Result.fail("项目不存在");
        }
        
        BeanUtils.copyProperties(projectDTO, project);
        project.setSource(projectDTO.getProjectType());
        // 不再保存status字段，由前端根据日期计算
        
        // 设置更新时间
        project.setUpdateTime(LocalDateTime.now());
        
        boolean success = researchProjectService.updateProject(project);
        return success ? Result.success(true) : Result.fail("更新失败");
    }
    
    /**
     * 删除项目
     * 
     * @param id 项目ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteProject(@PathVariable("id") Long id) {
        // 获取项目信息，以便处理附件删除（如果需要在服务端处理）
        ResearchProject project = researchProjectService.getProjectById(id);
        if (project == null) {
            return Result.fail("项目不存在");
        }
        
        boolean success = researchProjectService.deleteProject(id);
        return success ? Result.success(true) : Result.fail("删除失败");
    }
} 